package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.Role;
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

    @Override
    public Role getSystemRole() { return Role.ROLE_STAFF_MANAGER; }

    @Override
    public EmployeeType getEmployeeType() { return EmployeeType.STAFF_MANAGER; }

    // =====================================================
    // ACCOUNT MANAGEMENT METHODS
    // =====================================================

    /**
     * Validates and prepares a new employee for creation.
     * Ensures the employee has required fields and inherits the
     * staff manager's department when none is assigned.
     * Actual persistence is handled by the service layer.
     *
     * @param e the employee to prepare
     * @throws IllegalArgumentException if required fields are missing
     */
    public void createAccountForEmployee(Employee e) {
        if (e.getName() == null || e.getName().isBlank()) {
            throw new IllegalArgumentException("Employee name is required");
        }
        if (e.getEmail() == null || e.getEmail().isBlank()) {
            throw new IllegalArgumentException("Employee email is required");
        }
        if (e.getDepartment() == null && this.getDepartment() != null) {
            e.setDepartment(this.getDepartment());
        }
    }

    /**
     * Validates that an employee account can be deleted.
     * A staff manager cannot delete their own account.
     *
     * @param e the employee to delete
     * @throws IllegalArgumentException if the staff manager tries to delete their own account
     */
    public void deleteEmployeeAccount(Employee e) {
        if (this.getId() != null && this.getId().equals(e.getId())) {
            throw new IllegalArgumentException(
                    "Staff manager cannot delete their own account"
            );
        }
    }

    /**
     * Validates that credential recovery can be performed for an employee.
     * The employee must have an email address on record.
     *
     * @param e the employee requiring credential recovery
     * @throws IllegalArgumentException if the employee has no email address
     */
    public void credentialsRecovery(Employee e) {
        if (e.getEmail() == null || e.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Cannot recover credentials: employee has no email address on record"
            );
        }
    }
}