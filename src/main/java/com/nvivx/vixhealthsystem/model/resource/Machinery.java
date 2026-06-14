package com.nvivx.vixhealthsystem.model.resource;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import jakarta.persistence.*;

/**
 * Represents a medical machine installed inside a specialized room.
 * <p>
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
     * Current operational status of the machine.
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
     * Creates a machine with the specified name and initializes its status.
     *
     * @param name the machine name
     */
    public Machinery(String name) {
        this.name = name;
        updateStatus();
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the unique machine identifier.
     *
     * @return the machine ID
     */
    public Long getId() {
        return id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the unique machine identifier.
     *
     * @param id the machine ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the machine name.
     *
     * @return the machine name
     */
    public String getName() {
        return name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the machine name.
     *
     * @param name the machine name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the room where the machine is installed.
     *
     * @return the specialized room
     */
    public SpecializedRoom getSpecializedRoom() {
        return specializedRoom;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the room where the machine is installed.
     *
     * @param specializedRoom the specialized room to set
     */
    public void setSpecializedRoom(SpecializedRoom specializedRoom) {
        this.specializedRoom = specializedRoom;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the current operational status of the machine.
     *
     * @return the machine status
     */
    public MachineStatus getStatus() {
        return status;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the current operational status of the machine.
     *
     * @param status the machine status to set
     */
    public void setStatus(MachineStatus status) {
        this.status = status;
    }
    /// @endcond

    // =====================================================
    // STATUS METHODS
    // =====================================================

    /**
     * Updates the machine status.
     * <p>
     * Business logic to determine the current status
     * should be implemented here.
     */
    public void updateStatus() {
        double r = Math.random();
        if (r < 0.50) {
            this.status = MachineStatus.WORKING;
        } else if (r < 0.80) {
            this.status = MachineStatus.FAULTY;
        } else {
            this.status = MachineStatus.UNDER_MAINTENANCE;
        }
    }

    /**
     * Checks whether the machine is faulty.
     *
     * @return true if the machine status is FAULTY, false otherwise
     */
    public boolean isFaulty() {
        return status == MachineStatus.FAULTY;
    }
}
