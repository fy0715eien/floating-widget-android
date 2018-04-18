package com.example.fy071.floatingwidget.component.database;

public class Alarm {
    private int id;
    private String title;
    private String content;
    private String date;
    private String time;


    public Alarm() {
    }

    public Alarm(int id, String date, String time, String title, String content) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.title = title;
        this.content = content;
    }

    public Alarm(String date, String time, String title, String content) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.content = content;
    }

    public Alarm withDate(String date) {
        this.date = date;
        return this;
    }

    public Alarm withTitle(String title) {
        this.title = title;
        return this;
    }

    public Alarm withContent(String content) {
        this.content = content;
        return this;
    }

    public Alarm withTime(String time) {
        this.time = time;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", date'" + date + '\'' +
                ", time'" + time + '\'' +
                ", title'" + title + '\'' +
                ", cotent'" + content + '\'' +
                '}';

    }

}
