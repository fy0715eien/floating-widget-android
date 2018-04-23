package com.example.fy071.floatingwidget.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Administrator on 2018/4/18.
 */

public class AlarmBuilder {
    private static final String TAG = "AlarmBuilder";

    private int id;
    private String title;
    private String content;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    AlarmBuilder() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void start(Context context) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);//也可以填数字，0-11,一月为0
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        //设置一个PendingIntent对象，发送广播
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.setComponent(new ComponentName("com.example.fy071.floatingwidget","com.example.fy071.floatingwidget.reminder.AlarmReceiver"));
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, 0);

        //获取AlarmManager对象
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Log.d(TAG, "start: called" + am);

        Log.d(TAG, c.getTime().toString());

        if (am != null) {
            if (Build.VERSION.SDK_INT < 21)
                am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            else {
                Intent i2 = new Intent(context, ReminderListActivity.class);
                i2.putExtra("id", id);
                PendingIntent pi2 = PendingIntent.getActivity(context, id, i2, 0);
                am.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), pi2), pi);
            }
        }
    }

    public void cancel(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Log.d(TAG, "cancel: called" + am);
        if (am != null) {
            am.cancel(pi);
        }
    }
}