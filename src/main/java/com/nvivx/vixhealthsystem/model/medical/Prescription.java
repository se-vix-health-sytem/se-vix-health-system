package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import jakarta.persistence.*;

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

    /**
     * Default constructor required by JPA.
     */
    public Prescription() {
    }

    /**
     * Creates a prescription with the specified date and medication.
     *
     * @param dateTime   the date and time the prescription was issued
     * @param medication the prescribed medication
     */
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

    /**
     * Returns the unique prescription identifier.
     *
     * @return the prescription ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique prescription identifier.
     *
     * @param id the prescription ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the medical record associated with this prescription.
     *
     * @return the medical record
     */
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    /**
     * Sets the medical record associated with this prescription.
     *
     * @param medicalRecord the medical record to set
     */
    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    /**
     * Returns the specialist who issued the prescription.
     *
     * @return the medical specialist
     */
    public MedicalSpecialist getMedicalSpecialist() {
        return medicalSpecialist;
    }

    /**
     * Sets the specialist who issued the prescription.
     *
     * @param medicalSpecialist the medical specialist to set
     */
    public void setMedicalSpecialist(MedicalSpecialist medicalSpecialist) {
        this.medicalSpecialist = medicalSpecialist;
    }

    /**
     * Returns the date and time the prescription was issued.
     *
     * @return the prescription date and time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time the prescription was issued.
     *
     * @param dateTime the prescription date and time to set
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Returns the prescribed medication.
     *
     * @return the medication
     */
    public String getMedication() {
        return medication;
    }

    /**
     * Sets the prescribed medication.
     *
     * @param medication the medication to set
     */
    public void setMedication(String medication) {
        this.medication = medication;
    }
}
