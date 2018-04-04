package com.example.fy071.floatingwidget.component;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.ramotion.circlemenu.CircleMenuView;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class FloatingViewService extends Service {
    private static final String TAG = "FloatingViewService";
    private static final int TO_LEFT = 1;
    private static final int TO_RIGHT = 2;
    private static final int TO_UP = 3;
    private static final int TO_BOTTOM = 4;
    private static final int UPDATE_PIC = 0x100;
    private View view;// 透明窗体
    private int statusBarHeight;
    private CircleMenuView circleMenuView;
    private static final int DIFFER = 5;//距离
    private HandlerUI handler = null;
    private Thread updateThread = null;
    private boolean viewAdded = false;// 透明窗体是否已经显示
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager.LayoutParams centerLayoutParams;


    //图片资源
    private int[] res = { R.id.id_a, R.id.id_b, R.id.id_c, R.id.id_d, R.id.id_e, R.id.id_f, R.id.id_g, R.id.id_h };
    //存放ImageView
    private List<ImageView> imageViewList = new ArrayList<ImageView>();
    //菜单是不是展开
    private boolean isNotExpand = true;


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
        view = LayoutInflater.from(this).inflate(R.layout.service_floating_view, null);
        view.setOnTouchListener(new FloatingTouchListener());

        for (int i = 0; i < res.length; i++) {
            ImageView imageView = (ImageView) view.findViewById(res[i]);
            //存放在list中
            imageView.setOnTouchListener(new FloatingTouchListener());
            imageView.setOnClickListener(new FloatingClickListener());
            imageViewList.add(imageView);

        }

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
//注释
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
        centerLayoutParams.gravity=Gravity.CENTER;

        /*
         * 监听窗体移动事件
         */

    }

    int getMin(float left, float right, float up, float bottom) {
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
            int eventAction = event.getAction();
            switch (eventAction) {
                case MotionEvent.ACTION_DOWN: // 按下事件，记录按下时手指在悬浮窗的XY坐标值
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

            switch (v.getId()) {
                //主菜单被点击
                case R.id.id_a:
                    //主菜单没有展开时被点击
                    if (isNotExpand == true) {
                        //启动动画
                        startAnim();
                    } else {
                        //关闭动画
                        closeAnim();
                    }
                    break;
                //定义其他组件被点击时触发的事件
                default:
                    Toast.makeText(FloatingViewService.this, "您点击了:" + view.getId(), Toast.LENGTH_LONG).show();
                    break;
            }
        }

        //关闭动画
        private void closeAnim() {
            for (int i = 1; i < res.length; i++) {
                float angle = (360 * 1.0f / (res.length - 2)) * (i - 1);
                PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("translationX", (float) (Math.sin((angle * 1.57 / 90)) * 200), 0);
                PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("translationY", (float) (Math.cos((angle * 1.57 / 90)) * 200), 0);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageViewList.get(i), holder1, holder2);
                // ObjectAnimator animator =
                // ObjectAnimator.ofFloat(imageViewList.get(i), "translationY", i * 60, 0);
                animator.setDuration(300);
                animator.start();
                isNotExpand = true;

            }
        }

        //开始动画
        private void startAnim() {
            //遍历第一个不是主菜单的ImageView列表
            for (int i = 1; i < res.length; i++) {
                //获取展开角度
                float angle = (360 * 1.0f / (res.length - 2)) * (i - 1);
                //获取X位移
                PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("translationX", 0, (float) (Math.sin((angle * 1.57 / 90)) * 200));
                //获取Y位移
                PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("translationY", 0, (float) (Math.cos((angle * 1.57 / 90)) * 200));
                //设置ImageView的属性动画
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageViewList.get(i), holder1, holder2);
                // ObjectAnimator animator =
                // ObjectAnimator.ofFloat(imageViewList.get(i), "translationY", 0, i *
                // 60);
                //动画时间
                animator.setDuration(1000);
                //动画延迟时间
                animator.setFrameDelay(500 * i);
                //设置加速器
                animator.setInterpolator(new BounceInterpolator());
                //启动动画
                animator.start();
                isNotExpand = false;
            }
        }
    }
}

