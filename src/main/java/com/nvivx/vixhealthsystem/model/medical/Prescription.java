package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a medical prescription issued by a medical specialist.
 *
 * A prescription belongs to a medical record and identifies
 * the medication prescribed to the patient.
 *
 * @see MedicalRecord
 * @see MedicalSpecialist
 */
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

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public MedicalSpecialist getMedicalSpecialist() {
        return medicalSpecialist;
    }

    public void setMedicalSpecialist(MedicalSpecialist medicalSpecialist) {
        this.medicalSpecialist = medicalSpecialist;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }
}