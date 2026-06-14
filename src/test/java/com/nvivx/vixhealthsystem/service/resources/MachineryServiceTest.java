package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import com.nvivx.vixhealthsystem.model.person.employee.Technician;
import com.nvivx.vixhealthsystem.model.resource.Machinery;
import com.nvivx.vixhealthsystem.repository.MachineryRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @brief Unit tests for MachineryService using Mockito mocks for MachineryRepository and AuditService.
 * Covers machine retrieval by id/status/room, faulty-machine filtering, status updates,
 * fault reporting, repair, maintenance scheduling, count helpers, and alert generation.
 */
@ExtendWith(MockitoExtension.class)
class MachineryServiceTest {

    @Mock
    private MachineryRepository machineryRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MachineryService service;

    /**
     * Tests that getAllMachines() returns all machines
     * provided by the mocked repository.
     */
    @Test
    void shouldReturnAllMachines() {
        Machinery m1 = createMachine(1L, "MRI Machine", MachineStatus.WORKING);
        Machinery m2 = createMachine(2L, "X-Ray Machine", MachineStatus.FAULTY);

        when(machineryRepository.findAll()).thenReturn(List.of(m1, m2));

        List<Machinery> result = service.getAllMachines();

        assertEquals(2, result.size());
        assertEquals("MRI Machine", result.get(0).getName());
        assertEquals("X-Ray Machine", result.get(1).getName());

        verify(machineryRepository).findAll();
    }

    /**
     * Tests that getMachineById() returns the correct machine
     * when the machine exists.
     */
    @Test
    void shouldFindMachineById() {
        Machinery machine = createMachine(1L, "MRI Machine", MachineStatus.WORKING);

        when(machineryRepository.findById(1L)).thenReturn(Optional.of(machine));

        Machinery result = service.getMachineById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("MRI Machine", result.getName());

        verify(machineryRepository).findById(1L);
    }

    /**
     * Tests that getMachineById() throws an exception
     * when the machine does not exist.
     */
    @Test
    void shouldThrowWhenMachineDoesNotExist() {
        when(machineryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.getMachineById(99L)
        );

        assertEquals("Machine not found with id: 99", exception.getMessage());

        verify(machineryRepository).findById(99L);
    }

    /**
     * Tests that getMachinesByStatus() returns machines
     * with the requested status.
     */
    @Test
    void shouldReturnMachinesByStatus() {
        Machinery machine = createMachine(1L, "MRI Machine", MachineStatus.FAULTY);

        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(machine));

        List<Machinery> result =
                service.getMachinesByStatus(MachineStatus.FAULTY);

        assertEquals(1, result.size());
        assertEquals(MachineStatus.FAULTY, result.get(0).getStatus());

        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    /**
     * Tests that getWorkingMachines() returns only machines
     * with WORKING status.
     */
    @Test
    void shouldReturnWorkingMachines() {
        Machinery machine = createMachine(1L, "MRI Machine", MachineStatus.WORKING);

        when(machineryRepository.findByStatus(MachineStatus.WORKING))
                .thenReturn(List.of(machine));

        List<Machinery> result = service.getWorkingMachines();

        assertEquals(1, result.size());
        assertEquals(MachineStatus.WORKING, result.get(0).getStatus());

        verify(machineryRepository).findByStatus(MachineStatus.WORKING);
    }

    /**
     * Tests that getFaultyMachines() returns only machines
     * with FAULTY status.
     */
    @Test
    void shouldReturnFaultyMachines() {
        Machinery machine = createMachine(1L, "X-Ray Machine", MachineStatus.FAULTY);

        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(machine));

        List<Machinery> result = service.getFaultyMachines();

        assertEquals(1, result.size());
        assertEquals(MachineStatus.FAULTY, result.get(0).getStatus());

        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    /**
     * Tests that getMachinesUnderMaintenance() returns only machines
     * with UNDER_MAINTENANCE status.
     */
    @Test
    void shouldReturnMachinesUnderMaintenance() {
        Machinery machine = createMachine(
                1L,
                "CT Scanner",
                MachineStatus.UNDER_MAINTENANCE
        );

        when(machineryRepository.findByStatus(MachineStatus.UNDER_MAINTENANCE))
                .thenReturn(List.of(machine));

        List<Machinery> result = service.getMachinesUnderMaintenance();

        assertEquals(1, result.size());
        assertEquals(MachineStatus.UNDER_MAINTENANCE, result.get(0).getStatus());

        verify(machineryRepository)
                .findByStatus(MachineStatus.UNDER_MAINTENANCE);
    }

    /**
     * Tests that getMachinesByRoom() returns machines
     * installed in the selected specialized room.
     */
    @Test
    void shouldReturnMachinesByRoom() {
        Machinery machine = createMachine(1L, "MRI Machine", MachineStatus.WORKING);

        when(machineryRepository.findBySpecializedRoomId(10L))
                .thenReturn(List.of(machine));

        List<Machinery> result = service.getMachinesByRoom(10L);

        assertEquals(1, result.size());
        assertEquals("MRI Machine", result.get(0).getName());

        verify(machineryRepository).findBySpecializedRoomId(10L);
    }

    /**
     * Tests that getFaultyMachinesForTechnician() uses the Technician
     * domain method to return only faulty machines.
     */
    @Test
    void shouldReturnFaultyMachinesForTechnician() {
        Technician technician = new Technician();

        Machinery working = createMachine(1L, "MRI Machine", MachineStatus.WORKING);
        Machinery faulty = createMachine(2L, "X-Ray Machine", MachineStatus.FAULTY);

        when(machineryRepository.findAll()).thenReturn(List.of(working, faulty));

        List<Machinery> result =
                service.getFaultyMachinesForTechnician(technician);

        assertEquals(1, result.size());
        assertEquals("X-Ray Machine", result.get(0).getName());
        assertEquals(MachineStatus.FAULTY, result.get(0).getStatus());

        verify(machineryRepository).findAll();
    }

    /**
     * Tests that updateMachineStatus() saves the machine
     * and creates an audit log entry.
     *
     * The final status is not asserted because Machinery.updateStatus()
     * uses random logic.
     */
    @Test
    void shouldUpdateMachineStatus() {
        Machinery machine = createMachine(1L, "MRI Machine", MachineStatus.WORKING);

        when(machineryRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(machineryRepository.save(machine)).thenReturn(machine);

        Machinery result =
                service.updateMachineStatus(1L, MachineStatus.FAULTY);

        assertNotNull(result);

        verify(machineryRepository).findById(1L);
        verify(machineryRepository).save(machine);
        verify(auditService).log(
                eq("UPDATE_MACHINE_STATUS"),
                eq("Machinery"),
                eq("1"),
                contains("Status changed from WORKING to FAULTY")
        );
    }

    /**
     * Tests that reportFaultyMachine() marks a machine as faulty
     * and creates a fault report audit log.
     */
    @Test
    void shouldReportFaultyMachine() {
        Machinery machine = createMachine(1L, "X-Ray Machine", MachineStatus.WORKING);

        when(machineryRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(machineryRepository.save(machine)).thenReturn(machine);

        Machinery result =
                service.reportFaultyMachine(1L, "Machine stopped responding");

        assertNotNull(result);

        verify(auditService).log(
                eq("UPDATE_MACHINE_STATUS"),
                eq("Machinery"),
                eq("1"),
                contains("ALERT")
        );

        verify(auditService).log(
                eq("REPORT_FAULTY_MACHINE"),
                eq("Machinery"),
                eq("1"),
                contains("Machine stopped responding")
        );
    }

    /**
     * Tests that repairMachine() updates the machine
     * and creates a repair audit log.
     */
    @Test
    void shouldRepairMachine() {
        Machinery machine = createMachine(1L, "X-Ray Machine", MachineStatus.FAULTY);

        when(machineryRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(machineryRepository.save(machine)).thenReturn(machine);

        Machinery result = service.repairMachine(1L);

        assertNotNull(result);

        verify(auditService).log(
                eq("REPAIR_MACHINE"),
                eq("Machinery"),
                eq("1"),
                contains("Machine repaired")
        );
    }

    /**
     * Tests that scheduleMaintenance() updates the machine
     * and creates a maintenance audit log.
     */
    @Test
    void shouldScheduleMaintenance() {
        Machinery machine = createMachine(1L, "CT Scanner", MachineStatus.WORKING);

        when(machineryRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(machineryRepository.save(machine)).thenReturn(machine);

        Machinery result =
                service.scheduleMaintenance(1L, "Monthly check");

        assertNotNull(result);

        verify(auditService).log(
                eq("SCHEDULE_MAINTENANCE"),
                eq("Machinery"),
                eq("1"),
                contains("Monthly check")
        );
    }

    /**
     * Tests that getFaultyMachineCount() returns the number
     * of faulty machines.
     */
    @Test
    void shouldReturnFaultyMachineCount() {
        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(
                        createMachine(1L, "X-Ray", MachineStatus.FAULTY),
                        createMachine(2L, "MRI", MachineStatus.FAULTY)
                ));

        long result = service.getFaultyMachineCount();

        assertEquals(2, result);

        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    /**
     * Tests that getMaintenanceMachineCount() returns the number
     * of machines under maintenance.
     */
    @Test
    void shouldReturnMaintenanceMachineCount() {
        when(machineryRepository.findByStatus(MachineStatus.UNDER_MAINTENANCE))
                .thenReturn(List.of(
                        createMachine(1L, "CT Scanner", MachineStatus.UNDER_MAINTENANCE)
                ));

        long result = service.getMaintenanceMachineCount();

        assertEquals(1, result);

        verify(machineryRepository)
                .findByStatus(MachineStatus.UNDER_MAINTENANCE);
    }

    /**
     * Tests that getWorkingMachineCount() returns the number
     * of working machines.
     */
    @Test
    void shouldReturnWorkingMachineCount() {
        when(machineryRepository.findByStatus(MachineStatus.WORKING))
                .thenReturn(List.of(
                        createMachine(1L, "MRI", MachineStatus.WORKING),
                        createMachine(2L, "CT Scanner", MachineStatus.WORKING)
                ));

        long result = service.getWorkingMachineCount();

        assertEquals(2, result);

        verify(machineryRepository).findByStatus(MachineStatus.WORKING);
    }

    /**
     * Tests that getTotalMachineCount() returns the total number
     * of machines from the repository.
     */
    @Test
    void shouldReturnTotalMachineCount() {
        when(machineryRepository.count()).thenReturn(5L);

        long result = service.getTotalMachineCount();

        assertEquals(5L, result);

        verify(machineryRepository).count();
    }

    /**
     * Tests that hasFaultyMachines() returns true
     * when at least one faulty machine exists.
     */
    @Test
    void shouldReturnTrueWhenThereAreFaultyMachines() {
        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(
                        createMachine(1L, "X-Ray", MachineStatus.FAULTY)
                ));

        boolean result = service.hasFaultyMachines();

        assertTrue(result);

        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    /**
     * Tests that hasFaultyMachines() returns false
     * when no faulty machines exist.
     */
    @Test
    void shouldReturnFalseWhenThereAreNoFaultyMachines() {
        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of());

        boolean result = service.hasFaultyMachines();

        assertFalse(result);

        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    /**
     * Tests that getActiveAlerts() returns alert information
     * for faulty machines with known room locations.
     */
    @Test
    void shouldReturnActiveAlertsWithRoomLocation() {
        SpecializedRoom room = new SpecializedRoom("MRI-101", "MRI");

        Machinery machine = createMachine(1L, "MRI Machine", MachineStatus.FAULTY);
        machine.setSpecializedRoom(room);

        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(machine));

        List<MachineryService.AlertInfo> result = service.getActiveAlerts();

        assertEquals(1, result.size());

        MachineryService.AlertInfo alert = result.get(0);

        assertEquals(1L, alert.getId());
        assertEquals("MRI Machine", alert.getName());
        assertEquals(MachineStatus.FAULTY, alert.getStatus());
        assertEquals("MRI-101", alert.getLocation());
        assertEquals(
                "Machine reported faulty. Immediate attention required.",
                alert.getMessage()
        );

        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    /**
     * Tests that getActiveAlerts() returns Unknown Location
     * when the faulty machine has no specialized room assigned.
     */
    @Test
    void shouldReturnActiveAlertsWithUnknownLocation() {
        Machinery machine = createMachine(1L, "Portable Monitor", MachineStatus.FAULTY);
        machine.setSpecializedRoom(null);

        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(machine));

        List<MachineryService.AlertInfo> result = service.getActiveAlerts();

        assertEquals(1, result.size());
        assertEquals("Unknown Location", result.get(0).getLocation());

        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    private Machinery createMachine(
            Long id,
            String name,
            MachineStatus status
    ) {
        Machinery machine = new Machinery();
        machine.setId(id);
        machine.setName(name);
        machine.setStatus(status);
        return machine;
    }
}