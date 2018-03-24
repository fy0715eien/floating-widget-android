package com.example.fy071.floatingwidget.component;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.entity.Pet;
/**
 * Created by fy071 on 2018/3/9.
 */
/*hhhhh*/
public class FloatingViewService extends Service {

    private static final int UPDATE_PIC = 0x100;
    private View view;// 透明窗体
    private HandlerUI handler = null;
    private Thread updateThread = null;
    private boolean viewAdded = false;// 透明窗体是否已经显示
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        createFloatView();
        refresh();
        startForeground(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int retVal = super.onStartCommand(intent, flags, startId);
        return retVal;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        removeView();
        stopForeground(true);
    }
    public void startForeground(Service context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("I'm running.")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        Notification notification = builder.build();

        context.startForeground(8888, notification);
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
        handler = new HandlerUI();
        UpdateUI update = new UpdateUI();
        updateThread = new Thread(update);
        updateThread.start(); // 开户线程
        view = LayoutInflater.from(this).inflate(R.layout.service_floating_view, null);

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        /*
         * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
         * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
         * PixelFormat.TRANSPARENT：悬浮窗透明
         */
        if(Build.VERSION.SDK_INT>26)
        {
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_APPLICATION_OVERLAY,
                    LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        }
        else
        {
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
                    LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        }
        // layoutParams.gravity = Gravity.RIGHT|Gravity.BOTTOM; //悬浮窗开始在右下角显示
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        /**
         * 监听窗体移动事件
         */
        view.setOnTouchListener(new OnTouchListener() {
            float[] temp = new float[] { 0f, 0f };

            public boolean onTouch(View v, MotionEvent event) {
                int eventaction = event.getAction();
                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN: // 按下事件，记录按下时手指在悬浮窗的XY坐标值
                        temp[0] = event.getX();
                        temp[1] = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        refreshView((int) (event.getRawX() - temp[0]),
                                (int) (event.getRawY() - temp[1]));
                        break;
                    case MotionEvent.ACTION_UP:
                        DisplayMetrics dm = new DisplayMetrics();
                        windowManager.getDefaultDisplay().getMetrics(dm);

                        float l=event.getRawX() - temp[0]+v.getWidth()/2,u=(event.getRawY() - temp[1])+v.getHeight()/2;
                        float r=dm.widthPixels-l,b=dm.heightPixels-u;
                        int ju=getMin(l,r,u,b);
                        switch(ju){
                            case 1:
                                refreshView(0, (int) (event.getRawY() - temp[1]));
                                break;
                            case 2:
                                refreshView(dm.widthPixels-v.getWidth(), (int) (event.getRawY() - temp[1]));
                                break;
                            case 3:
                                refreshView((int)(event.getRawX() - temp[0]), 0);
                                break;
                            case 4:
                                refreshView((int)(event.getRawX() - temp[0]), dm.heightPixels-v.getHeight());
                                break;
                        }
                }
                return true;
            }
        });
    }
    int getMin(float l,float r,float u,float b)
    {
        if(l<=r&&l<=u&&l<=b)return 1;
        if(r<=u&&r<=b)return 2;
        if(u<=b)return 3;
        return 4;
    }
    /**
     * 刷新悬浮窗
     *
     * @param x
     *            拖动后的X轴坐标
     * @param y
     *            拖动后的Y轴坐标
     */
    private void refreshView(int x, int y) {
        layoutParams.x = x;
        layoutParams.y = y;// STATUS_HEIGHT;
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
     *
     */
    class HandlerUI extends Handler {
        public HandlerUI() {

        }
    }
    /**
     * 更新悬浮窗的信息
     *
     * @author Administrator
     *
     */
    class UpdateUI implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            // 如果没有中断就一直运行
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = handler.obtainMessage();
                msg.what = UPDATE_PIC; // 设置消息标识
                handler.sendMessage(msg);
                // 休眠1s
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
