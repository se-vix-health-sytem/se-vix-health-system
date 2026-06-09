package com.nvivx.vixhealthsystem.model.person.employee;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Manages employee accounts in the system.
 * Responsible for creating, deleting and recovering employee accounts.
 *
 * This role is typically assigned to human resources
 * or administrative personnel with account management privileges.
 *
 * @see Employee
 */
@Entity
@DiscriminatorValue("STAFF_MANAGER")
public class StaffManager extends Employee {

    // =====================================================
    // ACCOUNT MANAGEMENT METHODS
    // =====================================================

    /**
     * Creates a new employee account.
     *
     * The actual persistence is handled by the service layer.
     *
     * @return newly created employee
     */
    public Employee createAccountForEmployee() {
        return null;
    }

    /**
     * Removes an employee account from the system.
     *
     * @param e employee to remove
     */
    public void deleteEmployeeAccount(Employee e) {

    }

    /**
     * Starts the credential recovery process for an employee.
     *
     * @param e employee requiring credential recovery
     */
    public void credentialsRecovery(Employee e) {

    }
}