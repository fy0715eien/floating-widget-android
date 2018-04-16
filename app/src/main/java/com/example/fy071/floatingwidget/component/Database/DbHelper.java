package com.example.fy071.floatingwidget.component.Database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import junit.runner.Version;

public class DbHelper extends SQLiteOpenHelper{

    private final String DBNAME = "alarm.db";
    private final String TABLE_NAME = "info";

    private final String INFO_ID = "_id";
    private final String INFO_DATE = "date";
    private final String INFO_TIME = "time";
    private final String INFO_TITLE = "title";
    private final String INFO_CONTENT = "content";



    public DbHelper(Context context,String name ,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }

    public DbHelper(Context context){
        super(context,"alarm.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //如果没有数据库及数据表，就创建一个
        StringBuilder sql = new StringBuilder();
        sql.append("Create table if not exists ");
        sql.append(TABLE_NAME + "( ");
        sql.append(INFO_ID + "integer primary key autoincrement, ");
        sql.append(INFO_DATE + "varchar(15), ");
        sql.append(INFO_TIME + "varchar(15), ");
        sql.append(INFO_TITLE + "varchar(30), ");
        sql.append(INFO_CONTENT + "varchar(120) ");
        sql.append(" )");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        String sql = "drop table if exists " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    };
}
