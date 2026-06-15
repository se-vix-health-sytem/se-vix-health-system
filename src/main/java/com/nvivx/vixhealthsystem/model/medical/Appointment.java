package com.nvivx.vixhealthsystem.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nvivx.vixhealthsystem.model.enums.AppointmentStatus;
import com.nvivx.vixhealthsystem.model.enums.PaymentStatus;
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
     * Payment status of the appointment.
     */
    private PaymentStatus paymentStatus;

    /**
     * Current lifecycle status of the appointment.
     */
    private AppointmentStatus status;

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
        this.status = AppointmentStatus.CONFIRMED;
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
        this.status = AppointmentStatus.CONFIRMED;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the unique appointment identifier.
     *
     * @return the appointment ID
     */
    public int getId() {
        return id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the unique appointment identifier.
     *
     * @param id the appointment ID to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the scheduled date and time.
     *
     * @return the appointment date and time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the scheduled date and time.
     *
     * @param dateTime the appointment date and time to set
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the duration of the appointment in minutes.
     *
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the duration of the appointment in minutes.
     *
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the optional notes for the appointment.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the optional notes for the appointment.
     *
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the patient attending the appointment.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the patient attending the appointment.
     *
     * @param patient the patient to set
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the medical specialist conducting the appointment.
     *
     * @return the medical specialist
     */
    public MedicalSpecialist getMedicalSpecialist() {
        return medicalSpecialist;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the medical specialist conducting the appointment.
     *
     * @param medicalSpecialist the medical specialist to set
     */
    public void setMedicalSpecialist(MedicalSpecialist medicalSpecialist) {
        this.medicalSpecialist = medicalSpecialist;
    }
    /// @endcond

    /**
     * Returns the payment status enum.
     */
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Returns whether the appointment has been paid (boolean convenience getter).
     */
    public boolean isPaid() {
        return paymentStatus == PaymentStatus.PAID;
    }

    /**
     * Sets the payment status using the enum.
     */
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Sets the payment status from a boolean (backward-compatible: true → PAID, false → UNPAID).
     */
    @JsonIgnore
    public void setPaymentStatus(boolean paid) {
        this.paymentStatus = paid ? PaymentStatus.PAID : PaymentStatus.UNPAID;
    }

    /**
     * Returns the current lifecycle status as a String (backward-compatible for templates and controllers).
     */
    public String getStatus() {
        return status != null ? status.name() : null;
    }

    /**
     * Returns the current lifecycle status enum.
     */
    public AppointmentStatus getStatusEnum() {
        return status;
    }

    /**
     * Sets the lifecycle status from a String (backward-compatible).
     */
    public void setStatus(String status) {
        this.status = status != null ? AppointmentStatus.valueOf(status) : null;
    }

    /**
     * Sets the lifecycle status using the enum.
     */
    public void setStatus(AppointmentStatus status) {
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
                && status != AppointmentStatus.CANCELLED
                && status != AppointmentStatus.COMPLETED;
    }

    /**
     * Checks whether the appointment can be cancelled.
     */
    @JsonIgnore
    public boolean isCancellable() {
        return status != null
                && status != AppointmentStatus.CANCELLED
                && status != AppointmentStatus.COMPLETED;
    }

    /**
     * Cancels this appointment.
     *
     * @throws IllegalStateException if the appointment cannot be cancelled
     */
    public void cancel() {
        if (!isCancellable()) {
            throw new IllegalStateException(
                    "Appointment cannot be cancelled: current status is " + status
            );
        }
        this.status = AppointmentStatus.CANCELLED;
    }

    /**
     * Reschedules this appointment to a new date and time.
     *
     * @param newDateTime the new scheduled date and time
     * @throws IllegalStateException if the appointment is cancelled or completed
     */
    public void reschedule(LocalDateTime newDateTime) {
        if (!isActive()) {
            throw new IllegalStateException(
                    "Appointment cannot be rescheduled: current status is " + status
            );
        }
        this.dateTime = newDateTime;
        this.status = AppointmentStatus.RESCHEDULED;
    }
}
