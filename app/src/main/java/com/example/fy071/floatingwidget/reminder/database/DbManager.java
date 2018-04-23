package com.example.fy071.floatingwidget.reminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class DbManager {
    private DbHelper myDbHelper;

    public DbManager(Context context) {
        myDbHelper = new DbHelper(context);
    }

    public void insert(Alarm alarm) {
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.INFO_TITLE, alarm.getTitle());
        cv.put(DbHelper.INFO_CONTENT, alarm.getContent());
        cv.put(DbHelper.INFO_YEAR, alarm.getYear());
        cv.put(DbHelper.INFO_MONTH, alarm.getMonth());
        cv.put(DbHelper.INFO_DAY, alarm.getDay());
        cv.put(DbHelper.INFO_HOUR, alarm.getHour());
        cv.put(DbHelper.INFO_MINUTE, alarm.getMinute());
        db.insert(DbHelper.TABLE_NAME, null, cv);
        db.close();
    }

    public Alarm search(int id) {
        SQLiteDatabase db = myDbHelper.getReadableDatabase();

        Cursor cs = db.query(DbHelper.TABLE_NAME, null, "_id = ? ", new String[]{String.valueOf(id)}, null, null, null);
        Alarm alarm = null;
        if (cs.moveToNext()) {
            alarm = new Alarm();
            alarm.setId(cs.getInt(cs.getColumnIndex(DbHelper.INFO_ID)));
            alarm.setTitle(cs.getString(cs.getColumnIndex(DbHelper.INFO_TITLE)));
            alarm.setContent(cs.getString(cs.getColumnIndex(DbHelper.INFO_CONTENT)));
            alarm.setYear(cs.getInt(cs.getColumnIndex(DbHelper.INFO_YEAR)));
            alarm.setMonth(cs.getInt(cs.getColumnIndex(DbHelper.INFO_MONTH)));
            alarm.setDay(cs.getInt(cs.getColumnIndex(DbHelper.INFO_DAY)));
            alarm.setHour(cs.getInt(cs.getColumnIndex(DbHelper.INFO_HOUR)));
            alarm.setMinute(cs.getInt(cs.getColumnIndex(DbHelper.INFO_MINUTE)));
        }
        cs.close();
        db.close();
        return alarm;
    }

    public List<Alarm> searchAll() {
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cs = db.query(DbHelper.TABLE_NAME, null, null, null, null, null, null);
        Alarm alarm;
        List<Alarm> list = new ArrayList<>();

        while (cs.moveToNext()) {
            alarm = new Alarm();
            alarm.setId(cs.getInt(cs.getColumnIndex(DbHelper.INFO_ID)));
            alarm.setTitle(cs.getString(cs.getColumnIndex(DbHelper.INFO_TITLE)));
            alarm.setContent(cs.getString(cs.getColumnIndex(DbHelper.INFO_CONTENT)));
            alarm.setYear(cs.getInt(cs.getColumnIndex(DbHelper.INFO_YEAR)));
            alarm.setMonth(cs.getInt(cs.getColumnIndex(DbHelper.INFO_MONTH)));
            alarm.setDay(cs.getInt(cs.getColumnIndex(DbHelper.INFO_DAY)));
            alarm.setHour(cs.getInt(cs.getColumnIndex(DbHelper.INFO_HOUR)));
            alarm.setMinute(cs.getInt(cs.getColumnIndex(DbHelper.INFO_MINUTE)));
            list.add(alarm);
        }

        cs.close();
        db.close();
        return list;
    }

    public int getLastInsertedId() {
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(_id) FROM info", null);
        c.moveToFirst();
        int id = c.getInt(0);
        c.close();
        return id;
    }

    public void delete(int id) {
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_NAME, "_id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void update(Alarm alarm) {
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DbHelper.INFO_ID, alarm.getId());
        cv.put(DbHelper.INFO_TITLE, alarm.getTitle());
        cv.put(DbHelper.INFO_CONTENT, alarm.getContent());
        cv.put(DbHelper.INFO_YEAR, alarm.getYear());
        cv.put(DbHelper.INFO_MONTH, alarm.getMonth());
        cv.put(DbHelper.INFO_DAY, alarm.getDay());
        cv.put(DbHelper.INFO_HOUR, alarm.getHour());
        cv.put(DbHelper.INFO_MINUTE, alarm.getMinute());

        String id = String.valueOf(alarm.getId());
        db.update(DbHelper.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();
    }
}
