package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.resource.Machinery;

import java.util.List;

/**
 * Responsible for managing and maintaining medical machinery.
 * Technicians can view all machines, identify faulty ones, and help employees with credentials.
 *
 * @see Employee
 * @see Machinery
 *
 */

public class Technician extends Employee {

    // ========== Machine Management Methods ==========

    /**
     * Returns the full list of all machines in the medical facility.
     * Used by technicians to get an overview of all equipment.
     *
     * @return an array of all Machine objects in the system
     */

    public List<Machinery> getMachineList() {
        // Will query the database for all Machine records
        // Returns empty array if no machines exist
        return null;
    }

    /**
     * Returns only the machines that are currently marked as faulty.
     * Used to prioritize repairs and maintenance.
     *
     * @return an array of Machine objects where status = false or "faulty"
     */

    public List<Machinery> getFaultyMachineList() {

        // Will query the database for Machine records where status = 'faulty'
        // Returns empty array if no faulty machines exist

        return null;
    }

    /**
     * Assists an employee with recovering their login credentials.
     * Same functionality as StaffManager.credentialsRecovery().
     *
     * @param e the employee who needs credential recovery
     */

    public void credentialsRecovery(Employee e) {

        // Will look up the employee's email from the database
        // Send a password reset link or temporary credentials

    }
}