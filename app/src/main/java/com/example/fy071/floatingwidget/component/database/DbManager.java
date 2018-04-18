package com.example.fy071.floatingwidget.component.database;

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
        cv.put("date", alarm.getDate());
        cv.put("time", alarm.getTime());
        cv.put("title", alarm.getTitle());
        cv.put("content", alarm.getContent());
        db.insert("info", null, cv);
        db.close();
    }

    public Alarm search(int id) {
        SQLiteDatabase db = myDbHelper.getReadableDatabase();

        Cursor cs = db.query("info", null, "_id = ? ", new String[]{String.valueOf(id)}, null, null, null);
        Alarm alarm = null;
        if (cs.moveToNext()) {
            alarm = new Alarm();
            alarm.setId(cs.getInt(cs.getColumnIndex("_id")));
            alarm.setDate(cs.getString(cs.getColumnIndex("date")));
            alarm.setTime(cs.getString(cs.getColumnIndex("time")));
            alarm.setTitle(cs.getString(cs.getColumnIndex("title")));
            alarm.setContent(cs.getString(cs.getColumnIndex("content")));
        }
        cs.close();
        db.close();
        return alarm;
    }

    public List<Alarm> searchAll() {
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cs = db.query("info", null, null, null, null, null, null);
        Alarm alarm = null;
        List<Alarm> list = new ArrayList<>();

        while (cs.moveToNext()) {
            alarm = new Alarm();
            alarm.setId(cs.getInt(cs.getColumnIndex("_id")));
            alarm.setDate(cs.getString((cs.getColumnIndex("date"))));
            alarm.setTime(cs.getString(cs.getColumnIndex("time")));
            alarm.setTitle(cs.getString(cs.getColumnIndex("title")));
            alarm.setContent(cs.getString(cs.getColumnIndex("content")));
            list.add(alarm);
        }

        cs.close();
        db.close();
        return list;
    }


    public void deleteAll() {
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        db.delete("info", null, null);
        db.close();
    }


    public void delete(int id) {
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        db.delete("info", "_id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void update(Alarm alarm) {
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("_id", alarm.getId());
        cv.put("date", alarm.getDate());
        cv.put("time", alarm.getTime());
        cv.put("title", alarm.getTitle());
        cv.put("content", alarm.getContent());
        String id = String.valueOf(alarm.getId());
        db.update("info", cv, "_id = ?", new String[]{id});
        db.close();
    }
}
