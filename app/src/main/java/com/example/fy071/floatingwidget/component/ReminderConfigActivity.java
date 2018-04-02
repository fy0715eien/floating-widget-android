package com.example.fy071.floatingwidget.component;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.fy071.floatingwidget.R;

import java.util.Calendar;

public class ReminderConfigActivity extends Activity implements TimePickerDialog.TimePickerDialogInterface {
    private TimePickerDialog mTimePickerDialog;
    private EditText rd_content;
    private SharedPreferences sharedPrefrences;
    private SharedPreferences.Editor editor;

    // 要存储的文件名
    private static final String FILENAME = "filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder);
        mTimePickerDialog = new TimePickerDialog(ReminderConfigActivity.this);


        rd_content = (EditText) findViewById(R.id.reminder_content);


        sharedPrefrences = this.getSharedPreferences(FILENAME, MODE_WORLD_READABLE);
        String r_upgrade= sharedPrefrences.getString("upgrade", "请输入提醒内容！");

        rd_content.setText(r_upgrade);

        Button bt = (Button) findViewById(R.id.button_save);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = getSharedPreferences(FILENAME, MODE_WORLD_WRITEABLE).edit();
                String upgrade=rd_content.getText().toString();
                editor.putString("upgrade", upgrade);
                editor.apply();
                }
        });
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