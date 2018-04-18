package com.example.fy071.floatingwidget.component.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.database.Alarm;
import com.example.fy071.floatingwidget.component.database.DbManager;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FYReminderConfigActivity extends AppCompatActivity {
    private static final String TAG = "FYReminderConfigActivit";
    private static final int NEW_ALARM = -1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.alarm_config_title)
    EditText alarmConfigTitle;
    @BindView(R.id.alarm_config_content)
    EditText alarmConfigContent;
    @BindView(R.id.alarm_config_time_hint)
    TextView alarmTimeHint;
    @BindView(R.id.alarm_config_date)
    TextView alarmConfigDate;
    @BindView(R.id.alarm_config_time)
    TextView alarmConfigTime;
    private DbManager dbManager;
    private Alarm alarm = null;
    private int id;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            final String time = String.valueOf(hourOfDay) + ":" + minute;
            alarmConfigTime.setText(time);
        }
    };
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            alarmTimeHint.setVisibility(View.GONE);
            final String date = String.valueOf(year) + "." + month + "." + dayOfMonth;
            alarmConfigDate.setText(date);
            showTimePickerDialog();
        }
    };

    @OnClick(R.id.linearLayout_date_time)
    void showDatePickerDialog() {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, onDateSetListener, year, month, day).show();
    }

    void showTimePickerDialog() {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(this, onTimeSetListener, hour, minute, DateFormat.is24HourFormat(this)).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fyreminder_config);
        ButterKnife.bind(this);

        dbManager = new DbManager(this);

        // 判断是修改操作还是新增操作
        id = getIntent().getIntExtra("id", NEW_ALARM);
        if (id == NEW_ALARM) {
            toolbar.setTitle(R.string.toolbar_new_reminder);

            alarmTimeHint.setVisibility(View.VISIBLE);
        } else {
            toolbar.setTitle(R.string.toolbar_config_reminder);

            alarm = dbManager.searchAlarm(id);
            alarmConfigTitle.setText(alarm.getTitle());
            alarmConfigContent.setText(alarm.getContent());
            alarmConfigDate.setText(alarm.getDate());
            alarmConfigTime.setText(alarm.getTime());
        }
        initToolbar();

        dbManager.insert(new Alarm("title", "content", "date", "time"));
    }

    private void initToolbar() {
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
