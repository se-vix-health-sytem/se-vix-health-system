package com.nvivx.vixhealthsystem.model.person.employee;
import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.facility.Office;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.medical.Prescription;

/**
 * Represents medical staff like Doctors, nurses, etc.
 *
 * The 'specialty' attribute indicates the medical field (e.g., "Cardiology").
 * The 'role' attribute distinguishes levels (e.g., nurse level N1 vs N10 have different tasks).
 * The 'licenseNumber' is the professional license identifier.
 *
 * @see Employee
 */

public class MedicalSpecialist extends Employee {
    private String specialty;
    private String role;
    private String licenseNumber;
    private Department department;
    private Office office;

    // ========== Getters and Setters ==========

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * Returns the specialist's unique professional license number.
     * Used for verification and legal purposes.
     *
     * @return the license number as a String
     */

    public String getLicenseNumber() {
        return licenseNumber;
    }

    // ========== Business Methods ==========

    /**
     * Issues a prescription for a specific patient.
     * This creates a record linking the patient to the prescribed medication/treatment.
     *
     * @param p the patient receiving the prescription
     * @param pr the prescription details (medication, dosage, notes, etc.)
     */

    public void appPrescriptionForPatient(Patient p, Prescription pr) {

        // Will insert into the database: links patient, medical specialist, and prescription
        // Also updates the patient's medical record

    }
}