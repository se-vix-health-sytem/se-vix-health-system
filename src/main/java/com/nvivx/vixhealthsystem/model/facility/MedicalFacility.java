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
 * <p>
 * A medical facility contains departments, rooms and a storage
 * area used to manage resources and equipment.
 *
 * @see Department
 * @see Room
 * @see Storage
 */
@Entity
@Table(name = "MedicalFacilities")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "type",
        discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue("MEDICAL_FACILITY")
public class MedicalFacility {

    /** Unique facility identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Facility name. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Geographic location of the facility. */
    @Embedded
    private Location location;

    /** Contact email. */
    @Column(name = "email")
    private String email;

    /** Contact phone number. */
    @Column(name = "phone")
    private String phoneNumber;

    /** Facility storage. */
    @OneToOne(mappedBy = "medicalFacility")
    private Storage storage;

    /** Rooms belonging to this facility. */
    @OneToMany(mappedBy = "medicalFacility")
    private List<Room> rooms = new ArrayList<>();

    /** Departments belonging to this facility. */
    @OneToMany(mappedBy = "medicalFacility")
    private List<Department> departments = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public MedicalFacility() {
    }

    /**
     * Creates a MedicalFacility with the specified details.
     *
     * @param name the facility name
     * @param location the geographic location
     * @param email the contact email
     * @param phoneNumber the contact phone number
     */
    public MedicalFacility(
            String name,
            Location location,
            String email,
            String phoneNumber
    ) {
        this.name = name;
        this.location = location;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the unique facility identifier.
     *
     * @return the facility ID
     */
    public Long getId() {
        return id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the unique facility identifier.
     *
     * @param id the facility ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the facility name.
     *
     * @return the facility name
     */
    public String getName() {
        return name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the facility name.
     *
     * @param name the facility name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the geographic location of the facility.
     *
     * @return the location object
     */
    public Location getLocation() {
        return location;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the geographic location of the facility.
     *
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the contact email.
     *
     * @return the contact email
     */
    public String getEmail() {
        return email;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the contact email.
     *
     * @param email the contact email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the contact phone number.
     *
     * @return the contact phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the contact phone number.
     *
     * @param phoneNumber the contact phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the facility storage.
     *
     * @return the storage object
     */
    public Storage getStorage() {
        return storage;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the facility storage.
     *
     * @param storage the storage to set
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the list of rooms belonging to this facility.
     *
     * @return the list of rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the list of rooms belonging to this facility.
     *
     * @param rooms the list of rooms to set
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the list of departments belonging to this facility.
     *
     * @return the list of departments
     */
    public List<Department> getDepartments() {
        return departments;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the list of departments belonging to this facility.
     *
     * @param departments the list of departments to set
     */
    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
    /// @endcond
}