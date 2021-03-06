package com.example.fy071.floatingwidget.pet;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.bluetooth.PairingActivity;
import com.example.fy071.floatingwidget.reminder.ReminderConfigActivity;
import com.example.fy071.floatingwidget.reminder.ReminderListActivity;
import com.example.fy071.floatingwidget.settings.SettingsActivity;
import com.example.fy071.floatingwidget.util.Key;
import com.example.fy071.floatingwidget.util.NotificationChannelsManager;
import com.example.fy071.floatingwidget.util.PxDpConverter;
import com.ramotion.circlemenu.CircleMenuView;

import static java.lang.Math.abs;

public class FloatingViewService extends Service {
    private static final String TAG = "FloatingViewService";

    public static final int BUTTON_OFF = 0;
    public static final int BUTTON_REMINDER = 1;
    public static final int BUTTON_NEW_REMINDER = 2;
    public static final int BUTTON_SETTINGS = 3;
    public static final int BUTTON_PAIRING = 4;

    private static final int DIFFER = 5;//判断是否为点击操作

    private static final int TO_SIDE = 100;//判断是否需要贴边,单位为dp

    private static final int MESSAGE_DURATION = 1000;//毫秒

    private static final float RATIO = (float) 1.75;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable checkMessage;
    private Toast toast;
    private DisplayMetrics dm;


    private ViewGroup virtualParent;

    private View view;// 透明窗体
    private View menuView;// 菜单窗体

    private ImageView petModel;
    private ImageView virtualPetModel;

    private View message_view;
    private TextView tvMessage;
    private int statusBarHeight;

    private CircleMenuView circleMenuView;

    private boolean viewAdded = false;// 透明窗体是否已经显示
    private boolean circlemenuAdded = false;//环形菜单
    private boolean virtualViewAdded = false;//父窗口

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager.LayoutParams virtualLayoutParams;
    private WindowManager.LayoutParams centerLayoutParams;
    private RelativeLayout.LayoutParams relativeParams;

    private SharedPreferences sharedPreferences;

    private Intent intent;
    private WeChatMessageReceiver receiver;

    private static int layoutID = R.layout.layout_pet_1;
    private static int downAnimeID = R.drawable.down_anime_1;
    private static int upFrameID = R.drawable.emoji_1_0;
    private static int upAnimeID = R.drawable.up_anime_1;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        receiver = new WeChatMessageReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.tofloatingpet.message");
        registerReceiver(receiver, intentFilter);
        setModel();
        createFloatView();
        refresh();
        startForeground(this);
        checkMessage = new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                sendMessage();
                mHandler.postDelayed(checkMessage, MESSAGE_DURATION * 2);
            }
        };
        mHandler.post(checkMessage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeView();
        unregisterReceiver(receiver);
        stopForeground(true);
    }

    public void startForeground(Service context) {
        Notification.Builder builder = new Notification.Builder(this)
                .setContentText("I'm running.")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("pet")
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NotificationChannelsManager.FOREGROUND_SERVICE_CHANNEL);
        }

        context.startForeground(8888, builder.build());
    }

    /**
     * 关闭悬浮窗
     */
    public void removeView() {
        if (viewAdded) {
            windowManager.removeView(view);
            viewAdded = false;
        }
    }

    private void createFloatView() {
        statusBarHeight = 0;
        toast = new Toast(this);
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        setTheme(R.style.AppTheme);


        view = LayoutInflater.from(this).inflate(layoutID, null);
        petModel = view.findViewById(R.id.imageView_pet);
        setInitFrame();
        virtualParent = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.layout_virtualparent, null);
        virtualPetModel = virtualParent.findViewById(R.id.imageView_pet);
        relativeParams = new RelativeLayout.LayoutParams(0, 0);

        menuView = LayoutInflater.from(this).inflate(R.layout.popup_menu, null);
        circleMenuView = menuView.findViewById(R.id.circle_menu);

        message_view = LayoutInflater.from(this).inflate(R.layout.layout_message, null);
        tvMessage = message_view.findViewById(R.id.message_view);

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        initLayoutParams();

        //悬浮窗开始在左上角显示
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.windowAnimations = android.R.style.Animation_Toast;
        virtualLayoutParams.gravity = Gravity.START | Gravity.TOP;
        virtualLayoutParams.windowAnimations = android.R.style.Animation_Toast;
        centerLayoutParams.gravity = Gravity.CENTER;
        view.setOnTouchListener(new FloatingTouchListener());
        view.setOnClickListener(new FloatingClickListener());
        circleMenuView.setEventListener(new CircleMenuView.EventListener() {
            @Override
            public void onMenuCloseAnimationEnd(@NonNull CircleMenuView v) {
                Log.d(TAG, "onMenuCloseAnimationEnd: ");
                windowManager.removeView(menuView);
                circlemenuAdded = false;
                refresh();
            }
            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int index) {
                switch (index) {
                    case BUTTON_OFF:
                        intent = null;
                        stopSelf();
                        break;
                    case BUTTON_REMINDER:
                        intent = new Intent(FloatingViewService.this, ReminderListActivity.class);
                        break;
                    case BUTTON_NEW_REMINDER:
                        intent = new Intent(FloatingViewService.this, ReminderConfigActivity.class);
                        break;
                    case BUTTON_SETTINGS:
                        intent = new Intent(FloatingViewService.this, SettingsActivity.class);
                        break;
                    case BUTTON_PAIRING:
                        intent = new Intent(FloatingViewService.this, PairingActivity.class);
                        break;
                    default:
                        intent = null;
                }
                windowManager.removeView(menuView);
                circlemenuAdded = false;
                refresh();
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
        refresh();
    }

    /**
     * 刷新悬浮窗
     *
     * @param x 拖动后的X轴坐标
     * @param y 拖动后的Y轴坐标
     */
    private void refreshView(float x, float y) {
        layoutParams.x = (int) x;
        layoutParams.y = (int) y - statusBarHeight;
        refresh();
    }

    private void refreshView2(float xLast, float yLast, final float xNext, float yNext) {

        yLast = yLast - statusBarHeight;
        yNext = yNext - statusBarHeight;
        layoutParams.x = (int) xNext;
        layoutParams.y = (int) yNext;
        //virtualLayoutParams.windowAnimations = 0;
        //windowManager.addView(virtualParent, virtualLayoutParams);
        //virtualLayoutParams.windowAnimations = android.R.style.Animation_Dialog;
        //windowManager.updateViewLayout(virtualParent, virtualLayoutParams);
        // virtualViewAdded = true;
        // AnimationSet animationSet = new AnimationSet(true);
        //参数1～2：x轴的开始位置
        //参数3～4：y轴的开始位置
        //参数5～6：x轴的结束位置
        //参数7～8：x轴的结束位置
        /*TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, xLast,
                Animation.ABSOLUTE, xNext,
                Animation.ABSOLUTE, yLast,
                Animation.ABSOLUTE, yNext
        );
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(300);

        virtualPetModel.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                windowManager.removeView(view);
                viewAdded = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub

                //layoutParams.windowAnimations = 0;
                windowManager.addView(view, layoutParams);
                viewAdded = true;
                //layoutParams.windowAnimations = android.R.style.Animation_Dialog;
                //windowManager.updateViewLayout(view, layoutParams);
                windowManager.removeView(virtualParent);
                virtualViewAdded = false;
            }
        });
        */
        windowManager.removeView(view);
        windowManager.addView(view, layoutParams);
    }

    /**
     * 添加悬浮窗或者更新悬浮窗 如果悬浮窗还没添加则添加 如果已经添加则更新其位置
     */
    private void refresh() {
        // 如果已经添加了就只更新view
        if (viewAdded) {
            windowManager.updateViewLayout(view, layoutParams);
        } else {
            windowManager.addView(view, layoutParams);
            viewAdded = true;
        }
    }

    private void setDownAnim() {
        petModel.setImageResource(downAnimeID);
    }

    private void setInitFrame() {
        petModel.setImageResource(upFrameID);
    }


    private void setUpAnime() {
        petModel.setImageResource(upAnimeID);
    }


    class FloatingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            removeView();
            windowManager.addView(menuView, centerLayoutParams);
            circlemenuAdded = true;
            circleMenuView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    circleMenuView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    circleMenuView.open(true);
                }
            });
        }
    }

    /*悬浮窗监听器*/
    class FloatingTouchListener implements View.OnTouchListener {
        float fingerStartX, fingerStartY;
        float ScreenStartX, ScreenStartY;

        public boolean onTouch(View v, MotionEvent event) {
            AnimationDrawable animationDrawable;
            int eventAction = event.getAction();
            switch (eventAction) {
                case MotionEvent.ACTION_DOWN: // 按下事件，记录按下时手指在悬浮窗的XY坐标值
                    setDownAnim();
                    animationDrawable = (AnimationDrawable) petModel.getDrawable();
                    if (!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                    fingerStartX = event.getX();
                    fingerStartY = event.getY();
                    ScreenStartX = event.getRawX();
                    ScreenStartY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    refreshView(event.getRawX() - fingerStartX, event.getRawY() - fingerStartY);
                    break;
                case MotionEvent.ACTION_UP:
                    setUpAnime();
                    animationDrawable = (AnimationDrawable) petModel.getDrawable();
                    if (!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                    setInitFrame();

                    float ScreenEndX = event.getRawX();
                    float ScreenEndY = event.getRawY();
                    float left = event.getRawX() - fingerStartX;

                    float right = dm.widthPixels - left - v.getWidth();
                    if (abs(ScreenEndX - ScreenStartX) < DIFFER && abs(ScreenEndY - ScreenStartY) < DIFFER) {
                        refreshView(ScreenStartX - fingerStartX, ScreenStartY - fingerStartY);
                        v.performClick();
                    } else {
                        if (PxDpConverter.convertPixelsToDp(left) < TO_SIDE) {
                            refreshView2(
                                    event.getRawX() - fingerStartX,
                                    event.getRawY() - fingerStartY,
                                    0,
                                    event.getRawY() - fingerStartY
                            );
                        } else if (PxDpConverter.convertPixelsToDp(right) < TO_SIDE) {
                            refreshView2(
                                    event.getRawX() - fingerStartX,
                                    event.getRawY() - fingerStartY,
                                    dm.widthPixels - v.getWidth(),
                                    event.getRawY() - fingerStartY);
                        } else {
                            refreshView(ScreenEndX - fingerStartX, ScreenEndY - fingerStartY);
                        }
                    }
            }
            return true;
        }
    }

    private void sendMessage() {
        if (viewAdded && receiver.message.size() > 0) {

            tvMessage.setText(receiver.message.elementAt(0).toString());

            float xOff = layoutParams.x + view.getWidth() / 2;
            float yOff = layoutParams.y;

            if (xOff > dm.widthPixels / 2) {
                float tvMessageWidth = countStringLength(tvMessage.getText().toString()) * tvMessage.getTextSize() / RATIO;
                if (tvMessageWidth > layoutParams.x)
                    tvMessageWidth = layoutParams.x;
                if (tvMessageWidth > dm.widthPixels / 2)
                    tvMessageWidth = dm.widthPixels / 2;
                tvMessage.setMaxWidth((int) tvMessageWidth);
                xOff = xOff - view.getWidth() / 2 - tvMessageWidth;
            } else {
                xOff += view.getWidth() / 2;
                float tvMessageWidth = dm.widthPixels - xOff;
                if (tvMessageWidth > dm.widthPixels / 2)
                    tvMessageWidth = dm.widthPixels / 2;
                tvMessage.setMaxWidth((int) tvMessageWidth);
            }
            toast.setGravity(Gravity.START | Gravity.TOP, (int) xOff, (int) yOff);
            toast.setView(message_view);
            toast.setDuration(Toast.LENGTH_SHORT);
            receiver.message.remove(0);
            toast.show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, MESSAGE_DURATION);
        }

    }

    public float countStringLength(String str) {

        float count = 0;
        for (int i = 0; i < str.length(); i++) {
            char item = str.charAt(i);
            if (item < 128) {
                count++;
            } else {
                count+= RATIO;
            }
        }
        return count;
    }

    /**
     * LayoutParams.TYPE_PHONE：保证该悬浮窗所有View的最上层
     * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
     * PixelFormat.TRANSPARENT：悬浮窗透明
     */
    private void initLayoutParams() {
        if (Build.VERSION.SDK_INT > 26) {
            layoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.TYPE_APPLICATION_OVERLAY,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
            virtualLayoutParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.TYPE_APPLICATION_OVERLAY,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
            centerLayoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.TYPE_APPLICATION_OVERLAY,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
        } else {
            layoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.TYPE_PHONE,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
            virtualLayoutParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.TYPE_PHONE,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
            centerLayoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.TYPE_PHONE,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
        }
    }

    //设置模型
    private void setModel(){
        switch(sharedPreferences.getString(Key.PET_MODEL, "")) {
            case "model_1":
                layoutID = R.layout.layout_pet_1;
                downAnimeID = R.drawable.down_anime_1;
                upFrameID = R.drawable.emoji_1_0;
                upAnimeID = R.drawable.up_anime_1;
                break;
            case "model_2":
                layoutID = R.layout.layout_pet_2;
                downAnimeID = R.drawable.down_anime_2;
                upFrameID = R.drawable.test2_1;
                upAnimeID = R.drawable.up_anime_2;
                break;
            case "model_3":
                layoutID = R.layout.layout_pet_3;
                downAnimeID = R.drawable.down_anime_3;
                upFrameID = R.drawable.test3_1;
                upAnimeID = R.drawable.up_anime_3;
                break;
            default:
                layoutID = R.layout.layout_pet_1;
                downAnimeID = R.drawable.down_anime_1;
                upFrameID = R.drawable.emoji_1_0;
                upAnimeID = R.drawable.up_anime_1;
        }
    }
}
