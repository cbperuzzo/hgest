package com.lumem.hgest.model.service;

import com.lumem.hgest.model.shift.Shift;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity(name = "service")
public class Service {
    @Id
    @GeneratedValue
    private long id;
    private long os;
    private String title;
    private String description;

    private LocalDate openDate;
    private LocalTime openTime;

    private LocalDate closeDate;
    private LocalTime closeTime;

    private boolean closed;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Shift> shifts;

    public Service() {
    }

    public Service(long id, long os, String title, String description, LocalDate openDate, LocalTime openTime, LocalDate closeDate, LocalTime closeTime, boolean closed, List<Shift> shifts) {
        this.id = id;
        this.os = os;
        this.title = title;
        this.description = description;
        this.openDate = openDate;
        this.openTime = openTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
        this.closed = closed;
        this.shifts = shifts;
    }

    public Service(String title) {
        this.title = title;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOs() {
        return os;
    }

    public void setOs(long os) {
        this.os = os;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
