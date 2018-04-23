package com.example.fy071.floatingwidget.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.fy071.floatingwidget.reminder.database.Alarm;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Administrator on 2018/4/18.
 */

public class AlarmBuilder {
    private static final String TAG = "AlarmBuilder";
    private Alarm alarm;

    public AlarmBuilder() {
    }

    AlarmBuilder(Alarm alarm) {
        this.alarm = alarm;
    }

    public void start(Context context) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, alarm.getYear());
        c.set(Calendar.MONTH, alarm.getMonth());//也可以填数字，0-11,一月为0
        c.set(Calendar.DAY_OF_MONTH, alarm.getDay());
        c.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        c.set(Calendar.MINUTE, alarm.getMinute());
        c.set(Calendar.SECOND, 0);
        //设置一个PendingIntent对象，发送广播
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", alarm.getTitle());
        intent.putExtra("content", alarm.getContent());
        PendingIntent pi = PendingIntent.getBroadcast(context, alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //获取AlarmManager对象
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Log.d(TAG, "start: called" + am);

        Log.d(TAG, c.getTime().toString());

        if (am != null) {
            if (Build.VERSION.SDK_INT < 21)
                am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            else {
                Intent i2 = new Intent(context, ReminderListActivity.class);
                i2.putExtra("id", alarm.getId());
                PendingIntent pi2 = PendingIntent.getActivity(context, alarm.getId(), i2, 0);
                am.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), pi2), pi);
            }
        }
    }

    public void cancel(Context context, int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Log.d(TAG, "cancel: called" + am);
        if (am != null) {
            am.cancel(pi);
        }
    }
}