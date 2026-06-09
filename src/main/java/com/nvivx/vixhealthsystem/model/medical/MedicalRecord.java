package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete medical record of a patient.
 *
 * Each patient owns exactly one medical record.
 *
 * The medical record stores general health information
 * and provides access to medical conditions,
 * prescriptions and surgeries associated with the patient.
 *
 * @see Patient
 * @see MedicalCondition
 * @see Prescription
 * @see Surgery
 */
@Entity
@Table(name = "MedicalRecords")
public class MedicalRecord {

    /**
     * Unique medical record identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Patient owning this medical record.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "patient_id",
            nullable = false,
            unique = true
    )
    private Patient patient;

    /**
     * Patient height in centimeters.
     */
    @Column(name = "height")
    private Float height;

    /**
     * Patient weight in kilograms.
     */
    @Column(name = "weight")
    private Float weight;

    /**
     * Patient blood type.
     */
    @Column(name = "blood_type")
    private String bloodType;

    /**
     * Known allergies.
     *
     * Stored as plain text for simplicity.
     */
    @Column(name = "allergies")
    private String allergies;

    /**
     * Vaccinations received by the patient.
     *
     * Stored as plain text for simplicity.
     */
    @Column(name = "vaccines")
    private String vaccines;

    /**
     * Medical conditions associated with this record.
     */
    @OneToMany(mappedBy = "medicalRecord")
    private List<MedicalCondition> conditions =
            new ArrayList<>();

    /**
     * Prescriptions associated with this record.
     */
    @OneToMany(mappedBy = "medicalRecord")
    private List<Prescription> prescriptions =
            new ArrayList<>();

    /**
     * Surgeries associated with this record.
     */
    @OneToMany(mappedBy = "medicalRecord")
    private List<Surgery> surgeries =
            new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public MedicalRecord() {
    }

    public MedicalRecord(
            Float height,
            Float weight,
            String bloodType
    ) {
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getVaccines() {
        return vaccines;
    }

    public void setVaccines(String vaccines) {
        this.vaccines = vaccines;
    }

    public List<MedicalCondition> getConditions() {
        return conditions;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public List<Surgery> getSurgeries() {
        return surgeries;
    }
}