package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.medical.Prescription;
import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Represents medical personnel such as doctors, surgeons,
 * specialists and nurses.
 *
 * The specialty identifies the medical field
 * (e.g. Cardiology, Neurology, Orthopedics).
 *
 * The license number uniquely identifies the professional
 * medical license issued by the appropriate authority.
 *
 * @see Employee
 * @see Prescription
 */
@Entity
@DiscriminatorValue("MEDICAL_SPECIALIST")
public class MedicalSpecialist extends Employee {

    /**
     * Medical specialization field.
     */
    @Column(name = "specialty")
    private String specialty;

    /**
     * Professional license identifier.
     */
    @Column(name = "license_number")
    private String licenseNumber;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Issues a prescription for a patient.
     *
     * The actual persistence is handled by the service layer.
     *
     * @param p patient receiving the prescription
     * @param pr prescription details
     */
    public void appPrescriptionForPatient(
            Patient p,
            Prescription pr
    ) {

    }
}