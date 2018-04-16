package com.example.fy071.floatingwidget.component.Database;

public class Alarm {
    private int id;
    private String date;
    private String time;
    private String title;
    private String content;

    public void setId(int id){
        this.id = id;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setTime(String time){
        this.time = time;
    }

    public int getId(){
        return id;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    @Override
    public String toString(){
            return "Alarm{" +
                    "id=" + id +
                    ", date'" + date + '\'' +
                    ", time'" + time + '\'' +
                    ", title'" + title + '\'' +
                    ", cotent'" + content + '\'' +
                    '}';

    }

}
