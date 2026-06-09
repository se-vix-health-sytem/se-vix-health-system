package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.resource.Storage;

import java.util.ArrayList;

public class Hospital extends MedicalFacility{
    private ArrayList<InternationRoom> roomsForPatients;

    public Hospital(String name, Location location, String email, String phoneNumber, Storage storage, ArrayList<Room> rooms, ArrayList<InternationRoom> roomsForPatients) {
        super(name, location, email, phoneNumber, storage, rooms);
        this.roomsForPatients = roomsForPatients;
    }

    public ArrayList<InternationRoom> getRoomsForPatients() {
        return roomsForPatients;
    }

    public void setRoomsForPatients(ArrayList<InternationRoom> roomsForPatients) {
        this.roomsForPatients = roomsForPatients;
    }

    public ArrayList<InternationRoom> getFreeRoomsForPatients() {
        ArrayList<InternationRoom> out = new ArrayList<>();
        for (int i = 0; i < roomsForPatients.size(); i++) {
            InternationRoom room = roomsForPatients.get(i);
            if (room.getNFreeBeds() > 0) {
                out.add(room);
            }
        }
        return out;
    }

    public InternationRoom findPatientInRoom(Patient p) throws Exception {
        for (int i = 0; i < roomsForPatients.size(); i++) {
            InternationRoom room = roomsForPatients.get(i);
            if (room.hasPatient(p)) {
                return room;
            }
        }
        throw new Exception("No patient " + p + " in the hospital");
    }

    public void internPatient(Patient p, InternationRoom r) throws Exception {
        r.addPatient(p);
    }

    public void dismissPatient(Patient p) throws Exception {
        findPatientInRoom(p).removePatient(p);
    }
}
