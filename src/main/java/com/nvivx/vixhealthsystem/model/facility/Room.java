package com.nvivx.vixhealthsystem.model.facility;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
@Setter
@Getter
@Entity
@Table(name = "Rooms")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "type",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class Room {


    /**
     * -- SETTER --
     *  Sets the room identifier.
     *  <p>
     *  Normally managed automatically by JPA.
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * -- SETTER --
     *  Assigns the room to a medical facility.
     *
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private MedicalFacility medicalFacility;

    /**
     * Room number or room code.
     * Used to uniquely identify the room inside a facility.
     * -- GETTER --
     *  Returns the room number.
     * <p>
     *
     * -- SETTER --
     *  Sets the room number.
     *
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
     * @param number room number
     */
    public Room(String number) {
        this.number = number;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

}