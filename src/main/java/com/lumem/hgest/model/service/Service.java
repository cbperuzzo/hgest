package com.lumem.hgest.model.service;

import com.lumem.hgest.model.shift.Shift;
import com.lumem.hgest.model.task.Task;
import com.lumem.hgest.model.user.StoredUser;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

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

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "service")
    private Set<Shift> shifts;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "service_assignment",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "userEmployee_id"))
    private Set<StoredUser> assignees;

    @OneToMany(mappedBy = "service",fetch = FetchType.LAZY)
    private Set<Task> tasks;

    public Service() {
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

    public Set<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(Set<Shift> shifts) {
        this.shifts = shifts;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Set<StoredUser> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<StoredUser> assignees) {
        this.assignees = assignees;
    }
}
