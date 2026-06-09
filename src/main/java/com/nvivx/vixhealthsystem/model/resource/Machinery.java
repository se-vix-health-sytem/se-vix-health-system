package com.nvivx.vixhealthsystem.model.resource;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import jakarta.persistence.*;

/**
 * Represents a medical machine installed inside a specialized room.
 *
 * Machines can be operational, under maintenance or faulty.
 * Technicians use the machine status to identify equipment
 * requiring intervention.
 *
 * @see SpecializedRoom
 * @see MachineStatus
 */
@Entity
@Table(name = "Machines")
public class Machinery {

    /**
     * Unique machine identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Room where the machine is installed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialized_room_id", nullable = false)
    private SpecializedRoom specializedRoom;

    /**
     * Machine name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Current machine status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MachineStatus status;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Machinery() {
    }

    /**
     * Creates a machine with the specified name.
     *
     * @param name machine name
     */
    public Machinery(String name) {
        this.name = name;
        updateStatus();
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpecializedRoom getSpecializedRoom() {
        return specializedRoom;
    }

    public void setSpecializedRoom(SpecializedRoom specializedRoom) {
        this.specializedRoom = specializedRoom;
    }

    public MachineStatus getStatus() {
        return status;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    // =====================================================
    // STATUS METHODS
    // =====================================================

    /**
     * Updates the machine status.
     *
     * Business logic to determine the current status
     * should be implemented here.
     */
    public void updateStatus() {
        // TODO
    }

    /**
     * Checks whether the machine is faulty.
     *
     * @return true if the machine is faulty
     */
    public boolean isFaulty() {
        return status == MachineStatus.FAULTY;
    }
}