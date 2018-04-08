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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

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
    private ImageView petModel;
    private View menuView;// 菜单窗体
    private int statusBarHeight;
    private CircleMenuView circleMenuView;
    private static final int DIFFER = 5;//距离
    private HandlerUI handler = null;
    Thread updateThread = null;
    private boolean viewAdded = false;// 透明窗体是否已经显示
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager.LayoutParams centerLayoutParams;

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
        handler = new HandlerUI();
        UpdateUI update = new UpdateUI();
        updateThread = new Thread(update);
        updateThread.start();
        setTheme(R.style.AppTheme);
        int layoutID;
        switch(PreferenceHelper.petModel){
            case "model_1":layoutID=R.layout.layout_pet_1;break;
            case "model_2":layoutID=R.layout.layout_pet_2;break;
            case "model_3":layoutID=R.layout.layout_pet_3;break;
            default:layoutID=R.layout.layout_pet_1;
        }
        view = LayoutInflater.from(this).inflate(layoutID, null);
        petModel=view.findViewById(R.id.imageView_pet);
        menuView =LayoutInflater.from(this).inflate(R.layout.popup_menu, null);
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
        layoutParams.windowAnimations=android.R.style.Animation_Translucent;
        centerLayoutParams.gravity=Gravity.CENTER;

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
                        intent=new Intent(FloatingViewService.this,ReminderConfigActivity.class);// TODO: 2018/4/8 change activity
                        break;
                    case BUTTON_SETTINGS:
                        intent=new Intent(FloatingViewService.this,SettingsActivity.class);
                        break;
                    case BUTTON_CLOSE:
                        stopSelf();
                        break;
                    default:
                }
                windowManager.removeView(menuView);
            }
        });
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
    private void refreshView2(float xlast,float ylast,float xnext, float ynext) {
        ylast=ylast-statusBarHeight;
        ynext=ynext-statusBarHeight;
        WindowManager.LayoutParams lp=layoutParams;
        float xadd=(xnext-xlast)/100,yadd=(ynext-ylast)/100;
        for(int i=1;i<100;i++)
        {
            lp.x=(int)(xlast+i*xadd);
            lp.y=(int)(ylast+i*yadd);
            windowManager.updateViewLayout(view,lp);
            try{
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        }
        layoutParams.x = (int) xnext;
        layoutParams.y = (int) ynext;
        windowManager.updateViewLayout(view, layoutParams);
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

    /**
     * 接受消息和处理消息
     *
     * @author Administrator
     */
    class HandlerUI extends Handler {
        public HandlerUI() {

        }
    }

    /**
     * 更新悬浮窗的信息
     *
     * @author Administrator
     */
    class UpdateUI implements Runnable {
        @Override
        public void run() {
            // 如果没有中断就一直运行
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = handler.obtainMessage();
                msg.what = UPDATE_PIC; // 设置消息标识
                handler.sendMessage(msg);
                // 休眠1s
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /*悬浮窗监听器*/
    class FloatingTouchListener implements View.OnTouchListener {
        float fingerStartX, fingerStartY;
        float ScreenStartX,ScreenStartY;
        public boolean onTouch(View v, MotionEvent event) {
            AnimationDrawable animationDrawable;
            int eventAction = event.getAction();
            switch (eventAction) {
                case MotionEvent.ACTION_DOWN: // 按下事件，记录按下时手指在悬浮窗的XY坐标值
                    int downAnimeID;
                    switch(PreferenceHelper.petModel){
                        case "model_1":downAnimeID=R.drawable.down_anime_1;break;
                        case "model_2":downAnimeID=R.drawable.down_anime_2;break;
                        case "model_3":downAnimeID=R.drawable.down_anime_3;break;
                        default:downAnimeID=R.drawable.down_anime_1;
                    }
                    petModel.setImageResource(downAnimeID);
                    animationDrawable=(AnimationDrawable)petModel.getDrawable();
                    if(!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                    fingerStartX = event.getX();
                    fingerStartY = event.getY();
                    ScreenStartX= event.getRawX();
                    ScreenStartY= event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    refreshView(event.getRawX() - fingerStartX, event.getRawY() - fingerStartY);
                    break;
                case MotionEvent.ACTION_UP:
                    DisplayMetrics dm = new DisplayMetrics();
                    windowManager.getDefaultDisplay().getMetrics(dm);

                    float ScreenEndX=event.getRawX();
                    float ScreenEndY=event.getRawY();
                    float left = event.getRawX() - fingerStartX + v.getWidth() / 2;
                    float up = event.getRawY() - fingerStartY + v.getHeight() / 2;
                    float right = dm.widthPixels - left;
                    float button = dm.heightPixels - up;

                    int min = getMin(left, right, up, button);

                    switch (min) {
                        case TO_LEFT:
                            refreshView2(event.getRawX() - fingerStartX,event.getRawY() - fingerStartY,0, event.getRawY() - fingerStartY);
                            break;
                        case TO_RIGHT:
                            refreshView2(event.getRawX() - fingerStartX,event.getRawY() - fingerStartY,dm.widthPixels - v.getWidth(), event.getRawY() - fingerStartY);
                            break;
                        case TO_UP:
                            refreshView2(event.getRawX() - fingerStartX,event.getRawY() - fingerStartY,event.getRawX() - fingerStartX, 0);
                            break;
                        case TO_BOTTOM:
                            refreshView2(event.getRawX() - fingerStartX,event.getRawY() - fingerStartY,event.getRawX() - fingerStartX, dm.heightPixels - v.getHeight());
                            break;
                    }
                    int upAnimeID;
                    switch(PreferenceHelper.petModel){
                        case "model_1":upAnimeID=R.drawable.up_anime_1;break;
                        case "model_2":upAnimeID=R.drawable.up_anime_2;break;
                        case "model_3":upAnimeID=R.drawable.up_anime_3;break;
                        default:upAnimeID=R.drawable.up_anime_1;
                    }
                    petModel.setImageResource(upAnimeID);
                    animationDrawable=(AnimationDrawable)petModel.getDrawable();
                    if(!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                    if (abs(ScreenEndX - ScreenStartX) < DIFFER && abs(ScreenEndY - ScreenStartY) < DIFFER) {
                        v.performClick();
                    }
            }
            return true;
        }
    }


    class FloatingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            removeView();
            windowManager.addView(menuView,centerLayoutParams);
            circleMenuView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    circleMenuView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    circleMenuView.open(true);
                }
            });
        }
    }
}
