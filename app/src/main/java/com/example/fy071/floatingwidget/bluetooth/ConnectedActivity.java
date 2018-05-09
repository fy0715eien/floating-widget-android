package com.example.fy071.floatingwidget.bluetooth;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectedActivity extends AppCompatActivity {
    private static final String TAG = "ConnectedActivity";
    private final MyHandler handler = new MyHandler(this);

    public static final float RATIO = 10000;

    private int width;
    private int height;

    private BluetoothConnectService bluetoothConnectService;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imageView_local)
    ImageView localPet;

    @BindView(R.id.imageView_remote)
    ImageView remotePet;

    private float fingerStartX, fingerStartY, viewStartX, viewStartY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        ButterKnife.bind(this);

        initToolbar();

        bluetoothConnectService = BluetoothConnectService.getInstance();
        bluetoothConnectService.setHandler(handler);

        initLocalPet();

        // 远程宠物暂时使用默认模型
        initRemotePet(1);

        // 获取屏幕宽高(单位：像素)
        WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        assert windowManager != null;
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setNewPosition(int x, int y) {
        // 未初始化远程宠物模型，返回
        if (remotePet == null) {
            return;
        }

        float newX = x * width / RATIO;
        float newY = y * height / RATIO;

        remotePet.setX(newX);
        remotePet.setY(newY);
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.drawer_item_pairing);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initLocalPet() {
        switch (PreferenceHelper.petModel) {
            case "model_1":
                localPet.setImageResource(R.drawable.emoji_1_0);
                break;
            case "model_2":
                localPet.setImageResource(R.drawable.emoji_2_0);
                break;
            case "model_3":
                localPet.setImageResource(R.drawable.emoji_2_1);
                break;
            default:
                localPet.setImageResource(R.drawable.emoji_1_0);
        }

        localPet.setOnTouchListener(new View.OnTouchListener() {
            AnimationDrawable animationDrawable;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
/*                        switch (PreferenceHelper.petModel) {
                            case "model_1":
                                localPet.setImageResource(R.drawable.down_anime_1);
                                break;
                            case "model_2":
                                localPet.setImageResource(R.drawable.down_anime_2);
                                break;
                            case "model_3":
                                localPet.setImageResource(R.drawable.down_anime_3);
                                break;
                            default:
                        }
                        animationDrawable = (AnimationDrawable) localPet.getDrawable();
                        if (!animationDrawable.isRunning()) {
                            animationDrawable.start();
                        }*/
                        //event.getRawXY()获得手指相对屏幕左上角的坐标
                        //v.getXY()获得view相对layout左上角的坐标
                        //二者原点不同故需先保存，之后补上相差坐标
                        fingerStartX = event.getRawX();
                        fingerStartY = event.getRawY();
                        viewStartX = v.getX();
                        viewStartY = v.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 设置本地坐标
                        float localNewX = event.getRawX() + viewStartX - fingerStartX;
                        float localNewY = event.getRawY() + viewStartY - fingerStartY;
                        v.setX(localNewX);
                        v.setY(localNewY);

                        // 发送相对坐标(范围0-1，由于只能传整数，先乘比例，接收方除以比例)
                        float tempNewX = (localNewX / width) * RATIO;
                        float tempNewY = (localNewY / height) * RATIO;
                        bluetoothConnectService.sendCoordinate((int) tempNewX, (int) tempNewY);
                        break;
                    case MotionEvent.ACTION_UP:
/*                        switch (PreferenceHelper.petModel) {
                            case "model_1":
                                localPet.setImageResource(R.drawable.up_anime_1);
                                break;
                            case "model_2":
                                localPet.setImageResource(R.drawable.up_anime_2);
                                break;
                            case "model_3":
                                localPet.setImageResource(R.drawable.up_anime_3);
                                break;
                            default:
                        }
                        animationDrawable = (AnimationDrawable) localPet.getDrawable();
                        if (!animationDrawable.isRunning()) {
                            animationDrawable.start();
                        }*/
                        break;
                }
                return true;
            }
        });
    }

    private void initRemotePet(int modelNumber) {
        switch (modelNumber) {
            case 1:
                remotePet.setImageResource(R.drawable.emoji_1_0);
                break;
            case 2:
                remotePet.setImageResource(R.drawable.test2_1);
                break;
            case 3:
                remotePet.setImageResource(R.drawable.test3_1);
                break;
            default:
                remotePet.setImageResource(R.drawable.emoji_1_0);
        }
    }

    static class MyHandler extends Handler {
        WeakReference<ConnectedActivity> mActivity;

        MyHandler(ConnectedActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.w(TAG, "handleMessage: message receive");
            ConnectedActivity activity = mActivity.get();
            if (activity != null) {
                activity.setNewPosition(msg.arg1, msg.arg2);
            }
        }
    }
}