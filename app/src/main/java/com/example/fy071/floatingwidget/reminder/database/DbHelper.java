package com.example.fy071.floatingwidget.reminder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "alarm.db";
    public static final String TABLE_NAME = "info";
    public static final String INFO_ID = "_id";
    public static final String INFO_TITLE = "title";
    public static final String INFO_CONTENT = "content";
    public static final String INFO_YEAR = "year";
    public static final String INFO_MONTH = "month";
    public static final String INFO_DAY = "day";
    public static final String INFO_HOUR = "hour";
    public static final String INFO_MINUTE = "minute";

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //如果没有数据库及数据表，就创建一个
        String sql = "Create table if not exists " +
                TABLE_NAME + "( " +
                INFO_ID + " integer primary key autoincrement," +
                INFO_TITLE + " varchar(30)," +
                INFO_CONTENT + " varchar(120)," +
                INFO_YEAR + " integer," +
                INFO_MONTH + " integer," +
                INFO_DAY + " integer," +
                INFO_HOUR + " integer," +
                INFO_MINUTE + " integer" +
                " )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
}
