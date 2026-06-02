package com.nvivx.vixhealthsystem.model.person.employee;

/**
 * Manages employee accounts in the system.
 * This includes creating new accounts, deleting old ones, and helping with lost credentials.
 *
 * @see Employee
 */

public class StaffManager extends Employee {

    // ========== Account Management Methods ==========

    /**
     * Creates an employee account for any type of employee.
     *
    */

    public Employee createAccountForEmployee() {
        // TODO: Replace with methods for each employee type?
        return null;
    }

    /**
     * Permanently removes an employee's account from the system.
     * This action cannot be undone (or can be soft-deleted with an 'active' flag).
     *
     * @param e the employee whose account should be deleted
     */

    public void deleteEmployeeAccount(Employee e) {

        // Will delete the employee record from the database
        // Or set an 'active = false' flag for soft deletion

    }

    /**
     * Helps an employee regain access to their account.
     * Typically, sends a password reset email or generates a temporary password.
     *
     * @param e the employee who needs credential recovery
     */

    public void credentialsRecovery(Employee e) {

        // Will look up the employee's email from the database
        // Send a password reset link or temporary credentials

    }
}