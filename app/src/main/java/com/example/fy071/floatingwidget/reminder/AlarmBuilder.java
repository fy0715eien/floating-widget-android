package com.example.fy071.floatingwidget.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.fy071.floatingwidget.reminder.database.Alarm;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Administrator on 2018/4/18.
 */

public class AlarmBuilder {
    private static final String TAG = "AlarmBuilder";
    private Alarm alarm;

    AlarmBuilder() {
    }

    AlarmBuilder(Alarm alarm) {
        this.alarm = alarm;
    }

    public void start(Context context) {
        //获取AlarmManager对象
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        assert am != null;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, alarm.getYear());
        c.set(Calendar.MONTH, alarm.getMonth());
        c.set(Calendar.DAY_OF_MONTH, alarm.getDay());
        c.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        c.set(Calendar.MINUTE, alarm.getMinute());
        c.set(Calendar.SECOND, 0);

        //构造闹钟触发时的PendingIntent
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("title", alarm.getTitle());
        alarmIntent.putExtra("content", alarm.getContent());
        PendingIntent alarmPi = PendingIntent.getBroadcast(context, alarm.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmPi);
        } else {
            // 构造点击闹钟开启的PendingIntent
            Intent alarmListIntent = new Intent(context, ReminderListActivity.class);
            PendingIntent alarmListPi = PendingIntent.getActivity(context, alarm.getId(), alarmListIntent, 0);

            // 设置闹钟信息
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), alarmListPi);

            // 以闹钟信息和闹钟触发时Intent设置闹钟
            am.setAlarmClock(alarmClockInfo, alarmPi);
        }
    }

    public void cancel(Context context, int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        assert am != null;

        am.cancel(pi);
    }
}