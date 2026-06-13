package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.staff.TechnicianController;
import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.Technician;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.resources.MachineryService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TechnicianControllerTest {

    private final MachineryService machineryService = mock(MachineryService.class);
    private final EmployeeService employeeService = mock(EmployeeService.class);
    private final ShiftService shiftService = mock(ShiftService.class);
    private final VacationService vacationService = mock(VacationService.class);
    private final HttpSession session = mock(HttpSession.class);

    private final TechnicianController controller =
            new TechnicianController(machineryService, employeeService, shiftService, vacationService);

    // ================= DASHBOARD =================

    @Test
    void dashboard_shouldWork() {
        when(machineryService.getTotalMachineCount()).thenReturn(10L);
        when(machineryService.getFaultyMachineCount()).thenReturn(2L);
        when(machineryService.getMaintenanceMachineCount()).thenReturn(1L);
        when(machineryService.getActiveAlerts()).thenReturn(List.of());

        Model model = new ConcurrentModel();

        String view = controller.dashboard(model);

        assertEquals("technician/dashboard", view);
        assertEquals("Technician Dashboard", model.getAttribute("pageTitle"));
        assertEquals(10L, model.getAttribute("totalMachines"));
    }

    // ================= ALL MACHINES =================

    @Test
    void viewAllMachines_shouldWork() {
        when(machineryService.getAllMachines()).thenReturn(List.of());

        Model model = new ConcurrentModel();

        String view = controller.viewAllMachines(model);

        assertEquals("technician/machines", view);
        assertEquals("All Machines", model.getAttribute("pageTitle"));
    }

    // ================= FAULTY MACHINES =================

    @Test
    void viewFaultyMachines_shouldWork() {
        Technician tech = new Technician();
        tech.setId(1L);

        when(session.getAttribute("user")).thenReturn(tech);
        when(employeeService.findById(1L)).thenReturn(tech);
        when(machineryService.getFaultyMachinesForTechnician(any()))
                .thenReturn(List.of());

        Model model = new ConcurrentModel();

        String view = controller.viewFaultyMachines(session, model);

        assertEquals("technician/machines", view);
        assertTrue((Boolean) model.getAttribute("isFaultyView"));
    }

    // ================= UPDATE STATUS =================

    @Test
    void updateMachineStatus_shouldWork() {

        // IMPORTANT: your service returns a MACHINE-like object
        var machine = mock(com.nvivx.vixhealthsystem.model.resource.Machinery.class);

        when(machine.getName()).thenReturn("MRI Scanner");

        when(machineryService.updateMachineStatus(anyLong(), any(MachineStatus.class)))
                .thenReturn(machine);

        Model model = new ConcurrentModel();

        String view = controller.updateMachineStatus(1L, "FAULTY", model);

        assertEquals("technician/result", view);
        assertTrue(model.getAttribute("message").toString().contains("MRI Scanner"));
    }

    // ================= ALERTS =================

    @Test
    void alerts_shouldWork() {
        when(machineryService.getActiveAlerts()).thenReturn(List.of());

        Model model = new ConcurrentModel();

        String view = controller.viewAlerts(model);

        assertEquals("technician/alerts", view);
        assertEquals("Machine Alerts", model.getAttribute("pageTitle"));
    }

    // ================= PROFILE =================

    @Test
    void profile_shouldRedirectIfNoSession() {
        when(session.getAttribute("user")).thenReturn(null);

        Model model = new ConcurrentModel();

        String view = controller.viewProfile(session, model);

        assertEquals("redirect:/login", view);
    }

    @Test
    void profile_shouldWork() {
        Employee emp = new Technician();
        emp.setId(1L);

        when(session.getAttribute("user")).thenReturn(emp);
        when(employeeService.findById(1L)).thenReturn(emp);

        Model model = new ConcurrentModel();

        String view = controller.viewProfile(session, model);

        assertEquals("employee/profile", view);
        assertEquals("My Profile", model.getAttribute("pageTitle"));
    }

    // ================= SHIFTS =================

    @Test
    void shifts_shouldWork() {
        Technician tech = new Technician();
        tech.setId(1L);

        when(session.getAttribute("user")).thenReturn(tech);

        when(shiftService.getShiftsForEmployee(anyLong()))
                .thenReturn(List.of());

        when(vacationService.getApprovedRequestsForEmployee(anyInt()))
                .thenReturn(List.of(mock(VacationRequest.class)));

        Model model = new ConcurrentModel();

        String view = controller.viewMyShifts(session, model);

        assertEquals("employee/my-shifts", view);
        assertEquals("My Shifts", model.getAttribute("pageTitle"));
    }

    // ================= MACHINE DETAILS =================

    @Test
    void machineDetails_shouldWork() {
        var machine = mock(com.nvivx.vixhealthsystem.model.resource.Machinery.class);
        when(machine.getName()).thenReturn("Pump");

        when(machineryService.getMachineById(1L)).thenReturn(machine);

        Model model = new ConcurrentModel();

        String view = controller.viewMachineDetails(1L, model);

        assertEquals("technician/machine-details", view);
    }

    // ================= REPAIR =================

    @Test
    void repairMachine_shouldWork() {
        var machine = mock(com.nvivx.vixhealthsystem.model.resource.Machinery.class);

        when(machine.getName()).thenReturn("Ventilator");

        when(machineryService.repairMachine(1L)).thenReturn(machine);

        Model model = new ConcurrentModel();

        String view = controller.repairMachine(1L, "fixed", model);

        assertEquals("technician/result", view);
        assertTrue(model.getAttribute("message").toString().contains("Ventilator"));
    }
}