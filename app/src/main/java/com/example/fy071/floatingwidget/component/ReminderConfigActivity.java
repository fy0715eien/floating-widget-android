package com.example.fy071.floatingwidget.component;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;


import com.example.fy071.floatingwidget.R;

import java.util.Calendar;

public class ReminderConfigActivity extends Activity implements TimePickerDialog.TimePickerDialogInterface {
    private TimePickerDialog mTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder);
        mTimePickerDialog = new TimePickerDialog(ReminderConfigActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_new_reminder);

    }

    //时间选择器----------确定
    @Override
    public void positiveListener() {
        TextView reminder_time = (TextView)findViewById(R.id.reminder_time);
        int year = mTimePickerDialog.getYear();
        int month = mTimePickerDialog.getMonth();
        int day = mTimePickerDialog.getDay();
        int hour = mTimePickerDialog.getHour();
        int minute = mTimePickerDialog.getMinute();
        Log.i("=====","=======year======"+mTimePickerDialog.getYear());
        Log.i("=====","=======getMonth======"+mTimePickerDialog.getMonth());
        Log.i("=====","=======getDay======"+mTimePickerDialog.getDay());
        Log.i("=====","=======getHour======"+mTimePickerDialog.getHour());
        Log.i("=====","=======getMinute======"+mTimePickerDialog.getMinute());
        reminder_time.setText(year+"年"+month+"月"+day+"日"+hour+":"+minute);
    }

    //时间选择器-------取消
    @Override
    public void negativeListener() {

    }
}