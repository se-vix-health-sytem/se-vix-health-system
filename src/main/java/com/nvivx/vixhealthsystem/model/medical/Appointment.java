package com.nvivx.vixhealthsystem.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a medical appointment.
 * Stored as JSON (appointments.json), not in the SQL database.
 * Contains patient, medical specialist, datetime, status, and payment info.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {

    @Setter
    private int id;
    @Setter
    private LocalDateTime dateTime;
    @Setter
    private int duration;        // in minutes
    @Setter
    private String notes;
    @Setter
    private String status;       // CONFIRMED, CANCELLED, RESCHEDULED, COMPLETED
    @Setter
    private String paymentStatus; // PAID, UNPAID

    // Stored as IDs for lightweight JSON, full objects kept for in-memory use
    @Setter
    private long patientId;
    @Setter
    private long doctorId;

    @JsonIgnoreProperties({"appointments", "medicalRecord"})
    private Patient patient;

    @JsonIgnoreProperties({"password"})
    private MedicalSpecialist doctor;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public Appointment() {}

    /**
     * Full constructor used by services when creating a new appointment.
     */
    public Appointment(LocalDateTime dateTime, int duration, String notes,
                       Patient patient, MedicalSpecialist doctor) {
        this.dateTime = dateTime;
        this.duration = duration;
        this.notes = notes;
        this.patient = patient;
        this.doctor = doctor;
        this.status = "CONFIRMED";
        this.paymentStatus = "UNPAID";
        if (patient != null) this.patientId = patient.getId();
        if (doctor != null) this.doctorId = doctor.getId();
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public int getId() { return id; }

    public LocalDateTime getDateTime() { return dateTime; }

    public int getDuration() { return duration; }

    public String getNotes() { return notes; }

    public String getStatus() { return status; }

    public String getPaymentStatus() { return paymentStatus; }

    public long getPatientId() { return patientId; }

    public long getDoctorId() { return doctorId; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) {
        this.patient = patient;
        if (patient != null) this.patientId = patient.getId();
    }

    public MedicalSpecialist getDoctor() { return doctor; }
    public void setDoctor(MedicalSpecialist doctor) {
        this.doctor = doctor;
        if (doctor != null) this.doctorId = (int) (long) doctor.getId();
    }
}