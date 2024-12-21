package com.lumem.hgest.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class shift {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY,targetEntity = User.class)
    @JoinColumn(name = "UserEmployee_id")
    private User UserEmployee;

    private String serviceOrder;

}
