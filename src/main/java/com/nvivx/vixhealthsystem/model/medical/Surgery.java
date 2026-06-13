package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a surgical procedure performed on a patient.
 * <p>
 * Each surgery belongs to a medical record and is performed
 * inside a specialized room at a scheduled date and time.
 *
 * @see MedicalRecord
 * @see SpecializedRoom
 */
@Entity
@Table(name = "Surgeries")
public class Surgery {

    /**
     * Unique surgery identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Medical record associated with this surgery.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    /**
     * Room where the surgery is performed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialized_room_id", nullable = false)
    private SpecializedRoom specializedRoom;

    /**
     * Medical specialist who performed this surgery.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_specialist_id")
    private MedicalSpecialist medicalSpecialist;

    /**
     * Scheduled date and time of the surgery.
     */
    @Column(name = "surgery_date", nullable = false)
    private LocalDateTime dateTime;

    /**
     * Surgery name or procedure type.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Detailed surgery description.
     */
    @Column(name = "description")
    private String description;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Surgery() {
    }

    /**
     * Creates a surgery with the specified details.
     *
     * @param dateTime        the scheduled date and time
     * @param name            the surgery name or procedure type
     * @param description     the detailed description
     * @param specializedRoom the room where the surgery takes place
     */
    public Surgery(
            LocalDateTime dateTime,
            String name,
            String description,
            SpecializedRoom specializedRoom
    ) {
        this.dateTime = dateTime;
        this.name = name;
        this.description = description;
        this.specializedRoom = specializedRoom;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the unique surgery identifier.
     *
     * @return the surgery ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique surgery identifier.
     *
     * @param id the surgery ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the medical record associated with this surgery.
     *
     * @return the medical record
     */
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    /**
     * Sets the medical record associated with this surgery.
     *
     * @param medicalRecord the medical record to set
     */
    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    /**
     * Returns the room where the surgery is performed.
     *
     * @return the specialized room
     */
    public SpecializedRoom getSpecializedRoom() {
        return specializedRoom;
    }

    /**
     * Sets the room where the surgery is performed.
     *
     * @param specializedRoom the specialized room to set
     */
    public void setSpecializedRoom(SpecializedRoom specializedRoom) {
        this.specializedRoom = specializedRoom;
    }

    /**
     * Returns the scheduled date and time of the surgery.
     *
     * @return the surgery date and time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Sets the scheduled date and time of the surgery.
     *
     * @param dateTime the surgery date and time to set
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Returns the surgery name or procedure type.
     *
     * @return the surgery name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the surgery name or procedure type.
     *
     * @param name the surgery name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the detailed surgery description.
     *
     * @return the surgery description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the detailed surgery description.
     *
     * @param description the surgery description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public MedicalSpecialist getMedicalSpecialist() {
        return medicalSpecialist;
    }

    public void setMedicalSpecialist(MedicalSpecialist medicalSpecialist) {
        this.medicalSpecialist = medicalSpecialist;
    }
}
