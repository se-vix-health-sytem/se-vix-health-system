package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.resource.Machinery;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specialized room containing medical equipment.
 * <p>
 * Specialized rooms are dedicated to specific medical activities,
 * such as radiology, MRI, CT scanning or surgery.
 * <p>
 * Each specialized room may contain multiple machines.
 *
 * @see Room
 * @see Machinery
 */
@Entity
@DiscriminatorValue("SPECIALIZED_ROOM")
public class SpecializedRoom extends Room {

    /**
     * Specialization of the room.
     * Examples: Radiology, MRI, CT Scan, Surgery.
     */
    @Column(name = "specialization")
    private String specialization;

    /** Machines installed inside the room. */
    @OneToMany(mappedBy = "specializedRoom")
    private List<Machinery> machineries = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public SpecializedRoom() {
    }

    /**
     * Creates a specialized room with the specified number and specialization.
     *
     * @param number the room number
     * @param specialization the room specialization
     */
    public SpecializedRoom(
            String number,
            String specialization
    ) {
        super(number);
        this.specialization = specialization;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the room specialization.
     *
     * @return the specialization
     */
    public String getSpecialization() {
        return specialization;
    }

    /**
     * Sets the room specialization.
     *
     * @param specialization the specialization to set
     */
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    /**
     * Returns the list of machines in this room.
     *
     * @return the list of machineries
     */
    protected List<Machinery> getMachineries() {
        return machineries;
    }

    /**
     * Sets the list of machines in this room.
     *
     * @param machineries the list of machineries to set
     */
    protected void setMachineries(List<Machinery> machineries) {
        this.machineries = machineries;
    }

    // =====================================================
    // MACHINE MANAGEMENT METHODS
    // =====================================================

    /**
     * Returns all machines currently marked as faulty.
     *
     * @return list of faulty machines
     */
    protected List<Machinery> getFaultyMachines() {
        List<Machinery> out = new ArrayList<>();
        for (Machinery machinery : machineries) {
            if (machinery.isFaulty()) {
                out.add(machinery);
            }
        }
        return out;
    }
}