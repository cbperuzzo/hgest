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

    //open
    public Shift(StoredUser storedUser, String os, String segment, LocalTime openTime, LocalDate openDate) {
        this.storedUser = storedUser;
        this.os = os;
        this.segment = segment;
        this.openTime = openTime;
        this.openDate = openDate;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", storedUser=" + storedUser +
                ", os='" + os + '\'' +
                ", segment='" + segment + '\'' +
                ", openTime=" + openTime +
                ", openDate=" + openDate +
                ", closeTime=" + closeTime +
                ", closeDate=" + closeDate +
                ", closed=" + closed +
                ", totalMinutes=" + totalMinutes +
                '}';
    }
}
