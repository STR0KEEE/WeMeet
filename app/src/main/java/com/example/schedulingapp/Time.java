package com.example.schedulingapp;

public class Time {
    private int hour;
    private int minute;
     public Time(int hour, int minute){
         this.hour=hour;
         this.minute=minute;
     }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
