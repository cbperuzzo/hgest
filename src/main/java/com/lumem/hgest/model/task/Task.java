package com.lumem.hgest.model.task;

import com.lumem.hgest.model.service.Service;
import com.lumem.hgest.model.shift.Shift;
import jakarta.persistence.*;

@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    private boolean done;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id",nullable = false)
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id",nullable = true)
    private Shift completionShift;
}
