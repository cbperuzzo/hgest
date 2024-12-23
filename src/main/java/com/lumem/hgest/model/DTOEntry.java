package com.lumem.hgest.model;


import java.time.LocalDate;
import java.time.LocalTime;

public class DTOEntry {
    private String os;
    private String segment;
    private LocalTime time;
    private LocalDate date;

    public DTOEntry(LocalDate date, String os, String segment, LocalTime time) {
        this.date = date;
        this.os = os;
        this.segment = segment;
        this.time = time;
    }

    public static DTOEntry placeholderEntry(){
        return new DTOEntry(LocalDate.now(),null,null,LocalTime.now());
    }
}
