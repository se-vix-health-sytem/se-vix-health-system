package com.nvivx.vixhealthsystem.model.resource;

import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the storage of a medical facility.
 * <p>
 * A storage contains resources and tracks the quantity
 * available for each resource.
 * <p>
 * Quantities are stored in the StorageResources join table.
 *
 * @see MedicalFacility
 * @see Resource
 */
@Entity
@Table(name = "Storages")
public class Storage {

    /**
     * Unique storage identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Medical facility owning this storage.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false, unique = true)
    private MedicalFacility medicalFacility;

    /**
     * Resources stored with their respective quantities.
     */
    @ElementCollection
    @CollectionTable(
            name = "StorageResources",
            joinColumns = @JoinColumn(name = "storage_id")
    )
    @MapKeyJoinColumn(name = "resource_id")
    @Column(name = "quantity", nullable = false)
    private Map<Resource, Integer> resources = new HashMap<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Storage() {
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the unique storage identifier.
     *
     * @return the storage ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique storage identifier.
     *
     * @param id the storage ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the medical facility owning this storage.
     *
     * @return the medical facility
     */
    public MedicalFacility getMedicalFacility() {
        return medicalFacility;
    }

    /**
     * Sets the medical facility owning this storage.
     *
     * @param medicalFacility the medical facility to set
     */
    public void setMedicalFacility(MedicalFacility medicalFacility) {
        this.medicalFacility = medicalFacility;
    }

    /**
     * Returns the map of resources and their quantities.
     *
     * @return the resource inventory map
     */
    public Map<Resource, Integer> getResources() {
        return resources;
    }

    /**
     * Sets the map of resources and their quantities.
     *
     * @param resources the resource inventory map to set
     */
    public void setResources(Map<Resource, Integer> resources) {
        this.resources = resources;
    }

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Returns the total quantity of all resources in storage.
     *
     * @return the sum of all resource quantities
     */
    public int getTotalQuantity() {
        return resources.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Adds a quantity of a resource to storage.
     * If the resource already exists, its quantity is increased.
     *
     * @param r the resource to add
     * @param q the quantity to add
     */
    public void addResource(Resource r, int q) {
        resources.put(r, resources.getOrDefault(r, 0) + q);
    }

    /**
     * Removes a quantity of a resource from storage.
     *
     * @param r the resource to remove
     * @param q the quantity to remove
     * @throws Exception if the resource is not present or the quantity is insufficient
     */
    public void removeResource(Resource r, int q) throws Exception {
        if (!resources.containsKey(r)) {
            throw new Exception("Resource not present");
        }

        int current = resources.get(r);

        if (q > current) {
            throw new Exception("Not enough resource quantity");
        }

        int updated = current - q;

        if (updated == 0) {
            resources.remove(r);
        } else {
            resources.put(r, updated);
        }
    }
}
