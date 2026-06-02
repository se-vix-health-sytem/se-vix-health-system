package com.nvivx.vixhealthsystem.model.medical;

import java.time.LocalDateTime;

public class Appointment {

    private int id;
    private LocalDateTime dateTime;
    private int duration;
    private String notes;

    public Appointment(int id, LocalDateTime dateTime, int duration, String notes) {
        this.id = id;
        this.dateTime = dateTime;
        this.duration = duration;
        this.notes = notes;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }


    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }


    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
