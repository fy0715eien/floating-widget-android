package com.example.fy071.floatingwidget.component;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
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
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.PreferenceHelper;
import com.ramotion.circlemenu.CircleMenuView;

import static java.lang.Math.abs;

public class FloatingViewService extends Service {
    private static final String TAG = "FloatingViewService";

    private static final int TO_LEFT = 1;
    private static final int TO_RIGHT = 2;
    private static final int TO_UP = 3;
    private static final int TO_BOTTOM = 4;
    private static final int UPDATE_PIC = 0x100;

    public static final int BUTTON_REMINDER = 0;
    public static final int BUTTON_SETTINGS = 1;
    public static final int BUTTON_CLOSE = 2;

    private View view;// 透明窗体
    private ViewGroup virtualParent;
    private ImageView petModel;
    private ImageView virtualPetModel;
    private View menuView;// 菜单窗体
    private int statusBarHeight;
    private CircleMenuView circleMenuView;
    private static final int DIFFER = 5;//距离
    private boolean viewAdded = false;// 透明窗体是否已经显示
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager.LayoutParams virtualLayoutParams;
    private WindowManager.LayoutParams centerLayoutParams;
    private RelativeLayout.LayoutParams relativeParams;

    private Intent intent;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
        refresh();
        startForeground(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        removeView();
        stopForeground(true);
    }


    public void startForeground(Service context) {
        String channelId = generateChannelId(context);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentText("I'm running.")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();
        context.startForeground(8888, notification);
    }


    private String generateChannelId(Service context) {
        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "floating_service";
            String channelName = getResources().getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_NONE
            );
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            channelId = "";
        }
        return channelId;
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

        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        setTheme(R.style.AppTheme);
        int layoutID;
        switch (PreferenceHelper.petModel) {
            case "model_1":
                layoutID = R.layout.layout_pet_1;
                break;
            case "model_2":
                layoutID = R.layout.layout_pet_2;
                break;
            case "model_3":
                layoutID = R.layout.layout_pet_3;
                break;
            default:
                layoutID = R.layout.layout_pet_1;
        }
        view = LayoutInflater.from(this).inflate(layoutID, null);
        petModel = view.findViewById(R.id.imageView_pet);

        virtualParent = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.layout_virtualparent, null);
        virtualPetModel = virtualParent.findViewById(R.id.imageView_pet);
        relativeParams = new RelativeLayout.LayoutParams(0, 0);

        menuView = LayoutInflater.from(this).inflate(R.layout.popup_menu, null);
        circleMenuView = menuView.findViewById(R.id.circle_menu);

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        /*
         * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
         * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
         * PixelFormat.TRANSPARENT：悬浮窗透明
         */
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
                    LayoutParams.TYPE_SYSTEM_ERROR,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
            virtualLayoutParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.TYPE_SYSTEM_ERROR,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
            centerLayoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.TYPE_SYSTEM_ERROR,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
        }
        //悬浮窗开始在左上角显示
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        //layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        virtualLayoutParams.gravity = Gravity.START | Gravity.TOP;
        centerLayoutParams.gravity = Gravity.CENTER;

        view.setOnTouchListener(new FloatingTouchListener());
        view.setOnClickListener(new FloatingClickListener());
        circleMenuView.setEventListener(new CircleMenuView.EventListener() {
            public void onMenuOpenAnimationStart(@NonNull CircleMenuView view) {
                Log.d(TAG, "onMenuOpenAnimationStart: ");
            }

            public void onMenuOpenAnimationEnd(@NonNull CircleMenuView view) {
                Log.d(TAG, "onMenuOpenAnimationEnd: ");
            }

            @Override
            public void onMenuCloseAnimationStart(@NonNull CircleMenuView v) {
                Log.d("D", "onMenuCloseAnimationStart");
            }

            @Override
            public void onMenuCloseAnimationEnd(@NonNull CircleMenuView v) {
                Log.d(TAG, "onMenuCloseAnimationEnd: ");
                windowManager.removeView(menuView);
                refresh();
            }

            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int index) {
                switch (index) {
                    case BUTTON_REMINDER:
                        intent = new Intent(FloatingViewService.this, ReminderConfigActivity.class);// TODO: 2018/4/8 change activity
                        break;
                    case BUTTON_SETTINGS:
                        intent = new Intent(FloatingViewService.this, SettingsActivity.class);
                        break;
                    case BUTTON_CLOSE:
                        stopSelf();
                        intent = null;
                        break;
                    default:
                        intent = null;
                }
                windowManager.removeView(menuView);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
        refresh();
    }

    private int getMin(float left, float right, float up, float bottom) {
        if (left <= right && left <= up && left <= bottom)
            return TO_LEFT;
        if (right <= up && right <= bottom)
            return TO_RIGHT;
        if (up <= bottom)
            return TO_UP;
        return TO_BOTTOM;
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
        layoutParams.x=(int)xNext;
        layoutParams.y=(int)yNext;
        windowManager.addView(virtualParent, virtualLayoutParams);
        AnimationSet animationSet = new AnimationSet(true);
        //参数1～2：x轴的开始位置
        //参数3～4：y轴的开始位置
        //参数5～6：x轴的结束位置
        //参数7～8：x轴的结束位置
        TranslateAnimation translateAnimation = new TranslateAnimation(
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                windowManager.addView(view,layoutParams);
                windowManager.removeView(virtualParent);
            }
        });
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
        int downAnimId;
        switch (PreferenceHelper.petModel) {
            case "model_1":
                downAnimId = R.drawable.down_anime_1;
                break;
            case "model_2":
                downAnimId = R.drawable.down_anime_2;
                break;
            case "model_3":
                downAnimId = R.drawable.down_anime_3;
                break;
            default:
                downAnimId = R.drawable.down_anime_1;
        }
        petModel.setImageResource(downAnimId);
    }


    class FloatingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            removeView();
            windowManager.addView(menuView, centerLayoutParams);
            circleMenuView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    circleMenuView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    circleMenuView.open(true);
                }
            });
        }
    }

    private void setUpAnim() {
        int upAnimId;
        switch (PreferenceHelper.petModel) {
            case "model_1":
                upAnimId = R.drawable.up_anime_1;
                break;
            case "model_2":
                upAnimId = R.drawable.up_anime_2;
                break;
            case "model_3":
                upAnimId = R.drawable.up_anime_3;
                break;
            default:
                upAnimId = R.drawable.up_anime_1;
        }
        petModel.setImageResource(upAnimId);
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
                   /* setUpAnim();
                    animationDrawable = (AnimationDrawable) petModel.getDrawable();
                    if (!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }*/
                    DisplayMetrics dm = new DisplayMetrics();
                    windowManager.getDefaultDisplay().getMetrics(dm);

                    float ScreenEndX = event.getRawX();
                    float ScreenEndY = event.getRawY();
                    float left = event.getRawX() - fingerStartX + v.getWidth() / 2;
                    float up = event.getRawY() - fingerStartY + v.getHeight() / 2;
                    float right = dm.widthPixels - left;
                    float button = dm.heightPixels - up;
                    int min = getMin(left, right, up, button);
                    if (abs(ScreenEndX - ScreenStartX) < DIFFER && abs(ScreenEndY - ScreenStartY) < DIFFER) {
                        switch (min) {
                            case TO_LEFT:
                                refreshView(0, event.getRawY() - fingerStartY);
                                break;
                            case TO_RIGHT:
                                refreshView(dm.widthPixels - v.getWidth(), event.getRawY() - fingerStartY);
                                break;
                            case TO_UP:
                                refreshView(event.getRawX() - fingerStartX, 0);
                                break;
                            case TO_BOTTOM:
                                refreshView(event.getRawX() - fingerStartX, dm.heightPixels - v.getHeight());
                                break;
                        }
                        v.performClick();
                    }
                    else {
                        switch (min) {
                            case TO_LEFT:
                                refreshView2(
                                        event.getRawX() - fingerStartX,
                                        event.getRawY() - fingerStartY,
                                        0,
                                        event.getRawY() - fingerStartY
                                );
                                break;
                            case TO_RIGHT:
                                refreshView2(
                                        event.getRawX() - fingerStartX,
                                        event.getRawY() - fingerStartY,
                                        dm.widthPixels - v.getWidth(),
                                        event.getRawY() - fingerStartY);
                                break;
                            case TO_UP:
                                refreshView2(
                                        event.getRawX() - fingerStartX,
                                        event.getRawY() - fingerStartY,
                                        event.getRawX() - fingerStartX,
                                        0);
                                break;
                            case TO_BOTTOM:
                                refreshView2(
                                        event.getRawX() - fingerStartX,
                                        event.getRawY() - fingerStartY,
                                        event.getRawX() - fingerStartX,
                                        dm.heightPixels - v.getHeight());
                                break;
                        }
                    }
            }
            return true;
        }
    }
}
