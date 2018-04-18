package com.example.fy071.floatingwidget.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.fy071.floatingwidget.component.database.Alarm;

import java.util.Calendar;
import java.util.ConcurrentModificationException;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Administrator on 2018/4/18.
 */

public class LRJAlarm {

    private int id;
    private String title;
    private String content;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;


    public LRJAlarm(){}
    public LRJAlarm(int _id,String t,String c,int y,int m,int d,int h,int min) {
//创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串
        id= _id;
        title = t;
        content = c;
        year = y;
        month = m;
        day = d;
        hour = h;
        minute = min;
    }
    public void set(int _id,String t,String c,int y,int m,int d,int h,int min) {
        id=_id;
        title = t;
        content = c;
        year = y;
        month = m;
        day = d;
        hour = h;
        minute = min;
    }
    public void start(Context context)
    {

        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month-1);//也可以填数字，0-11,一月为0
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        Intent intent=new Intent(context,LRJAlarmReceiver.class);
        intent.setAction("com.alarm.ring");
        intent.putExtra("title",title);
        intent.putExtra("content",content);
        PendingIntent pi=PendingIntent.getBroadcast(context, id, intent,0);
//设置一个PendingIntent对象，发送广播
        AlarmManager am=(AlarmManager)context.getSystemService(ALARM_SERVICE);
//获取AlarmManager对象
        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
    }
    public void cancel(Context context)
    {
        Intent intent=new Intent(context,LRJAlarmReceiver.class);
        intent.setAction("com.alarm.ring");
        intent.putExtra("title",title);
        intent.putExtra("content",content);
        PendingIntent pi=PendingIntent.getBroadcast(context, id, intent,0);

        AlarmManager am=(AlarmManager)context.getSystemService(ALARM_SERVICE);
        am.cancel(pi);
    }
}
