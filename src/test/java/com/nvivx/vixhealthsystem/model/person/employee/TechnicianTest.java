package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.resource.Machinery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Technician.
 *
 * Verifies system role, employee type, and the getFaultyMachineList domain
 * method that filters machinery by FAULTY status without mutating the source
 * list. Plain JUnit — no Spring context loaded.
 *
 * @see Technician
 */
class TechnicianTest {
    private Technician technician;
    private List<Machinery> machines;

    @BeforeEach
    void setUp() {
        technician = new Technician();
        technician.setId(1L);
        technician.setName("Giulia");
        technician.setSurname("Romano");

        machines = new ArrayList<>();

        Machinery m1 = new Machinery("X-Ray Machine");
        m1.setId(100L);
        m1.setStatus(MachineStatus.WORKING);

        Machinery m2 = new Machinery("MRI Scanner");
        m2.setId(101L);
        m2.setStatus(MachineStatus.FAULTY);

        Machinery m3 = new Machinery("CT Scanner");
        m3.setId(102L);
        m3.setStatus(MachineStatus.UNDER_MAINTENANCE);

        Machinery m4 = new Machinery("Ultrasound");
        m4.setId(103L);
        m4.setStatus(MachineStatus.FAULTY);

        machines.add(m1);
        machines.add(m2);
        machines.add(m3);
        machines.add(m4);
    }

    @Test
    void getSystemRole_ShouldReturnTechnicianRole() {
        assertEquals(Role.ROLE_TECHNICIAN, technician.getSystemRole());
    }

    @Test
    void getEmployeeType_ShouldReturnTechnicianType() {
        assertEquals(EmployeeType.TECHNICIAN, technician.getEmployeeType());
    }

    @Test
    void getFaultyMachineList_ShouldReturnOnlyFaultyMachines() {
        List<Machinery> faulty = technician.getFaultyMachineList(machines);

        assertEquals(2, faulty.size());
        assertTrue(faulty.stream().allMatch(Machinery::isFaulty));
        assertFalse(faulty.stream().anyMatch(m -> m.getStatus() == MachineStatus.WORKING));
        assertFalse(faulty.stream().anyMatch(m -> m.getStatus() == MachineStatus.UNDER_MAINTENANCE));
    }

    @Test
    void getFaultyMachineList_ShouldReturnEmptyListWhenNoFaultyMachines() {
        for (Machinery m : machines) {
            m.setStatus(MachineStatus.WORKING);
        }

        List<Machinery> faulty = technician.getFaultyMachineList(machines);

        assertTrue(faulty.isEmpty());
    }

    @Test
    void getFaultyMachineList_ShouldNotModifyOriginalList() {
        int originalSize = machines.size();
        List<Machinery> faulty = technician.getFaultyMachineList(machines);

        assertEquals(originalSize, machines.size());
        assertNotSame(machines, faulty);
    }
}