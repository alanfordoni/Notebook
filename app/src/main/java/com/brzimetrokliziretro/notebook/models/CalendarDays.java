package com.brzimetrokliziretro.notebook.models;

import java.util.Calendar;

public class CalendarDays {
    private Calendar date;

    public CalendarDays(){}

    public CalendarDays(Calendar day){
        this.date = day;
    }

    public void setDate(Calendar date){
        this.date = date;
    }

    public Calendar getDate(){
        return this.date;
    }
}
