package com.nvivx.vixhealthsystem.model.person.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.Prescription;
import com.nvivx.vixhealthsystem.model.medical.Surgery;
import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents medical personnel such as doctors, surgeons,
 * specialists and nurses.
 * <p>
 * The specialty identifies the medical field
 * (e.g. Cardiology, Neurology, Orthopedics).
 * <p>
 * The license number uniquely identifies the professional
 * medical license issued by the appropriate authority.
 *
 * @see Employee
 * @see Prescription
 * @see Appointment
 */
@Entity
@DiscriminatorValue("MEDICAL_SPECIALIST")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class MedicalSpecialist extends Employee {

    /**
     * Medical specialization field (e.g. Cardiology, Neurology).
     */
    @Column(name = "specialty")
    private String specialty;

    /**
     * Professional license identifier issued by the appropriate authority.
     */
    @Column(name = "license_number")
    private String licenseNumber;

    /**
     * Appointments assigned to this specialist.
     * Stored as a transient field — managed via JSON, not the SQL database.
     */
    @Transient
    private List<Appointment> appointments = new ArrayList<>();

    /**
     * Surgeries performed by this specialist.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "medicalSpecialist", fetch = FetchType.LAZY)
    private List<Surgery> surgeries = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public MedicalSpecialist() {
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the medical specialization field.
     *
     * @return the specialty
     */
    public String getSpecialty() {
        return specialty;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the medical specialization field.
     *
     * @param specialty the specialty to set
     */
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the professional license identifier.
     *
     * @return the license number
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the professional license identifier.
     *
     * @param licenseNumber the license number to set
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the list of appointments assigned to this specialist.
     *
     * @return the list of appointments
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the list of appointments assigned to this specialist.
     *
     * @param appointments the list of appointments to set
     */
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the list of surgeries performed by this specialist.
     *
     * @return the list of surgeries
     */
    public List<Surgery> getSurgeries() {
        return surgeries;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the list of surgeries performed by this specialist.
     *
     * @param surgeries the list of surgeries to set
     */
    public void setSurgeries(List<Surgery> surgeries) {
        this.surgeries = surgeries;
    }
    /// @endcond

    @Override
    public Role getSystemRole() { return Role.ROLE_MEDICAL_SPECIALIST; }

    @Override
    public EmployeeType getEmployeeType() { return EmployeeType.MEDICAL_SPECIALIST; }

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Issues a prescription for a patient by adding it to their medical record.
     * Delegates to the medical record's own prescription-addition logic,
     * which also sets the back-reference on the prescription.
     *
     * @param p  the patient receiving the prescription
     * @param pr the prescription to issue
     */
    public void appPrescriptionForPatient(
            Patient p,
            Prescription pr
    ) {
        pr.setMedicalSpecialist(this);
        p.getMedicalRecord().addPrescription(pr);
    }

    /**
     * Schedules a surgery for a patient by adding it to their medical record
     * and setting the performing specialist to {@code this}.
     *
     * @param p the patient undergoing the surgery
     * @param s the surgery to schedule
     */
    public void scheduleSurgeryForPatient(Patient p, Surgery s) {
        s.setMedicalSpecialist(this);
        p.getMedicalRecord().addSurgery(s);
    }
}
