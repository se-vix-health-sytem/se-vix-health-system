package com.nvivx.vixhealthsystem.model.medical;

import java.time.LocalDateTime;

public class Prescription {
    private LocalDateTime dateTime;
    private String medication;

    public Prescription(LocalDateTime dateTime, String medication) {
        this.dateTime = dateTime;
        this.medication = medication;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }


    public String getMedication() {
        return medication;
    }
    public void setMedication(String medication) {
        this.medication = medication;
    }
}