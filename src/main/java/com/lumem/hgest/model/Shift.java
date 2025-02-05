package com.lumem.hgest.model;

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
    private LocalTime closeTime;
    private LocalDate closeDate;

    public Shift(Long id, StoredUser storedUser, String os, String segment, LocalTime openTime,
                 LocalDate openDate, LocalTime closeTime, LocalDate closeDate) {
        this.id = id;
        this.storedUser = storedUser;
        this.os = os;
        this.segment = segment;
        this.openTime = openTime;
        this.openDate = openDate;
        this.closeTime = closeTime;
        this.closeDate = closeDate;
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
}
