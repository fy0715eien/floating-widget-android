package com.example.fy071.floatingwidget.component;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


import com.example.fy071.floatingwidget.R;

import java.util.Calendar;

public class RemainderConfigActivity extends Activity implements TimePickerDialog.TimePickerDialogInterface {
    private TimePickerDialog mTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_set_owner);
        mTimePickerDialog = new TimePickerDialog(RemainderConfigActivity.this);


    }

    //时间选择器----------确定
    @Override
    public void positiveListener() {
        int hour = mTimePickerDialog.getHour();
        int minute = mTimePickerDialog.getMinute();
        Log.i("=====","=======year======"+mTimePickerDialog.getYear());
        Log.i("=====","=======getMonth======"+mTimePickerDialog.getMonth());
        Log.i("=====","=======getDay======"+mTimePickerDialog.getDay());
        Log.i("=====","=======getHour======"+mTimePickerDialog.getHour());
        Log.i("=====","=======getMinute======"+mTimePickerDialog.getMinute());
        xxxxx.setText(hour+":"+minute);
    }

    //时间选择器-------取消
    @Override
    public void negativeListener() {

    }
}