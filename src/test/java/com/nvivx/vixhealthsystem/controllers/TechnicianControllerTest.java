package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.resource.Machinery;
import com.nvivx.vixhealthsystem.service.resources.MachineryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TechnicianControllerTest {

    private final MachineryService machineryService = Mockito.mock(MachineryService.class);
    private final TechnicianController controller = new TechnicianController(machineryService);


    @Test
    void shouldLoadDashboard() {
        ExtendedModelMap model = new ExtendedModelMap();

        Mockito.when(machineryService.getTotalMachineCount()).thenReturn(5L);
        Mockito.when(machineryService.getFaultyMachineCount()).thenReturn(2L);
        Mockito.when(machineryService.getMaintenanceMachineCount()).thenReturn(1L);
        Mockito.when(machineryService.getActiveAlerts()).thenReturn(List.of());

        String view = controller.dashboard(model);

        assertEquals("technician/dashboard", view);
        assertEquals(5L, model.get("totalMachines"));
    }

    @Test
    void shouldViewAllMachines() {
        ExtendedModelMap model = new ExtendedModelMap();

        Machinery m1 = Mockito.mock(Machinery.class);
        Machinery m2 = Mockito.mock(Machinery.class);

        Mockito.when(machineryService.getAllMachines())
                .thenReturn(List.of(m1, m2));

        String view = controller.viewAllMachines(model);

        assertEquals("technician/machines", view);
        assertEquals(2, ((List<?>) model.get("machines")).size());
    }


    @Test
    void shouldViewFaultyMachines() {
        ExtendedModelMap model = new ExtendedModelMap();

        Mockito.when(machineryService.getFaultyMachines())
                .thenReturn(List.of());

        String view = controller.viewFaultyMachines(model);

        assertEquals("technician/machines", view);
        assertTrue((Boolean) model.get("isFaultyView"));
    }


    @Test
    void shouldUpdateMachineStatusToFaulty() {
        ExtendedModelMap model = new ExtendedModelMap();

        Machinery machine = Mockito.mock(Machinery.class);
        Mockito.when(machine.getName()).thenReturn("X-Ray Machine");

        Mockito.when(machineryService.updateMachineStatus(
                Mockito.eq(1L),
                Mockito.eq(MachineStatus.FAULTY)
        )).thenReturn(machine);

        String view = controller.updateMachineStatus(
                1L,
                "FAULTY",
                model
        );

        assertEquals("technician/result", view);
        assertTrue(model.get("message").toString().contains("FAULTY"));
        assertTrue(model.get("message").toString().contains("X-Ray Machine"));
    }


    @Test
    void shouldViewMachineDetails() {
        ExtendedModelMap model = new ExtendedModelMap();

        Machinery machine = Mockito.mock(Machinery.class);
        Mockito.when(machine.getName()).thenReturn("MRI Scanner");

        Mockito.when(machineryService.getMachineById(1L))
                .thenReturn(machine);

        String view = controller.viewMachineDetails(1L, model);

        assertEquals("technician/machine-details", view);
        assertEquals(machine, model.get("machine"));
    }


    @Test
    void shouldRepairMachine() {
        ExtendedModelMap model = new ExtendedModelMap();

        Machinery machine = Mockito.mock(Machinery.class);
        Mockito.when(machine.getName()).thenReturn("Ventilator");

        Mockito.when(machineryService.repairMachine(1L))
                .thenReturn(machine);

        String view = controller.repairMachine(1L, model);

        assertEquals("technician/result", view);
        assertTrue(model.get("message").toString().contains("repaired"));
    }
}