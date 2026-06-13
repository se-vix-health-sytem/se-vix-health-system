package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles administrative tasks such as appointment scheduling,
 * patient admissions and discharges.
 * <p>
 * The secretary role identifies the secretary's specialization:
 * <ul>
 *   <li>Front Office</li>
 *   <li>Admissions</li>
 *   <li>Billing</li>
 * </ul>
 *
 * @see Employee
 */
@Entity
@DiscriminatorValue("SECRETARY")
public class Secretary extends Employee {

    /**
     * Secretary specialization type (e.g. Front Office, Admissions, Billing).
     */
    @Column(name = "secretary_type")
    private String role;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Secretary() {
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the secretary specialization role.
     *
     * @return the secretary role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the secretary specialization role.
     *
     * @param role the secretary role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    // =====================================================
    // APPOINTMENT MANAGEMENT
    // =====================================================

    /**
     * Books an appointment for a patient with a specific specialist.
     *
     * @param p  the patient
     * @param m  the medical specialist
     * @param dt the appointment date and time
     */
    public void makeAppointmentForPatient(
            Patient p,
            MedicalSpecialist m,
            LocalDateTime dt
    ) {

    }

    /**
     * Reschedules an existing appointment to a new date.
     *
     * @param p     the patient
     * @param dtOld the current appointment date and time
     * @param dtNew the new appointment date and time
     */
    public void rescheduleAppointmentForPatient(
            Patient p,
            LocalDateTime dtOld,
            LocalDateTime dtNew
    ) {

    }

    /**
     * Cancels an appointment for a patient.
     *
     * @param p  the patient
     * @param dt the appointment date and time to cancel
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
     * Displays room availability across the facility.
     */
    public List<InternationRoom> getRoomAvailability() {
        return null;
    }

    /**
     * Assigns a patient to an inpatient room.
     *
     * @param ir the inpatient room
     * @param p  the patient to assign
     */
    public void setPatientInRoom(
            InternationRoom ir,
            Patient p
    ) {

    }

    /**
     * Discharges a patient from the facility.
     *
     * @param p the patient to discharge
     */
    public void dismissPatient(
            Patient p
    ) {

    }
}
