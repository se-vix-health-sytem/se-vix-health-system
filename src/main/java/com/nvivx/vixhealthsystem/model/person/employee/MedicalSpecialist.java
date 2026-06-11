package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.Prescription;
import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

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

    /**
     * Returns the medical specialization field.
     *
     * @return the specialty
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Sets the medical specialization field.
     *
     * @param specialty the specialty to set
     */
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    /**
     * Returns the professional license identifier.
     *
     * @return the license number
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Sets the professional license identifier.
     *
     * @param licenseNumber the license number to set
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * Returns the list of appointments assigned to this specialist.
     *
     * @return the list of appointments
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Sets the list of appointments assigned to this specialist.
     *
     * @param appointments the list of appointments to set
     */
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Issues a prescription for a patient.
     * <p>
     * The actual persistence is handled by the service layer.
     *
     * @param p  the patient receiving the prescription
     * @param pr the prescription details
     */
    public void appPrescriptionForPatient(
            Patient p,
            Prescription pr
    ) {

    }
}
