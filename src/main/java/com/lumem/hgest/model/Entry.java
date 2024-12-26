package com.lumem.hgest.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Entry {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,targetEntity = StoredUser.class)
    @JoinColumn(name = "UserEmployee_id")
    private StoredUser storedUser;
    private String os;
    private String segment;
    private LocalTime time;
    private LocalDate date;

    public Entry(LocalDate date, Long id, String os, String segment, LocalTime time, StoredUser storedUser) {
        this.date = date;
        this.id = id;
        this.os = os;
        this.segment = segment;
        this.time = time;
        this.storedUser = storedUser;
    }

}
