package com.example.fy071.floatingwidget.component.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.entity.BluetoothConnectService;

import java.lang.ref.WeakReference;

public class ConnectedActivity extends AppCompatActivity {
    private final MyHandler handler = new MyHandler(this);

    private BluetoothConnectService bluetoothConnectService;

    private BluetoothDevice bluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        bluetoothConnectService = BluetoothConnectService.getInstance();
        bluetoothConnectService.setHandler(handler);

        bluetoothDevice = getIntent().getExtras().getParcelable("device");

        //为客户端
        if (bluetoothDevice != null) {
            bluetoothConnectService.connectServer(bluetoothDevice);
        }
    }

    public void setNewPosition(int x, int y) {

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
