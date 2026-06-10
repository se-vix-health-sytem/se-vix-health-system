package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.resource.Machinery;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @Setter
    @Getter
    @Column(name = "specialization")
    private String specialization;

    /**
     * Machines installed inside the room.
     */
    @OneToMany(mappedBy = "specializedRoom")
    private List<Machinery> machineries = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public SpecializedRoom() {
    }

    public SpecializedRoom(
            String number,
            String specialization
    ) {
        super(number);
        this.specialization = specialization;
    }


    protected List<Machinery> getMachineries() {
        return machineries;
    }

    // =====================================================
    // MACHINE MANAGEMENT METHODS
    // =====================================================

    /**
     * Returns all machines currently marked as faulty.
     *
     * @return faulty machine list
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