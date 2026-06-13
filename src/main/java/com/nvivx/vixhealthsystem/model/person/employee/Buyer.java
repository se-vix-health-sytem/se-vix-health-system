package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.resource.Storage;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Part of the purchasing department.
 * Responsible for procuring and adding new resources to the hospital inventory.
 * <p>
 * Buyers are responsible for monitoring stock levels and requesting
 * new resources when needed.
 *
 * @see Employee
 * @see Resource
 */
@Entity
@DiscriminatorValue("BUYER")
public class Buyer extends Employee {

    // =====================================================
    // RESOURCE MANAGEMENT METHODS
    // =====================================================

    /**
     * Adds a quantity of a resource to a storage unit.
     * Delegates to the storage's own addition logic, which
     * increases the existing quantity or creates a new entry.
     * Actual persistence is handled by the service layer.
     *
     * @param storage  the storage unit to add the resource to
     * @param resource the resource to add
     * @param quantity the quantity to add
     */
    public void addResource(Storage storage, Resource resource, int quantity) {
        storage.addResource(resource, quantity);
    }
}