package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.resource.Resource;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Part of the purchasing department.
 * Responsible for procuring and adding new resources to the hospital inventory.
 *
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
     * Adds a new resource to the system.
     * If the resource already exists, this may increase its quantity.
     *
     * Actual persistence is handled by the service/repository layer.
     *
     * @param r resource to add
     */
    public void addResource(Resource r) {

    }
}