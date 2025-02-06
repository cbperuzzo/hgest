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

    public Shift(StoredUser storedUser, String os, String segment,
                 LocalTime openTime, LocalDate openDate, @Nullable LocalTime
                         closeTime, @Nullable LocalDate closeDate, boolean closed, Long totalMinutes) {
        this.storedUser = storedUser;
        this.os = os;
        this.segment = segment;
        this.openTime = openTime;
        this.openDate = openDate;
        this.closeTime = closeTime;
        this.closeDate = closeDate;
        this.closed = closed;
        this.totalMinutes = totalMinutes;
    }

    //open
    public Shift(Long id, StoredUser storedUser, String os, String segment, LocalTime openTime, LocalDate openDate) {
        this.id = id;
        this.storedUser = storedUser;
        this.os = os;
        this.segment = segment;
        this.openTime = openTime;
        this.openDate = openDate;
    }

    //converting the DTO time and date from string to actual date and time objects
}
