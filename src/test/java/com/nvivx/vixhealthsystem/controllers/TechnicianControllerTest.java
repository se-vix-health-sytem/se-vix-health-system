package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.staff.TechnicianController;
import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.resource.Machinery;
import com.nvivx.vixhealthsystem.service.resources.MachineryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TechnicianControllerTest {

    @Mock
    private MachineryService machineryService;

    @Mock
    private Model model;

    @InjectMocks
    private TechnicianController technicianController;

    private Machinery testMachine;

    @BeforeEach
    void setUp() {
        testMachine = new Machinery();
        testMachine.setId(1L);
        testMachine.setName("Test MRI Machine");
        testMachine.setStatus(MachineStatus.WORKING);
    }

    @Test
    void testDashboard() {
        // Arrange
        when(machineryService.getTotalMachineCount()).thenReturn(5L);
        when(machineryService.getFaultyMachineCount()).thenReturn(1L);
        when(machineryService.getMaintenanceMachineCount()).thenReturn(1L);
        when(machineryService.getActiveAlerts()).thenReturn(java.util.Collections.emptyList());

        // Act
        String result = technicianController.dashboard(model);

        // Assert
        assertEquals("technician/dashboard", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Technician Dashboard"));
        verify(model).addAttribute(eq("currentPage"), eq("dashboard"));
        verify(model).addAttribute(eq("totalMachines"), eq(5L));
        verify(model).addAttribute(eq("faultyCount"), eq(1L));
        verify(model).addAttribute(eq("maintenanceCount"), eq(1L));
        verify(model).addAttribute(eq("alertCount"), eq(1L));
        verify(model).addAttribute(eq("activeAlerts"), any());
    }

    @Test
    void testViewAllMachines() {
        // Arrange
        when(machineryService.getAllMachines()).thenReturn(java.util.Collections.emptyList());

        // Act
        String result = technicianController.viewAllMachines(model);

        // Assert
        assertEquals("technician/machines", result);
        verify(model).addAttribute(eq("pageTitle"), eq("All Machines"));
        verify(model).addAttribute(eq("currentPage"), eq("machines"));
        verify(model).addAttribute(eq("machines"), any());
        verify(model).addAttribute(eq("isFaultyView"), eq(false));
    }

    @Test
    void testViewFaultyMachines() {
        // Arrange
        when(machineryService.getFaultyMachines()).thenReturn(java.util.Collections.emptyList());

        // Act
        String result = technicianController.viewFaultyMachines(model);

        // Assert
        assertEquals("technician/machines", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Faulty Machines"));
        verify(model).addAttribute(eq("currentPage"), eq("faultyMachines"));
        verify(model).addAttribute(eq("machines"), any());
        verify(model).addAttribute(eq("isFaultyView"), eq(true));
    }

    @Test
    void testViewMaintenanceMachines() {
        // Arrange
        when(machineryService.getMachinesUnderMaintenance()).thenReturn(java.util.Collections.emptyList());

        // Act
        String result = technicianController.viewMaintenanceMachines(model);

        // Assert
        assertEquals("technician/machines", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Machines Under Maintenance"));
        verify(model).addAttribute(eq("currentPage"), eq("machines"));
        verify(model).addAttribute(eq("machines"), any());
    }

    @Test
    void testUpdateMachineStatus() {
        // Arrange
        when(machineryService.updateMachineStatus(eq(1L), eq(MachineStatus.WORKING)))
                .thenReturn(testMachine);

        // Act
        String result = technicianController.updateMachineStatus(1L, "WORKING", model);

        // Assert
        assertEquals("technician/result", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Machine Status Updated"));
        verify(model).addAttribute(eq("message"), anyString());
    }

    @Test
    void testViewAlerts() {
        // Arrange
        when(machineryService.getActiveAlerts()).thenReturn(java.util.Collections.emptyList());

        // Act
        String result = technicianController.viewAlerts(model);

        // Assert
        assertEquals("technician/alerts", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Machine Alerts"));
        verify(model).addAttribute(eq("currentPage"), eq("alerts"));
        verify(model).addAttribute(eq("alerts"), any());
        verify(model).addAttribute(eq("alertCount"), eq(0));
    }

    @Test
    void testViewMaintenanceHistory() {
        // Arrange
        when(machineryService.getAllMachines()).thenReturn(java.util.Collections.emptyList());

        // Act
        String result = technicianController.viewMaintenanceHistory(model);

        // Assert
        assertEquals("technician/maintenance-history", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Machine History"));
        verify(model).addAttribute(eq("machines"), any());
    }

    @Test
    void testViewMachineDetails() {
        // Arrange
        when(machineryService.getMachineById(1L)).thenReturn(testMachine);

        // Act
        String result = technicianController.viewMachineDetails(1L, model);

        // Assert
        assertEquals("technician/machine-details", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Machine Details - Test MRI Machine"));
        verify(model).addAttribute(eq("currentPage"), eq("machines"));
        verify(model).addAttribute(eq("machine"), eq(testMachine));
    }

    @Test
    void testRepairMachine() {
        // Arrange
        when(machineryService.repairMachine(1L)).thenReturn(testMachine);

        // Act - Note: repairMachine accepts notes as optional parameter
        String result = technicianController.repairMachine(1L, "Fixed the power supply", model);

        // Assert
        assertEquals("technician/result", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Machine Repaired"));
        verify(model).addAttribute(eq("message"), anyString());
    }

    @Test
    void testRepairMachineWithoutNotes() {
        // Arrange
        when(machineryService.repairMachine(1L)).thenReturn(testMachine);

        // Act - Test with null notes
        String result = technicianController.repairMachine(1L, null, model);

        // Assert
        assertEquals("technician/result", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Machine Repaired"));
        verify(model).addAttribute(eq("message"), anyString());
    }

    @Test
    void testViewMachineDetailsNotFound() {
        // Arrange
        when(machineryService.getMachineById(999L))
                .thenThrow(new RuntimeException("Machine not found with id: 999"));

        // Act
        String result = technicianController.viewMachineDetails(999L, model);

        // Assert
        assertEquals("technician/result", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Machine Not Found"));
        verify(model).addAttribute(eq("message"), anyString());
    }
}