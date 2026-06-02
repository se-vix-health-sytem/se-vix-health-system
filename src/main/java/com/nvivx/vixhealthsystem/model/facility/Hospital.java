package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.resource.Storage;

import java.util.ArrayList;

public class Hospital extends MedicalFacility{
    private ArrayList<InterationRoom> roomsForPatients;

    public Hospital(String name, Location location, String email, String phoneNumber, Storage storage, ArrayList<Room> rooms, ArrayList<InterationRoom> roomsForPatients) {
        super(name, location, email, phoneNumber, storage, rooms);
        this.roomsForPatients = roomsForPatients;
    }

    public ArrayList<InterationRoom> getRoomsForPatients() {
        return roomsForPatients;
    }

    public void setRoomsForPatients(ArrayList<InterationRoom> roomsForPatients) {
        this.roomsForPatients = roomsForPatients;
    }

    public ArrayList<InterationRoom> getFreeRoomsForPatients() {
        ArrayList<InterationRoom> out = new ArrayList<>();
        for (int i = 0; i < roomsForPatients.size(); i++) {
            InterationRoom room = roomsForPatients.get(i);
            if (room.getNFreeBeds() > 0) {
                out.add(room);
            }
        }
        return out;
    }

    public InterationRoom findPatientInRoom(Patient p) throws Exception {
        for (int i = 0; i < roomsForPatients.size(); i++) {
            InterationRoom room = roomsForPatients.get(i);
            if (room.hasPatient(p)) {
                return room;
            }
        }
        throw new Exception("No patient " + p + " in the hospital");
    }

    public void internPatient(Patient p, InterationRoom r) throws Exception {
        r.addPatient(p);
    }

    public void dismissPatient(Patient p) throws Exception {
        findPatientInRoom(p).removePatient(p);
    }
}
