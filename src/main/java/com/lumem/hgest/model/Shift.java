package com.lumem.hgest.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Shift {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,targetEntity = StoredUser.class)
    @JoinColumn(name = "UserEmployee_id")
    private StoredUser storedUser;

    private String os;
    private String segment;
    private LocalTime openTime;
    private LocalDate openDate;

    @Nullable
    private LocalTime closeTime;

    @Nullable
    private LocalDate closeDate;

    private boolean closed;

    private Long totalMinutes;

    public Shift(){

    }

    //open
    public Shift(StoredUser storedUser, String os, String segment, LocalTime openTime, LocalDate openDate) {
        this.storedUser = storedUser;
        this.os = os;
        this.segment = segment;
        this.openTime = openTime;
        this.openDate = openDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoredUser getStoredUser() {
        return storedUser;
    }

    public void setStoredUser(StoredUser storedUser) {
        this.storedUser = storedUser;
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

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    @Nullable
    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(@Nullable LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    @Nullable
    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(@Nullable LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Long getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Long totalMinutes) {
        this.totalMinutes = totalMinutes;
    }
}
