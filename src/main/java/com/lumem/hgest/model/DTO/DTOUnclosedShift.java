package com.lumem.hgest.model.DTO;

import com.lumem.hgest.model.Shift;
import com.lumem.hgest.model.Util.DateFormater;

public class DTOUnclosedShift {
    private Long id;
    private String os;
    private String seg;
    private String date;
    private String time;

    public DTOUnclosedShift(Long id, String os, String seg, String date, String time) {
        this.id = id;
        this.os = os;
        this.seg = seg;
        this.date = date;
        this.time = time;
    }

    public static DTOUnclosedShift createUnclosedShift(Shift shift){
        if (shift == null){
            return null;
        }
        else{
            return new DTOUnclosedShift(shift.getId(),shift.getOs(),shift.getSegment(),
                    DateFormater.brazilianFormat(shift.getOpenDate()),shift.getOpenTime().toString());

        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getSeg() {
        return seg;
    }

    public void setSeg(String seg) {
        this.seg = seg;
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
}
