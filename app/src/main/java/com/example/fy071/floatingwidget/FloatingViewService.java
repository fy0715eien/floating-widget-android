package com.example.fy071.floatingwidget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by fy071 on 2018/3/9.
 */

public class FloatingViewService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }
}
