package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;

import java.time.LocalDateTime;

public class Appointment {

    private int id;
    private LocalDateTime dateTime;
    private int duration;
    private String notes;
    private Patient patient;
    private MedicalSpecialist medicalSpecialist;
    private boolean paymentStatus;

    // Status for appointment tracking
    private String status;  // Possible values: PENDING, CONFIRMED, CANCELLED, RESCHEDULED, COMPLETED

    // Constructors
    public Appointment() {
    }

    public Appointment(int id, LocalDateTime dateTime, int duration, String notes) {
        this.id = id;
        this.dateTime = dateTime;
        this.duration = duration;
        this.notes = notes;
        this.status = "CONFIRMED"; // Default status
    }

    public Appointment(LocalDateTime dateTime, int duration, String notes, Patient patient, MedicalSpecialist medicalSpecialist) {
        this.dateTime = dateTime;
        this.duration = duration;
        this.notes = notes;
        this.patient = patient;
        this.medicalSpecialist = medicalSpecialist;
        this.status = "CONFIRMED";
    }

    // Getters and Setters
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public MedicalSpecialist getMedicalSpecialist() {
        return medicalSpecialist;
    }

    public void setMedicalSpecialist(MedicalSpecialist medicalSpecialist) {
        this.medicalSpecialist = medicalSpecialist;
    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // ADD THESE - Status getter and setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Helper method to check if appointment is active (not cancelled or completed)
    public boolean isActive() {
        return status != null && !"CANCELLED".equals(status) && !"COMPLETED".equals(status);
    }

    // Helper method to check if appointment can be cancelled
    public boolean isCancellable() {
        return status != null && !"CANCELLED".equals(status) && !"COMPLETED".equals(status);
    }
}