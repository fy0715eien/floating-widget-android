package com.example.fy071.floatingwidget.component.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.entity.BluetoothConnectService;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectedActivity extends AppCompatActivity {
    private final MyHandler handler = new MyHandler(this);
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private BluetoothConnectService bluetoothConnectService;

    private View localPet;
    private ImageView petModel;

    private float fingerStartX, fingerStartY, viewStartX, viewStartY;

    private View remotePet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        ButterKnife.bind(this);

        initToolbar();

        bluetoothConnectService = BluetoothConnectService.getInstance();
        bluetoothConnectService.setHandler(handler);

        initLocalPet();

        initRemotePet();
    }

    public void setNewPosition(int x, int y) {
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

    private void initLocalPet() {
        switch (PreferenceHelper.petModel) {
            case "model_1":
                localPet = LayoutInflater.from(this).inflate(R.layout.layout_pet_1, null);
                break;
            case "model_2":
                localPet = LayoutInflater.from(this).inflate(R.layout.layout_pet_2, null);
                break;
            case "model_3":
                localPet = LayoutInflater.from(this).inflate(R.layout.layout_pet_3, null);
                break;
            default:
        }

        petModel = localPet.findViewById(R.id.imageView_pet);

        localPet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnimationDrawable animationDrawable;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        animationDrawable = (AnimationDrawable) petModel.getDrawable();
                        if (!animationDrawable.isRunning()) {
                            animationDrawable.start();
                        }
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
                        bluetoothConnectService.sendData((int) newX, (int) newY);
                        break;
                    case MotionEvent.ACTION_UP:
                        petModel.setImageResource(R.drawable.up_anime_1);
                        animationDrawable = (AnimationDrawable) petModel.getDrawable();
                        if (!animationDrawable.isRunning()) {
                            animationDrawable.start();
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void initRemotePet() {
        remotePet = LayoutInflater.from(this).inflate(R.layout.layout_pet_1, null);
    }

    static class MyHandler extends Handler {
        WeakReference<ConnectedActivity> mActivity;

        MyHandler(ConnectedActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ConnectedActivity activity = mActivity.get();
            if (activity != null) {
                activity.setNewPosition(msg.arg1, msg.arg2);
            }
        }
    }
}
