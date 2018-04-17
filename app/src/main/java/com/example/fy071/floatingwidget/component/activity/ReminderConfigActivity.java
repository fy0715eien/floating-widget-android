package com.example.fy071.floatingwidget.component.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.TimePickerDialog;

import butterknife.ButterKnife;

public class ReminderConfigActivity extends BaseActivity implements TimePickerDialog.TimePickerDialogInterface {
    // 要存储的文件名
    private static final String FILENAME = "filename";
    private TimePickerDialog mTimePickerDialog;
    private EditText rd_content;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;

    private static final String TAG = "ReminderConfigActivity";

    private static final int NEW_ALARM = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder);
        ButterKnife.bind(this);

        // 判断是修改操作还是新增操作
        int id = getIntent().getIntExtra("id", NEW_ALARM);
        if (id == NEW_ALARM) {
            // TODO: 2018/4/16 新闹钟
        } else {
            // TODO: 2018/4/16 以id 读取数据库
        }

        initToolbar();

        mTimePickerDialog = new TimePickerDialog(ReminderConfigActivity.this);

        rd_content = findViewById(R.id.reminder_content);

        sharedPreferences = this.getSharedPreferences(FILENAME, MODE_PRIVATE);
        String r_upgrade = sharedPreferences.getString("upgrade", "请输入提醒内容！");

        rd_content.setText(r_upgrade);

        Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = getSharedPreferences(FILENAME, MODE_PRIVATE).edit();
                String upgrade = rd_content.getText().toString();
                editor.putString("upgrade", upgrade);
                editor.apply();
            }
        });
    }

    //时间选择器----------确定
    @Override
    public void positiveListener() {
        TextView reminder_time = findViewById(R.id.reminder_time);
        int year = mTimePickerDialog.getYear();
        int month = mTimePickerDialog.getMonth();
        int day = mTimePickerDialog.getDay();
        int hour = mTimePickerDialog.getHour();
        int minute = mTimePickerDialog.getMinute();
        Log.i(TAG, "year= " + mTimePickerDialog.getYear());
        Log.i(TAG, "month= " + mTimePickerDialog.getMonth());
        Log.i(TAG, "day= " + mTimePickerDialog.getDay());
        Log.i(TAG, "hour= " + mTimePickerDialog.getHour());
        Log.i(TAG, "minute= " + mTimePickerDialog.getMinute());
        reminder_time.setText(year + "年" + month + "月" + day + "日" + hour + ":" + minute);
    }

    //时间选择器-------取消
    @Override
    public void negativeListener() {

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_new_reminder);
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
}