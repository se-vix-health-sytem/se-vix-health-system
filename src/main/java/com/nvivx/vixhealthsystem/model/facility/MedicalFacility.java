package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.resource.Storage;

import java.util.ArrayList;
import java.util.Map;

public class MedicalFacility {
    private String name;
    private Location location;
    private String email;
    private String phoneNumber;
    private Storage storage;
    private ArrayList<Room> rooms;

    public MedicalFacility(String name, Location location, String email, String phoneNumber, Storage storage, ArrayList<Room> rooms) {
        this.name = name;
        this.location = location;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.storage = storage;
        this.rooms = rooms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<Resource, Integer> getResources() {
        return storage.getResources();
    }

    public void removeResources(Resource r, int q) throws Exception {
        storage.removeResource(r, q);
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
}
