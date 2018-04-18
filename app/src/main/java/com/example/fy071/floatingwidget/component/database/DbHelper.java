package com.example.fy071.floatingwidget.component.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "alarm.db";
    private final String TABLE_NAME = "info";


    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //如果没有数据库及数据表，就创建一个
        String INFO_ID = "_id";
        String INFO_DATE = "date";
        String INFO_TIME = "time";
        String INFO_TITLE = "title";
        String INFO_CONTENT = "content";

        String sql = "Create table if not exists " +
                TABLE_NAME + "( " +
                INFO_ID + " integer primary key autoincrement," +
                INFO_DATE + " varchar(15)," +
                INFO_TIME + " varchar(15)," +
                INFO_TITLE + " varchar(30)," +
                INFO_CONTENT + " varchar(120)" +
                " )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    ;
}
