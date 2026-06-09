package com.nvivx.vixhealthsystem.model.person.employee;
import com.nvivx.vixhealthsystem.model.resource.Resource;

/**
 * Part of the purchasing department.
 * Responsible for procuring and adding new resources to the hospital inventory.
 *
 *
 * @see Employee
 * @see Resource
 */

public class Buyer extends Employee {

    // ========== Resource Management Methods ==========

    /**
     * Adds a new resource to the system.
     * If the resource already exists, this may increase its quantity instead.
     *
     * @param r the resource to add (contains name, description, quantity, price)
     */

    public void addResource(Resource r) {

    }
}