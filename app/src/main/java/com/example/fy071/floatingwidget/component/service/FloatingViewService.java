package com.example.fy071.floatingwidget.component.service;

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
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.activity.PairingActivity;
import com.example.fy071.floatingwidget.component.activity.ReminderConfigActivity;
import com.example.fy071.floatingwidget.component.activity.SettingsActivity;
import com.example.fy071.floatingwidget.util.PreferenceHelper;
import com.example.fy071.floatingwidget.util.PxDpConverter;
import com.ramotion.circlemenu.CircleMenuView;

import java.util.List;
import java.util.Vector;

import static java.lang.Math.abs;

public class FloatingViewService extends Service {
    public static final int BUTTON_REMINDER = 0;
    public static final int BUTTON_SETTINGS = 1;
    public static final int BUTTON_CLOSE = 2;
    private static final String TAG = "FloatingViewService";
    private static final int TO_LEFT = 1;
    private static final int TO_RIGHT = 2;
    private static final int TO_UP = 3;
    private static final int TO_BOTTOM = 4;
    private static final int DIFFER = 5;//距离
    private static final int MESSAGE_DURATION=1;
    private static final int MESSAGE_LENGTH=20;
    private Vector message;
    private View view;// 透明窗体
    private ViewGroup virtualParent;
    private ImageView petModel;
    private ImageView virtualPetModel;
    private View menuView;// 菜单窗体
    private int statusBarHeight;
    private CircleMenuView circleMenuView;
    private static final int TO_SIDE = 100;//距离，判断是否需要贴边,单位为px不是dp
    private boolean viewAdded = false;// 透明窗体是否已经显示
    private boolean circlemenuAdded = false;//环形菜单
    private boolean virtualViewAdded = false;//父窗口
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
        message=new Vector();

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
        setInitFrame();
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
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        virtualLayoutParams.gravity = Gravity.START | Gravity.TOP;
        virtualLayoutParams.windowAnimations = android.R.style.Animation_Dialog;
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
                circlemenuAdded = false;
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
        layoutParams.x = (int) xNext;
        layoutParams.y = (int) yNext;
        //virtualLayoutParams.windowAnimations = 0;
        windowManager.addView(virtualParent, virtualLayoutParams);
        //virtualLayoutParams.windowAnimations = android.R.style.Animation_Dialog;
        //windowManager.updateViewLayout(virtualParent, virtualLayoutParams);
        virtualViewAdded = true;
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

        layoutParams.x = (int) xNext;
        layoutParams.y = (int) yNext;
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
                sendMessage("It's me~");
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

    private void setInitFrame() {
        int upFrameId;
        switch (PreferenceHelper.petModel) {
            case "model_1":
                upFrameId = R.drawable.test1_1;
                break;
            case "model_2":
                upFrameId = R.drawable.test2_1;
                break;
            case "model_3":
                upFrameId = R.drawable.test3_1;
                break;
            default:
                upFrameId = R.drawable.test1_1;
        }
        petModel.setImageResource(upFrameId);
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
                    setUpAnim();
                    animationDrawable = (AnimationDrawable) petModel.getDrawable();
                    if (!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                    setInitFrame();
                    DisplayMetrics dm = new DisplayMetrics();
                    windowManager.getDefaultDisplay().getMetrics(dm);

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

    private void sendMessage(String msg) {
       /* while(msg.length()>MESSAGE_LENGTH)
        {
            message.add(msg.substring(0,MESSAGE_LENGTH-1));
            msg=msg.substring(MESSAGE_LENGTH,msg.length()-1);
        }
        message.add(msg);*/
        Toast.makeText(this,message.size(),Toast.LENGTH_SHORT).show();
       /* while(message.size()>0)
        {
            Toast.makeText(this,"1111",Toast.LENGTH_SHORT).show();
            message.clear();
        }*/
           /* if(viewAdded)
            {
                View message_layout = LayoutInflater.from(this).inflate(R.layout.layout_message, null);
                TextView tvMessage =  message_layout.findViewById(R.id.message_view);
                tvMessage.setText(message.get(0));

                WindowManager.LayoutParams toastLayoutParams;
                if (Build.VERSION.SDK_INT > 26) {
                    toastLayoutParams = new LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.TYPE_APPLICATION_OVERLAY,
                            LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSPARENT
                    );
                }
                else {
                    toastLayoutParams = new LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.TYPE_SYSTEM_ERROR,
                            LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSPARENT
                    );
                }
                int[] location=new int[2];
                view.getLocationOnScreen(location);

                float xOff=location[0]+view.getWidth()/2-centerLayoutParams.x;
                float yOff=location[1]+view.getHeight()/2-centerLayoutParams.y;
                if(xOff>0)
                {
                    xOff-=view.getWidth()/2+message_layout.getWidth();
                }
                else
                {
                    xOff+=view.getWidth()/2;
                }
                tvMessage.setTextColor(0x7fffffff);
                tvMessage.setBackgroundColor(0x00000000);
                Toast toast=new Toast(this);
                toast.setGravity(Gravity.CENTER,(int)xOff,(int)yOff);
                toast.setView(message_layout);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                windowManager.addView(message_layout,toastLayoutParams);
                message.remove(0);
            }
        }*/
    }

}