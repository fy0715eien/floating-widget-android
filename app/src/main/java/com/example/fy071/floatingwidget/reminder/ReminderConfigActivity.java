package com.example.fy071.floatingwidget.reminder;

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
import com.example.fy071.floatingwidget.reminder.database.Alarm;
import com.example.fy071.floatingwidget.reminder.database.DbManager;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.fy071.floatingwidget.util.DateTimeFormatter.dateFormatter;
import static com.example.fy071.floatingwidget.util.DateTimeFormatter.timeFormatter;

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

    private DbManager dbManager;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            alarm.setHour(hourOfDay);
            alarm.setMinute(minute);

            alarmConfigTime.setText(timeFormatter(hourOfDay, minute));
        }
    };

    private int id;
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            alarm.setYear(year);
            alarm.setMonth(month);
            alarm.setDay(dayOfMonth);

            alarmConfigDate.setText(dateFormatter(year, month, dayOfMonth));
        }
    };

    @OnClick(R.id.save_alarm)
    void save() {
        // 若未完成提醒则不保存
        if (!isCompleted()) {
            return;
        }

        // 若完成则新建alarm对象，ID除外
        alarm.setTitle(alarmConfigTitle.getText().toString());
        alarm.setContent(alarmConfigContent.getText().toString());

        // 若是新建则等插入后获取id
        if (id == NEW_ALARM) {
            dbManager.insert(alarm);
            alarm.setId(dbManager.getLastInsertedId());
        } else {
            dbManager.update(alarm);
        }

        // 以alarm对象设置实际闹钟
        new AlarmBuilder(alarm).start(getApplicationContext());
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

            // 数据库中获取Alarm对象
            alarm = dbManager.search(id);

            // 提取Alarm信息，写入UI
            alarmConfigTitle.setText(alarm.getTitle());
            alarmConfigContent.setText(alarm.getContent());
            alarmConfigDate.setText(dateFormatter(alarm.getYear(), alarm.getMonth(), alarm.getDay()));
            alarmConfigTime.setText(timeFormatter(alarm.getHour(), alarm.getMinute()));
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

    private boolean isCompleted() {
        // 判断提醒是否完成，未完成则弹窗
        if (alarmConfigTitle.getText().toString().equals("")
                || alarmConfigContent.getText().toString().equals("")
                || alarmConfigDate.getText().toString().equals(getResources().getString(R.string.date))
                || alarmConfigTime.getText().toString().equals(getResources().getString(R.string.time))) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_reminder_not_complete)
                    .setMessage(R.string.dialog_message_reminder_not_complete)
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return false;
        }
        return true;
    }

}