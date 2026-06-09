package com.nvivx.vixhealthsystem.model.resource;

import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the storage of a medical facility.
 *
 * A storage contains resources and tracks the quantity
 * available for each resource.
 *
 * Quantities are stored in the StorageResources join table.
 *
 * @see MedicalFacility
 * @see Resource
 */
@Entity
@Table(name = "Storages")
public class Storage {

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
     * Resources stored with their quantities.
     */
    @ElementCollection
    @CollectionTable(
            name = "StorageResources",
            joinColumns = @JoinColumn(name = "storage_id")
    )
    @MapKeyJoinColumn(name = "resource_id")
    @Column(name = "quantity", nullable = false)
    private Map<Resource, Integer> resources = new HashMap<>();

    public Storage() {
    }

    public Long getId() {
        return id;
    }

    public MedicalFacility getMedicalFacility() {
        return medicalFacility;
    }

    public void setMedicalFacility(MedicalFacility medicalFacility) {
        this.medicalFacility = medicalFacility;
    }

    public int getTotalQuantity() {
        return resources.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public void addResource(Resource r, int q) {
        resources.put(r, resources.getOrDefault(r, 0) + q);
    }

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

    public Map<Resource, Integer> getResources() {
        return resources;
    }

    public void setResources(Map<Resource, Integer> resources) {
        this.resources = resources;
    }
}