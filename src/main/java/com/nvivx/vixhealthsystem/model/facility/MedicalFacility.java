package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.resource.Storage;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a healthcare facility such as a hospital,
 * clinic or specialized medical center.
 *
 * A medical facility contains departments, rooms and a storage
 * area used to manage resources and equipment.
 *
 * @see Department
 * @see Room
 * @see Storage
 */
@Entity
@Table(name = "MedicalFacilities")
public class MedicalFacility {

    /**
     * Unique facility identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Facility name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Facility type.
     * Examples: Hospital, Clinic, Emergency Center.
     */
    @Column(name = "type", nullable = false)
    private String type;

    /**
     * Geographic location of the facility.
     */
    @Embedded
    private Location location;

    /**
     * Contact email.
     */
    @Column(name = "email")
    private String email;

    /**
     * Contact phone number.
     */
    @Column(name = "phone")
    private String phoneNumber;

    /**
     * Facility storage.
     */
    @OneToOne(mappedBy = "medicalFacility")
    private Storage storage;

    /**
     * Rooms belonging to this facility.
     */
    @OneToMany(mappedBy = "medicalFacility")
    private List<Room> rooms = new ArrayList<>();

    /**
     * Departments belonging to this facility.
     */
    @OneToMany(mappedBy = "medicalFacility")
    private List<Department> departments = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public MedicalFacility() {
    }

    public MedicalFacility(
            String name,
            String type,
            Location location,
            String email,
            String phoneNumber
    ) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Returns all resources available in the facility storage.
     *
     * @return resource inventory
     */
    public Map<Resource, Integer> getResources() {
        return storage != null
                ? storage.getResources()
                : null;
    }
}