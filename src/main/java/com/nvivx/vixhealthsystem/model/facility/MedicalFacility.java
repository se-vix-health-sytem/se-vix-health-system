package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.resource.Storage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
@Entity
@Table(name = "MedicalFacilities")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "type",
        discriminatorType = DiscriminatorType.STRING
)
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