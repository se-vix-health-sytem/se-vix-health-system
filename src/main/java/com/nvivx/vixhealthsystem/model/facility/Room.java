package com.nvivx.vixhealthsystem.model.facility;

import jakarta.persistence.*;

/**
 * Represents a generic room inside a medical facility.
 * <p>
 * Rooms are stored using Single Table Inheritance.
 * Each room belongs to a medical facility and can be specialized
 * into different room types such as offices or inpatient rooms.
 * <p>
 * The room type is determined by the discriminator column "type".
 *
 * @see MedicalFacility
 * @see Office
 * @see InternationRoom
 */
@Entity
@Table(name = "Rooms")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "type",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class Room {

    /** Unique room identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Medical facility to which this room belongs. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private MedicalFacility medicalFacility;

    /**
     * Room number or room code.
     * Used to uniquely identify the room inside a facility.
     */
    @Column(name = "room_number", nullable = false)
    private String number;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Room() {
    }

    /**
     * Creates a room with the specified room number.
     *
     * @param number the room number
     */
    public Room(String number) {
        this.number = number;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the unique room identifier.
     *
     * @return the room ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique room identifier.
     *
     * @param id the room ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the medical facility to which this room belongs.
     *
     * @return the medical facility
     */
    public MedicalFacility getMedicalFacility() {
        return medicalFacility;
    }

    /**
     * Sets the medical facility to which this room belongs.
     *
     * @param medicalFacility the medical facility to set
     */
    public void setMedicalFacility(MedicalFacility medicalFacility) {
        this.medicalFacility = medicalFacility;
    }

    /**
     * Returns the room number or code.
     *
     * @return the room number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the room number or code.
     *
     * @param number the room number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }
}