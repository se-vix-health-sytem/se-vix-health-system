package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a medical prescription issued by a medical specialist.
 * <p>
 * A prescription belongs to a medical record and identifies
 * the medication prescribed to the patient.
 *
 * @see MedicalRecord
 * @see MedicalSpecialist
 */
@Getter
@Setter
@Entity
@Table(name = "Prescriptions")
public class Prescription {

    /**
     * Unique prescription identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Medical record associated with the prescription.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    /**
     * Specialist who issued the prescription.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_specialist_id", nullable = false)
    private MedicalSpecialist medicalSpecialist;

    /**
     * Date and time when the prescription was issued.
     */
    @Column(name = "prescription_date", nullable = false)
    private LocalDateTime dateTime;

    /**
     * Prescribed medication.
     */
    @Column(name = "medication", nullable = false)
    private String medication;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public Prescription() {
    }

    public Prescription(
            LocalDateTime dateTime,
            String medication
    ) {
        this.dateTime = dateTime;
        this.medication = medication;
    }
}