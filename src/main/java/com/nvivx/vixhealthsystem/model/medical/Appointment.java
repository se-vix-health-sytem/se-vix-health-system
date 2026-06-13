package com.nvivx.vixhealthsystem.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;

import java.time.LocalDateTime;

/**
 * Represents a scheduled appointment between a patient and a medical specialist.
 * <p>
 * Appointments are stored as JSON (appointments.json), not in the SQL database.
 * Each appointment has a status that tracks its lifecycle.
 * <p>
 * Possible status values: PENDING, CONFIRMED, CANCELLED, RESCHEDULED, COMPLETED.
 *
 * @see Patient
 * @see MedicalSpecialist
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {

    /**
     * Unique appointment identifier.
     */
    private int id;

    /**
     * Scheduled date and time of the appointment.
     */
    private LocalDateTime dateTime;

    /**
     * Duration of the appointment in minutes.
     */
    private int duration;

    /**
     * Optional notes for the appointment.
     */
    private String notes;

    /**
     * Patient attending the appointment.
     */
    private Patient patient;

    /**
     * Medical specialist conducting the appointment.
     */
    private MedicalSpecialist medicalSpecialist;

    /**
     * Whether the appointment has been paid.
     */
    private boolean paymentStatus;

    /**
     * Current lifecycle status of the appointment.
     * Possible values: PENDING, CONFIRMED, CANCELLED, RESCHEDULED, COMPLETED.
     */
    private String status;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required for JSON deserialization.
     */
    public Appointment() {
    }

    /**
     * Creates an appointment with basic scheduling details.
     * Status defaults to CONFIRMED.
     *
     * @param id       the appointment identifier
     * @param dateTime the scheduled date and time
     * @param duration the duration in minutes
     * @param notes    optional notes
     */
    public Appointment(
            int id,
            LocalDateTime dateTime,
            int duration,
            String notes
    ) {
        this.id = id;
        this.dateTime = dateTime;
        this.duration = duration;
        this.notes = notes;
        this.status = "CONFIRMED";
    }

    /**
     * Creates a full appointment with patient and specialist information.
     * Status defaults to CONFIRMED.
     *
     * @param dateTime          the scheduled date and time
     * @param duration          the duration in minutes
     * @param notes             optional notes
     * @param patient           the attending patient
     * @param medicalSpecialist the conducting specialist
     */
    public Appointment(
            LocalDateTime dateTime,
            int duration,
            String notes,
            Patient patient,
            MedicalSpecialist medicalSpecialist
    ) {
        this.dateTime = dateTime;
        this.duration = duration;
        this.notes = notes;
        this.patient = patient;
        this.medicalSpecialist = medicalSpecialist;
        this.status = "CONFIRMED";
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the unique appointment identifier.
     *
     * @return the appointment ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique appointment identifier.
     *
     * @param id the appointment ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the scheduled date and time.
     *
     * @return the appointment date and time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Sets the scheduled date and time.
     *
     * @param dateTime the appointment date and time to set
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Returns the duration of the appointment in minutes.
     *
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the appointment in minutes.
     *
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Returns the optional notes for the appointment.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the optional notes for the appointment.
     *
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the patient attending the appointment.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the patient attending the appointment.
     *
     * @param patient the patient to set
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Returns the medical specialist conducting the appointment.
     *
     * @return the medical specialist
     */
    public MedicalSpecialist getMedicalSpecialist() {
        return medicalSpecialist;
    }

    /**
     * Sets the medical specialist conducting the appointment.
     *
     * @param medicalSpecialist the medical specialist to set
     */
    public void setMedicalSpecialist(MedicalSpecialist medicalSpecialist) {
        this.medicalSpecialist = medicalSpecialist;
    }

    /**
     * Returns whether the appointment has been paid.
     *
     * @return true if paid, false otherwise
     */
    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Sets the payment status of the appointment.
     *
     * @param paymentStatus true if paid, false otherwise
     */
    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Returns the current lifecycle status of the appointment.
     * Possible values: PENDING, CONFIRMED, CANCELLED, RESCHEDULED, COMPLETED.
     *
     * @return the appointment status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current lifecycle status of the appointment.
     *
     * @param status the status to set (PENDING, CONFIRMED, CANCELLED, RESCHEDULED, COMPLETED)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Checks whether the appointment is currently active.
     * An appointment is active if it has not been cancelled or completed.
     *
     * @return true if the appointment is active, false otherwise
     */
    @JsonIgnore
    public boolean isActive() {
        return status != null
                && !"CANCELLED".equals(status)
                && !"COMPLETED".equals(status);
    }

    /**
     * Checks whether the appointment can be cancelled.
     * An appointment can be cancelled if it has not already been
     * cancelled or completed.
     *
     * @return true if the appointment is cancellable, false otherwise
     */
    @JsonIgnore
    public boolean isCancellable() {
        return status != null
                && !"CANCELLED".equals(status)
                && !"COMPLETED".equals(status);
    }
}
