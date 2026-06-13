package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.resource.Machinery;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

/**
 * Responsible for managing and maintaining medical machinery.
 * Technicians can monitor equipment, identify faulty machines
 * and assist employees with credential recovery.
 *
 * @see Employee
 * @see Machinery
 */
@Entity
@DiscriminatorValue("TECHNICIAN")
public class Technician extends Employee {

    // =====================================================
    // MACHINE MANAGEMENT METHODS
    // =====================================================

    /**
     * Returns all machines available in the facility.
     *
     * @return list of machines
     */
    public List<Machinery> getMachineList() {

        return null;
    }

    /**
     * Returns only machines currently marked as faulty.
     *
     * @return list of faulty machines
     */
    public List<Machinery> getFaultyMachineList() {

        return null;
    }
}