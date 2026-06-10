package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete medical record of a patient.
 * <p>
 * Each patient owns exactly one medical record.
 * <p>
 * The medical record stores general health information
 * and provides access to medical conditions,
 * prescriptions and surgeries associated with the patient.
 *
 * @see Patient
 * @see MedicalCondition
 * @see Prescription
 * @see Surgery
 */
@Getter
@Setter
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
     * <p>
     * Stored as plain text for simplicity.
     */
    @Column(name = "allergies")
    private String allergies;

    /**
     * Vaccinations received by the patient.
     * <p>
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

    // Helper method to add a single condition (useful for services)
    public void addCondition(MedicalCondition condition) {
        this.conditions.add(condition);
        condition.setMedicalRecord(this);
    }

    // Helper method to add a prescription
    public void addPrescription(Prescription prescription) {
        this.prescriptions.add(prescription);
        prescription.setMedicalRecord(this);
    }

    // Helper method to add a surgery
    public void addSurgery(Surgery surgery) {
        this.surgeries.add(surgery);
        surgery.setMedicalRecord(this);
    }

}