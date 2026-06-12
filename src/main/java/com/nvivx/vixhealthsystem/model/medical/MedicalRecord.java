package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.*;

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
    private List<MedicalCondition> conditions = new ArrayList<>();

    /**
     * Prescriptions associated with this record.
     */
    @OneToMany(mappedBy = "medicalRecord")
    private List<Prescription> prescriptions = new ArrayList<>();

    /**
     * Surgeries associated with this record.
     */
    @OneToMany(mappedBy = "medicalRecord")
    private List<Surgery> surgeries = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public MedicalRecord() {
    }

    /**
     * Creates a medical record with the patient's primary health data.
     *
     * @param height    the patient height in centimeters
     * @param weight    the patient weight in kilograms
     * @param bloodType the patient blood type
     */
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

    /**
     * Returns the unique medical record identifier.
     *
     * @return the medical record ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique medical record identifier.
     *
     * @param id the medical record ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the patient owning this medical record.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the patient owning this medical record.
     *
     * @param patient the patient to set
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Returns the patient height in centimeters.
     *
     * @return the height
     */
    public Float getHeight() {
        return height;
    }

    /**
     * Sets the patient height in centimeters.
     *
     * @param height the height to set
     */
    public void setHeight(Float height) {
        this.height = height;
    }

    /**
     * Returns the patient weight in kilograms.
     *
     * @return the weight
     */
    public Float getWeight() {
        return weight;
    }

    /**
     * Sets the patient weight in kilograms.
     *
     * @param weight the weight to set
     */
    public void setWeight(Float weight) {
        this.weight = weight;
    }

    /**
     * Returns the patient blood type.
     *
     * @return the blood type
     */
    public String getBloodType() {
        return bloodType;
    }

    /**
     * Sets the patient blood type.
     *
     * @param bloodType the blood type to set
     */
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    /**
     * Returns the known allergies as plain text.
     *
     * @return the allergies
     */
    public String getAllergies() {
        return allergies;
    }

    /**
     * Sets the known allergies as plain text.
     *
     * @param allergies the allergies to set
     */
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    /**
     * Returns the vaccinations received as plain text.
     *
     * @return the vaccines
     */
    public String getVaccines() {
        return vaccines;
    }

    /**
     * Sets the vaccinations received as plain text.
     *
     * @param vaccines the vaccines to set
     */
    public void setVaccines(String vaccines) {
        this.vaccines = vaccines;
    }

    /**
     * Returns the list of medical conditions associated with this record.
     *
     * @return the list of medical conditions
     */
    public List<MedicalCondition> getConditions() {
        return conditions;
    }

    /**
     * Sets the list of medical conditions associated with this record.
     *
     * @param conditions the list of medical conditions to set
     */
    public void setConditions(List<MedicalCondition> conditions) {
        this.conditions = conditions;
    }

    /**
     * Returns the list of prescriptions associated with this record.
     *
     * @return the list of prescriptions
     */
    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    /**
     * Sets the list of prescriptions associated with this record.
     *
     * @param prescriptions the list of prescriptions to set
     */
    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    /**
     * Returns the list of surgeries associated with this record.
     *
     * @return the list of surgeries
     */
    public List<Surgery> getSurgeries() {
        return surgeries;
    }

    /**
     * Sets the list of surgeries associated with this record.
     *
     * @param surgeries the list of surgeries to set
     */
    public void setSurgeries(List<Surgery> surgeries) {
        this.surgeries = surgeries;
    }

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Adds a condition to this record and sets the back-reference.
     *
     * @param condition the condition to add
     */
    public void addCondition(MedicalCondition condition) {
        this.conditions.add(condition);
        condition.setMedicalRecord(this);
    }

    /**
     * Adds a prescription to this record and sets the back-reference.
     *
     * @param prescription the prescription to add
     */
    public void addPrescription(Prescription prescription) {
        this.prescriptions.add(prescription);
        prescription.setMedicalRecord(this);
    }

    /**
     * Adds a surgery to this record and sets the back-reference.
     *
     * @param surgery the surgery to add
     */
    public void addSurgery(Surgery surgery) {
        this.surgeries.add(surgery);
        surgery.setMedicalRecord(this);
    }
}
