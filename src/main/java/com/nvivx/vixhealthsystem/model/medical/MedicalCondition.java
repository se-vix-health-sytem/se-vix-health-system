package com.nvivx.vixhealthsystem.model.medical;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Represents a medical condition diagnosed in a patient.
 * <p>
 * Medical conditions are associated with a medical record and
 * contain information regarding diagnosis, treatment and type.
 *
 * @see MedicalRecord
 */
@Entity
@Table(name = "MedicalConditions")
public class MedicalCondition {

    /**
     * Unique medical condition identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Medical record associated with this condition.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    /**
     * Condition name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Date of diagnosis.
     */
    @Column(name = "diagnosis_date")
    private LocalDate dateOfDiagnosis;

    /**
     * Condition category or type.
     */
    @Column(name = "type")
    private String type;

    /**
     * Detailed condition description.
     */
    @Column(name = "description")
    private String description;

    /**
     * Treatment associated with the condition.
     */
    @Column(name = "treatment")
    private String treatment;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public MedicalCondition() {
    }

    /**
     * Creates a medical condition with the specified details.
     *
     * @param name            the condition name
     * @param dateOfDiagnosis the date of diagnosis
     * @param type            the condition category or type
     * @param description     the detailed description
     * @param treatment       the prescribed treatment
     */
    public MedicalCondition(
            String name,
            LocalDate dateOfDiagnosis,
            String type,
            String description,
            String treatment
    ) {
        this.name = name;
        this.dateOfDiagnosis = dateOfDiagnosis;
        this.type = type;
        this.description = description;
        this.treatment = treatment;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the unique medical condition identifier.
     *
     * @return the condition ID
     */
    public Long getId() {
        return id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the unique medical condition identifier.
     *
     * @param id the condition ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the medical record associated with this condition.
     *
     * @return the medical record
     */
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the medical record associated with this condition.
     *
     * @param medicalRecord the medical record to set
     */
    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the condition name.
     *
     * @return the condition name
     */
    public String getName() {
        return name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the condition name.
     *
     * @param name the condition name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the date of diagnosis.
     *
     * @return the diagnosis date
     */
    public LocalDate getDateOfDiagnosis() {
        return dateOfDiagnosis;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the date of diagnosis.
     *
     * @param dateOfDiagnosis the diagnosis date to set
     */
    public void setDateOfDiagnosis(LocalDate dateOfDiagnosis) {
        this.dateOfDiagnosis = dateOfDiagnosis;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the condition category or type.
     *
     * @return the condition type
     */
    public String getType() {
        return type;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the condition category or type.
     *
     * @param type the condition type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the detailed condition description.
     *
     * @return the condition description
     */
    public String getDescription() {
        return description;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the detailed condition description.
     *
     * @param description the condition description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the treatment associated with the condition.
     *
     * @return the treatment
     */
    public String getTreatment() {
        return treatment;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the treatment associated with the condition.
     *
     * @param treatment the treatment to set
     */
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    /// @endcond
}
