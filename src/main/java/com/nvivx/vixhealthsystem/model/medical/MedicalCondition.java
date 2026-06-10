package com.nvivx.vixhealthsystem.model.medical;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents a medical condition diagnosed in a patient.
 * <p>
 * Medical conditions are associated with a medical record and
 * contain information regarding diagnosis, treatment and type.
 *
 * @see MedicalRecord
 */
@Getter
@Setter
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
     * Creates a medical condition.
     *
     * @param name condition name
     * @param dateOfDiagnosis diagnosis date
     * @param type condition type
     * @param description condition description
     * @param treatment prescribed treatment
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


}