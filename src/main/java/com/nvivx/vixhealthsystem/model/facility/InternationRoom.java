package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an inpatient room used for patient hospitalization.
 * <p>
 * An inpatient room contains a fixed number of beds and can host
 * multiple patients simultaneously.
 * <p>
 * Patients are linked through the RoomPatients table.
 *
 * @see Room
 * @see Patient
 */

@Getter
@Setter
@Entity
@DiscriminatorValue("INTERNATION_ROOM")
public class InternationRoom extends Room {

    /**
     * Total number of beds available in the room.
     */
    @Column(name = "beds_count")
    private Integer nBeds;


    @ManyToMany
    @JoinTable(
            name = "RoomPatients",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    private List<Patient> patients = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public InternationRoom() {
    }

    /**
     * Creates an inpatient room with a specified number of beds.
     *
     * @param number room number
     * @param nBeds total number of beds
     */
    public InternationRoom(String number, int nBeds) {
        super(number);
        this.nBeds = nBeds;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the total number of beds in the room.
     *
     * @return total bed count
     */
    public int getTotalNBeds() {
        return nBeds;
    }

    /**
     * Sets the total number of beds.
     *
     * @param nBeds bed count
     */
    public void setNBeds(int nBeds) {
        this.nBeds = nBeds;
    }

    /**
     * Returns the number of currently available beds.
     *
     * @return free bed count
     */
    public int getNFreeBeds() {
        return nBeds - patients.size();
    }

    // =====================================================
    // PATIENT MANAGEMENT METHODS
    // =====================================================

    /**
     * Admits a patient to the room.
     *
     * @param p patient to admit
     * @throws Exception if the room is already full
     */
    public void addPatient(Patient p) throws Exception {

        if (patients.size() >= nBeds) {
            throw new Exception(
                    "Patient limit reached for this room"
            );
        }

        patients.add(p);
    }

    /**
     * Checks whether a patient is currently admitted.
     *
     * @param p patient to search
     * @return true if the patient is present
     */
    public boolean hasPatient(Patient p) {
        return patients.contains(p);
    }

    /**
     * Removes a patient from the room.
     *
     * @param p patient to remove
     * @throws Exception if the patient is not present
     */
    public void removePatient(Patient p) throws Exception {

        if (!hasPatient(p)) {
            throw new Exception(
                    "No patient " + p + " in this room"
            );
        }

        patients.remove(p);
    }
}