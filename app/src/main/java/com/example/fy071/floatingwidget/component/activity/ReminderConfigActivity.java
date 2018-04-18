package com.example.fy071.floatingwidget.component.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.database.Alarm;
import com.example.fy071.floatingwidget.component.database.DbManager;
import com.example.fy071.floatingwidget.util.AlarmBuilder;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReminderConfigActivity extends AppCompatActivity {
    private static final String TAG = "ReminderConfigActivity";
    private static final int NEW_ALARM = -1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.alarm_config_title)
    TextInputEditText alarmConfigTitle;
    @BindView(R.id.alarm_config_content)
    TextInputEditText alarmConfigContent;
    @BindView(R.id.alarm_config_date)
    TextView alarmConfigDate;
    @BindView(R.id.alarm_config_time)
    TextView alarmConfigTime;

    private Alarm alarm = new Alarm();

    private AlarmBuilder alarmBuilder = new AlarmBuilder();

    private DbManager dbManager;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            final String time = String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute);
            alarmConfigTime.setText(time);

            alarmBuilder.setHour(hourOfDay);
            alarmBuilder.setMinute(minute);
        }
    };

    private int id;
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            final String date = String.valueOf(year) + "." + (month + 1) + "." + dayOfMonth;
            alarmConfigDate.setText(date);

            alarmBuilder.setYear(year);
            alarmBuilder.setMonth(month + 1);
            alarmBuilder.setDay(dayOfMonth);
        }
    };

    @OnClick(R.id.save_alarm)
    void save() {
        if (alarmConfigTitle.getText().toString().equals("")
                || alarmConfigContent.getText().toString().equals("")
                || alarmConfigDate.getText().toString().equals("")
                || alarmConfigTime.getText().toString().equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_reminder_not_complete)
                    .setMessage(R.string.dialog_message_reminder_not_complete)
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }

        alarm.withTitle(alarmConfigTitle.getText().toString())
                .withContent(alarmConfigContent.getText().toString())
                .withDate(alarmConfigDate.getText().toString())
                .withTime(alarmConfigTime.getText().toString());

        if (id == NEW_ALARM) {
            alarm.withId((int) System.currentTimeMillis());
            dbManager.insert(alarm);

            alarmBuilder.setId(alarm.getId());
            alarmBuilder.start(this);
        } else {
            alarm.withId(id);
            dbManager.update(alarm);
        }
        finish();
    }

    @OnClick(R.id.alarm_config_date)
    void showDatePickerDialog() {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, onDateSetListener, year, month, day).show();
    }

    @OnClick(R.id.alarm_config_time)
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
        setContentView(R.layout.activity_reminder_config);
        ButterKnife.bind(this);

        dbManager = new DbManager(this);

        // 判断是修改操作还是新增操作
        id = getIntent().getIntExtra("id", NEW_ALARM);
        if (id == NEW_ALARM) {
            toolbar.setTitle(R.string.toolbar_new_reminder);
        } else {
            toolbar.setTitle(R.string.toolbar_config_reminder);

            Log.d(TAG, "onCreate: " + id);

            alarm = dbManager.search(id);
            alarmConfigTitle.setText(alarm.getTitle());
            alarmConfigContent.setText(alarm.getContent());
            alarmConfigDate.setText(alarm.getDate());
            alarmConfigTime.setText(alarm.getTime());
        }
        initToolbar();
    }

    @Override
    public void onBackPressed() {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_discard_changes)
                .setPositiveButton(R.string.dialog_positive_button_discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
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