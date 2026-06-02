package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;

import java.util.ArrayList;

/**
 * Represents a medical department in the facility (e.g. Cardiology, Psychiatry).
 *
 * A department groups medical specialists from the same medical field.
 * It is composed inside a MedicalFacility (0..* departments per facility).
 *
 * @see MedicalFacility
 * @see MedicalSpecialist
 */

public class Department {

    private String name;
    private String description;
    private String email;
    private String phoneNumber;
    private ArrayList<MedicalSpecialist> specialists;

    //----------------- Constructors -----------------------------

    public Department() {
        this.specialists = new ArrayList<>();
    }

    public Department(String name, String description, String email, String phoneNumber) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.specialists = new ArrayList<>();
    }

    //----------------- Getters and Setters -----------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ArrayList<MedicalSpecialist> getSpecialists() {
        return specialists;
    }

    public void setSpecialists(ArrayList<MedicalSpecialist> specialists) {
        this.specialists = specialists;
    }


    //----------------- Methods -----------------------------
    /**
     * Adds a medical specialist to this department.
     *
     * @param specialist the MedicalSpecialist to add
     */
    public void addSpecialist(MedicalSpecialist specialist) {
        specialists.add(specialist);
    }

    /**
     * Removes a medical specialist from this department.
     *
     * @param specialist the MedicalSpecialist to remove
     */
    public void removeSpecialist(MedicalSpecialist specialist) {
        specialists.remove(specialist);
    }

    /**
     * Returns the number of specialists currently in this department.
     *
     * @return the specialist count as an int
     */
    public int getSpecialistCount() {
        return specialists.size();
    }

}
