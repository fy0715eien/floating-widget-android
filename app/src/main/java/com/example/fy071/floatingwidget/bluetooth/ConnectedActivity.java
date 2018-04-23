package com.example.fy071.floatingwidget.bluetooth;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectedActivity extends AppCompatActivity {
    private static final String TAG = "ConnectedActivity";
    private final MyHandler handler = new MyHandler(this);

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
        remotePet.setX((float) x);
        remotePet.setY((float) y);
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
                localPet.setImageResource(R.drawable.test1_1);
                break;
            case "model_2":
                localPet.setImageResource(R.drawable.test1_2);
                break;
            case "model_3":
                localPet.setImageResource(R.drawable.test1_3);
                break;
            default:
                localPet.setImageResource(R.drawable.test1_1);
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
                        float newX = event.getRawX() + viewStartX - fingerStartX;
                        float newY = event.getRawY() + viewStartY - fingerStartY;
                        v.setX(newX);
                        v.setY(newY);
                        Log.w(TAG, "onTouch: ACTION_MOVE");
                        bluetoothConnectService.sendCoordinate((int) newX, (int) newY);
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
                remotePet.setImageResource(R.drawable.test1_1);
                break;
            case 2:
                remotePet.setImageResource(R.drawable.test2_1);
                break;
            case 3:
                remotePet.setImageResource(R.drawable.test3_1);
                break;
            default:
                remotePet.setImageResource(R.drawable.test1_1);
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