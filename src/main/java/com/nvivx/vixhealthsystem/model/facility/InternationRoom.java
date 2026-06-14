package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.enums.BedStatus;
import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.*;
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
@Entity
@DiscriminatorValue("INTERNATION_ROOM")
public class InternationRoom extends Room {

    /** Total number of beds available in the room. */
    @Column(name = "beds_count")
    private Integer nBeds;

    /** Patients currently occupying beds in this room. */
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
     * @param number the room number
     * @param nBeds the total number of beds
     */
    public InternationRoom(String number, int nBeds) {
        super(number);
        this.nBeds = nBeds;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the total number of beds in the room.
     *
     * @return total bed count
     */
    public int getTotalNBeds() {
        return nBeds != null ? nBeds : 0;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the total number of beds.
     *
     * @param nBeds the bed count to set
     */
    public void setNBeds(int nBeds) {
        this.nBeds = nBeds;
    }
    /// @endcond

    /**
     * Returns the number of currently available beds.
     *
     * @return free bed count
     */
    public int getNFreeBeds() {
        return (nBeds != null ? nBeds : 0) - patients.size();
    }

    /// @cond INTERNAL
    /**
     * Returns the list of patients in this room.
     *
     * @return the list of patients
     */
    public List<Patient> getPatients() {
        return patients;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the list of patients in this room.
     *
     * @param patients the list of patients to set
     */
    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }
    /// @endcond

    // =====================================================
    // BED STATUS (derived from occupancy)
    // =====================================================

    /**
     * Returns the current bed availability status derived from occupancy.
     * FREE if at least one bed is available; OCCUPIED if the room is full.
     */
    @Transient
    public BedStatus getBedStatus() {
        return getNFreeBeds() > 0 ? BedStatus.FREE : BedStatus.OCCUPIED;
    }

    // =====================================================
    // PATIENT MANAGEMENT METHODS
    // =====================================================

    /**
     * Admits a patient to the room.
     *
     * @param p the patient to admit
     * @throws Exception if the room is already full
     */
    public void addPatient(Patient p) throws Exception {
        if (patients.size() >= (nBeds != null ? nBeds : 0)) {
            throw new Exception(
                    "Patient limit reached for this room"
            );
        }
        patients.add(p);
    }

    /**
     * Checks whether a patient is currently admitted.
     *
     * @param p the patient to search for
     * @return true if the patient is present, false otherwise
     */
    public boolean hasPatient(Patient p) {
        return patients.contains(p);
    }

    /**
     * Removes a patient from the room.
     *
     * @param p the patient to remove
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