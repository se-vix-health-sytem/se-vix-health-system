package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

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

    @Override
    public Role getSystemRole() { return Role.ROLE_SECRETARY; }

    @Override
    public EmployeeType getEmployeeType() { return EmployeeType.SECRETARY; }

    // =====================================================
    // APPOINTMENT MANAGEMENT
    // =====================================================

    /**
     * Books an appointment for a patient with a specific specialist on behalf of the patient.
     * Delegates to the patient's own appointment-creation logic.
     *
     * @param p  the patient
     * @param m  the medical specialist
     * @param dt the appointment date and time
     * @return the newly created appointment
     */
    public Appointment makeAppointmentForPatient(
            Patient p,
            MedicalSpecialist m,
            LocalDateTime dt
    ) {
        return p.makeAppointment(m, dt);
    }

    /**
     * Reschedules an existing appointment to a new date on behalf of a patient.
     * Delegates to the patient's own reschedule logic.
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
        p.rescheduleAppointment(dtOld, dtNew);
    }

    /**
     * Cancels an appointment on behalf of a patient.
     * Delegates to the patient's own cancellation logic.
     *
     * @param p  the patient
     * @param dt the appointment date and time to cancel
     */
    public void cancelAppointmentForPatient(
            Patient p,
            LocalDateTime dt
    ) {
        p.cancelAppointment(dt);
    }

    // =====================================================
    // ROOM MANAGEMENT
    // =====================================================

    /**
     * Assigns a patient to an inpatient room.
     * Delegates to the room's own admission logic.
     *
     * @param ir the inpatient room to assign the patient to
     * @param p  the patient to assign
     * @throws Exception if the room is full
     */
    public void setPatientInRoom(
            InternationRoom ir,
            Patient p
    ) throws Exception {
        ir.addPatient(p);
    }

    /**
     * Discharges a patient from an inpatient room.
     * Delegates to the room's own removal logic.
     *
     * @param ir the inpatient room the patient is currently in
     * @param p  the patient to discharge
     * @throws Exception if the patient is not in the room
     */
    public void dismissPatient(
            InternationRoom ir,
            Patient p
    ) throws Exception {
        ir.removePatient(p);
    }
}
