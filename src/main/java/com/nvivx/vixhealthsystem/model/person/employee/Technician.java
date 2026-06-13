package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.resource.Machinery;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for managing and maintaining medical machinery.
 * Technicians can monitor equipment and identify faulty machines.
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
     * Filters a list of machines and returns only those currently marked as faulty.
     * Uses each machine's own {@link Machinery#isFaulty()} domain method.
     *
     * @param allMachines the full list of machines to inspect
     * @return list containing only faulty machines
     */
    public List<Machinery> getFaultyMachineList(List<Machinery> allMachines) {
        return allMachines.stream()
                .filter(Machinery::isFaulty)
                .collect(Collectors.toList());
    }
}