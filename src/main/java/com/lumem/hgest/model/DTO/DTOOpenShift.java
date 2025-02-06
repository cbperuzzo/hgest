package com.lumem.hgest.model.DTO;


import java.time.LocalDate;
import java.time.LocalTime;


public class DTOOpenShift {
    private String os;
    private String segment;
    private String time;
    private String date;

    public DTOOpenShift(String date, String os, String segment, String time) {
        this.date = date;
        this.os = os;
        this.segment = segment;
        this.time = time;
    }

    public static DTOOpenShift placeholderEntry(){
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        String[] splitTime = localTime.toString().split(":");
        String time = splitTime[0]+":"+splitTime[1];
        String date = localDate.getYear()+"-"
                +String.format("%02d",localDate.getMonthValue())+"-"
                +String.format("%02d",localDate.getDayOfMonth());
        return new DTOOpenShift(date,null,null,time);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DTOEntry{" +
                "date='" + date + '\'' +
                ", os='" + os + '\'' +
                ", segment='" + segment + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}

