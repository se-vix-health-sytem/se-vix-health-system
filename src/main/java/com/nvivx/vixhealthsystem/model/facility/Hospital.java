package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.Patient;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hospital.
 * <p>
 * A hospital is a medical facility capable of admitting patients
 * for short-term or long-term stays.
 * <p>
 * Hospitals contain inpatient rooms and provide admission and
 * discharge services.
 *
 * @see MedicalFacility
 * @see InternationRoom
 * @see Patient
 */
@Entity
@DiscriminatorValue("HOSPITAL")
public class Hospital extends MedicalFacility {
    /**
     * Returns all inpatient rooms available in the hospital.
     * <p>
     * An inpatient room is a room designed to accommodate
     * admitted patients for short-term or long-term stays.
     * <p>
     * The method filters the facility room list and returns
     * only rooms of type InternationRoom.
     *
     * @return list of all inpatient rooms
     */
    public List<InternationRoom> getRoomsForPatients() {

        List<InternationRoom> out = new ArrayList<>();

        for (Room room : getRooms()) {

            if (room instanceof InternationRoom internRoom) {
                out.add(internRoom);
            }
        }

        return out;
    }

    /**
     * Returns all inpatient rooms that still have at least one
     * free bed available.
     *
     * @return list of available inpatient rooms
     */
    public List<InternationRoom> getFreeRoomsForPatients() {

        List<InternationRoom> out = new ArrayList<>();

        for (Room room : getRooms()) {

            if (room instanceof InternationRoom internRoom &&
                    internRoom.getNFreeBeds() > 0) {

                out.add(internRoom);
            }
        }

        return out;
    }

    /**
     * Finds the room currently hosting a patient.
     *
     * @param p patient to search
     * @return patient's room
     * @throws Exception if the patient is not admitted
     */
    public InternationRoom findPatientInRoom(Patient p)
            throws Exception {

        for (Room room : getRooms()) {

            if (room instanceof InternationRoom internRoom &&
                    internRoom.hasPatient(p)) {

                return internRoom;
            }
        }

        throw new Exception(
                "No patient " + p + " is currently admitted."
        );
    }

    /**
     * Admits a patient to an inpatient room.
     *
     * @param p patient
     * @param r destination room
     * @throws Exception if admission fails
     */
    public void internPatient(
            Patient p,
            InternationRoom r
    ) throws Exception {

        r.addPatient(p);
    }

    /**
     * Discharges a patient from the hospital.
     *
     * @param p patient to discharge
     * @throws Exception if the patient is not admitted
     */
    public void dismissPatient(
            Patient p
    ) throws Exception {

        findPatientInRoom(p).removePatient(p);
    }
}