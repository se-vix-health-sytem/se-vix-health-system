package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.Role;
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

    @Override
    public Role getSystemRole() { return Role.ROLE_BUYER; }

    @Override
    public EmployeeType getEmployeeType() { return EmployeeType.BUYER; }

    // =====================================================
    // RESOURCE MANAGEMENT METHODS
    // =====================================================

    /**
     * Adds a quantity of a resource to the facility storage,
     * navigating through the buyer's own department → facility → storage chain.
     * Actual persistence is handled by the service layer.
     *
     * @param resource the resource to add
     * @param quantity the quantity to add
     * @throws IllegalStateException if the department, facility or storage is not set
     */
    public void addResource(Resource resource, int quantity) {
        if (getDepartment() == null
                || getDepartment().getMedicalFacility() == null
                || getDepartment().getMedicalFacility().getStorage() == null) {
            throw new IllegalStateException(
                    "Buyer has no department/facility/storage assigned"
            );
        }
        getDepartment().getMedicalFacility().getStorage().addResource(resource, quantity);
    }
}