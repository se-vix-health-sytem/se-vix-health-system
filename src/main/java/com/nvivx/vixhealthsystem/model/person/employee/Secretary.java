package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

/**
 * Handles administrative tasks such as appointment scheduling,
 * patient admissions and discharges.
 *
 * The secretary type identifies the secretary's specialization:
 * - Front Office
 * - Admissions
 * - Billing
 *
 * @see Employee
 */
@Entity
@DiscriminatorValue("SECRETARY")
public class Secretary extends Employee {

    /**
     * Secretary specialization type.
     */
    @Column(name = "secretary_type")
    private String role;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // =====================================================
    // APPOINTMENT MANAGEMENT
    // =====================================================

    /**
     * Books an appointment for a patient.
     *
     * @param p patient
     * @param m medical specialist
     * @param dt appointment date and time
     */
    public void makeAppointmentForPatient(
            Patient p,
            MedicalSpecialist m,
            LocalDateTime dt
    ) {

    }

    /**
     * Reschedules an appointment.
     *
     * @param p patient
     * @param dtOld current appointment date
     * @param dtNew new appointment date
     */
    public void rescheduleAppointmentForPatient(
            Patient p,
            LocalDateTime dtOld,
            LocalDateTime dtNew
    ) {

    }

    /**
     * Cancels an appointment.
     *
     * @param p patient
     * @param dt appointment date
     */
    public void cancelAppointmentForPatient(
            Patient p,
            LocalDateTime dt
    ) {

    }

    // =====================================================
    // ROOM MANAGEMENT
    // =====================================================

    /**
     * Displays room availability.
     */
    public void getRoomAvailability() {

    }

    /**
     * Assigns a patient to a room.
     *
     * @param ir room
     * @param p patient
     */
    public void setPatientInRoom(
            InternationRoom ir,
            Patient p
    ) {

    }

    /**
     * Discharges a patient.
     *
     * @param p patient to dismiss
     */
    public void dismissPatient(
            Patient p
    ) {

    }
}