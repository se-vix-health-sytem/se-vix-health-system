package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
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
 * Arrange = prepare fake data and mock behavior
 * Act = call the method being tested
 * Assert = check the result
 * Verify = check that mocks were called correctly
 */
@ExtendWith(MockitoExtension.class)
class MachineryServiceTest {

    @Mock
    private MachineryRepository machineryRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MachineryService service;

    @Test
    void shouldReturnAllMachines() {
        // Arrange
        Machinery m1 = new Machinery("MRI");
        m1.setId(1L);

        Machinery m2 = new Machinery("X-Ray");
        m2.setId(2L);

        when(machineryRepository.findAll())
                .thenReturn(List.of(m1, m2));

        // Act
        List<Machinery> result = service.getAllMachines();

        // Assert
        assertEquals(2, result.size());
        assertEquals("MRI", result.get(0).getName());

        // Verify
        verify(machineryRepository).findAll();
    }

    @Test
    void shouldGetMachineById() {
        // Arrange
        Machinery machine = new Machinery("MRI");
        machine.setId(1L);

        when(machineryRepository.findById(1L))
                .thenReturn(Optional.of(machine));

        // Act
        Machinery result = service.getMachineById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("MRI", result.getName());

        // Verify
        verify(machineryRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenMachineNotFound() {
        // Arrange
        when(machineryRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.getMachineById(99L)
        );

        assertTrue(exception.getMessage().contains("Machine not found"));

        // Verify
        verify(machineryRepository).findById(99L);
    }

    @Test
    void shouldReturnMachinesByStatus() {
        // Arrange
        Machinery machine = new Machinery("MRI");
        machine.setId(1L);
        machine.setStatus(MachineStatus.WORKING);

        when(machineryRepository.findByStatus(MachineStatus.WORKING))
                .thenReturn(List.of(machine));

        // Act
        List<Machinery> result =
                service.getMachinesByStatus(MachineStatus.WORKING);

        // Assert
        assertEquals(1, result.size());
        assertEquals(MachineStatus.WORKING, result.get(0).getStatus());

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.WORKING);
    }

    @Test
    void shouldReturnWorkingMachines() {
        // Arrange
        Machinery machine = new Machinery("MRI");
        machine.setStatus(MachineStatus.WORKING);

        when(machineryRepository.findByStatus(MachineStatus.WORKING))
                .thenReturn(List.of(machine));

        // Act
        List<Machinery> result = service.getWorkingMachines();

        // Assert
        assertEquals(1, result.size());
        assertEquals(MachineStatus.WORKING, result.get(0).getStatus());

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.WORKING);
    }

    @Test
    void shouldReturnFaultyMachines() {
        // Arrange
        Machinery machine = new Machinery("X-Ray");
        machine.setStatus(MachineStatus.FAULTY);

        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(machine));

        // Act
        List<Machinery> result = service.getFaultyMachines();

        // Assert
        assertEquals(1, result.size());
        assertEquals(MachineStatus.FAULTY, result.get(0).getStatus());

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    @Test
    void shouldReturnMachinesUnderMaintenance() {
        // Arrange
        Machinery machine = new Machinery("CT Scanner");
        machine.setStatus(MachineStatus.UNDER_MAINTENANCE);

        when(machineryRepository.findByStatus(MachineStatus.UNDER_MAINTENANCE))
                .thenReturn(List.of(machine));

        // Act
        List<Machinery> result = service.getMachinesUnderMaintenance();

        // Assert
        assertEquals(1, result.size());
        assertEquals(MachineStatus.UNDER_MAINTENANCE, result.get(0).getStatus());

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.UNDER_MAINTENANCE);
    }

    @Test
    void shouldReturnMachinesByRoom() {
        // Arrange
        Machinery machine = new Machinery("MRI");
        machine.setId(1L);

        when(machineryRepository.findBySpecializedRoomId(10L))
                .thenReturn(List.of(machine));

        // Act
        List<Machinery> result = service.getMachinesByRoom(10L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("MRI", result.get(0).getName());

        // Verify
        verify(machineryRepository).findBySpecializedRoomId(10L);
    }

    @Test
    void shouldUpdateMachineStatus() {
        // Arrange
        Machinery machine = new Machinery("MRI");
        machine.setId(1L);
        machine.setStatus(MachineStatus.WORKING);

        when(machineryRepository.findById(1L))
                .thenReturn(Optional.of(machine));

        when(machineryRepository.save(machine))
                .thenReturn(machine);

        // Act
        Machinery result =
                service.updateMachineStatus(1L, MachineStatus.UNDER_MAINTENANCE);

        // Assert
        assertEquals(MachineStatus.UNDER_MAINTENANCE, result.getStatus());

        // Verify
        verify(machineryRepository).findById(1L);
        verify(machineryRepository).save(machine);
        verify(auditService).log(
                eq("UPDATE_MACHINE_STATUS"),
                eq("Machinery"),
                eq("1"),
                contains("Status changed from WORKING to UNDER_MAINTENANCE")
        );
    }

    @Test
    void shouldReportFaultyMachine() {
        // Arrange
        Machinery machine = new Machinery("MRI");
        machine.setId(1L);
        machine.setStatus(MachineStatus.WORKING);

        when(machineryRepository.findById(1L))
                .thenReturn(Optional.of(machine));

        when(machineryRepository.save(machine))
                .thenReturn(machine);

        // Act
        Machinery result =
                service.reportFaultyMachine(1L, "Screen not working");

        // Assert
        assertEquals(MachineStatus.FAULTY, result.getStatus());

        // Verify
        verify(machineryRepository).findById(1L);
        verify(machineryRepository).save(machine);
        verify(auditService).log(
                eq("REPORT_FAULTY_MACHINE"),
                eq("Machinery"),
                eq("1"),
                contains("Fault reported")
        );
    }

    @Test
    void shouldRepairMachine() {
        // Arrange
        Machinery machine = new Machinery("MRI");
        machine.setId(1L);
        machine.setStatus(MachineStatus.FAULTY);

        when(machineryRepository.findById(1L))
                .thenReturn(Optional.of(machine));

        when(machineryRepository.save(machine))
                .thenReturn(machine);

        // Act
        Machinery result = service.repairMachine(1L);

        // Assert
        assertEquals(MachineStatus.WORKING, result.getStatus());

        // Verify
        verify(machineryRepository).findById(1L);
        verify(machineryRepository).save(machine);
        verify(auditService).log(
                eq("REPAIR_MACHINE"),
                eq("Machinery"),
                eq("1"),
                contains("Machine repaired")
        );
    }

    @Test
    void shouldScheduleMaintenance() {
        // Arrange
        Machinery machine = new Machinery("MRI");
        machine.setId(1L);
        machine.setStatus(MachineStatus.WORKING);

        when(machineryRepository.findById(1L))
                .thenReturn(Optional.of(machine));

        when(machineryRepository.save(machine))
                .thenReturn(machine);

        // Act
        Machinery result =
                service.scheduleMaintenance(1L, "Monthly check");

        // Assert
        assertEquals(MachineStatus.UNDER_MAINTENANCE, result.getStatus());

        // Verify
        verify(machineryRepository).findById(1L);
        verify(machineryRepository).save(machine);
        verify(auditService).log(
                eq("SCHEDULE_MAINTENANCE"),
                eq("Machinery"),
                eq("1"),
                contains("Maintenance scheduled")
        );
    }

    @Test
    void shouldReturnFaultyMachineCount() {
        // Arrange
        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(new Machinery("MRI"), new Machinery("X-Ray")));

        // Act
        long result = service.getFaultyMachineCount();

        // Assert
        assertEquals(2, result);

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    @Test
    void shouldReturnMaintenanceMachineCount() {
        // Arrange
        when(machineryRepository.findByStatus(MachineStatus.UNDER_MAINTENANCE))
                .thenReturn(List.of(new Machinery("MRI")));

        // Act
        long result = service.getMaintenanceMachineCount();

        // Assert
        assertEquals(1, result);

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.UNDER_MAINTENANCE);
    }

    @Test
    void shouldReturnWorkingMachineCount() {
        // Arrange
        when(machineryRepository.findByStatus(MachineStatus.WORKING))
                .thenReturn(List.of(new Machinery("MRI"), new Machinery("CT")));

        // Act
        long result = service.getWorkingMachineCount();

        // Assert
        assertEquals(2, result);

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.WORKING);
    }

    @Test
    void shouldReturnTotalMachineCount() {
        // Arrange
        when(machineryRepository.count())
                .thenReturn(5L);

        // Act
        long result = service.getTotalMachineCount();

        // Assert
        assertEquals(5L, result);

        // Verify
        verify(machineryRepository).count();
    }

    @Test
    void shouldReturnTrueWhenThereAreFaultyMachines() {
        // Arrange
        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(new Machinery("MRI")));

        // Act
        boolean result = service.hasFaultyMachines();

        // Assert
        assertTrue(result);

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    @Test
    void shouldReturnFalseWhenThereAreNoFaultyMachines() {
        // Arrange
        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of());

        // Act
        boolean result = service.hasFaultyMachines();

        // Assert
        assertFalse(result);

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    @Test
    void shouldReturnActiveAlerts() {
        // Arrange
        SpecializedRoom room = new SpecializedRoom();
        room.setNumber("MRI-101");

        Machinery machine = new Machinery("MRI");
        machine.setId(1L);
        machine.setStatus(MachineStatus.FAULTY);
        machine.setSpecializedRoom(room);

        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(machine));

        // Act
        List<MachineryService.AlertInfo> result =
                service.getActiveAlerts();

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("MRI", result.get(0).getName());
        assertEquals(MachineStatus.FAULTY, result.get(0).getStatus());
        assertEquals("MRI-101", result.get(0).getLocation());
        assertTrue(result.get(0).getMessage().contains("faulty"));

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }

    @Test
    void shouldReturnActiveAlertsWithUnknownLocationWhenRoomIsNull() {
        // Arrange
        Machinery machine = new Machinery("X-Ray");
        machine.setId(2L);
        machine.setStatus(MachineStatus.FAULTY);
        machine.setSpecializedRoom(null);

        when(machineryRepository.findByStatus(MachineStatus.FAULTY))
                .thenReturn(List.of(machine));

        // Act
        List<MachineryService.AlertInfo> result =
                service.getActiveAlerts();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Unknown Location", result.get(0).getLocation());

        // Verify
        verify(machineryRepository).findByStatus(MachineStatus.FAULTY);
    }
}
