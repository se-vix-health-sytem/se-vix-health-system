package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.Patient;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InterationRoom extends Room{
    private int nBeds;
    private ArrayList<Patient> patients;

    public InterationRoom(String number, int nBeds) {
        super(number);
        this.nBeds = nBeds;
        patients = new ArrayList<>();
    }

    public int getTotalNBeds() {
        return nBeds;
    }

    public int getNFreeBeds() {
        return patients.size();
    }

    public void addPatient(Patient p) throws Exception {
        if (patients.size() >= nBeds) {
            throw new Exception("Patient limit reached for this room");
        } else {
            patients.add(p);
        }
    }

    public boolean hasPatient(Patient p) {
       return  patients.contains(p);
    }

    public void removePatient(Patient p) throws Exception {
        if (hasPatient(p)) {
            patients.remove(p);
        } else {
            throw new Exception("No patient " + p + " in this room");
        }
    }

}
