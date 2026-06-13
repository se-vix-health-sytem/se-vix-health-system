/* package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.staff.StaffManagerController;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StaffManagerControllerTest {

    private final EmployeeService employeeService =
            Mockito.mock(EmployeeService.class);

    private final VacationService vacationService =
            Mockito.mock(VacationService.class);

    private final ShiftService shiftService =
            Mockito.mock(ShiftService.class);

    private final AuditService auditService =
            Mockito.mock(AuditService.class);

    private final JsonAppointmentRepository appointmentRepository =
            Mockito.mock(JsonAppointmentRepository.class);

    private final StaffManagerController controller =
            new StaffManagerController(
                    employeeService,
                    vacationService,
                    shiftService,
                    auditService,
                    appointmentRepository
            );

    // ================= DASHBOARD =================

    @Test
    void shouldLoadDashboard() {
        ExtendedModelMap model = new ExtendedModelMap();

        Mockito.when(employeeService.getTotalEmployeeCount())
                .thenReturn(10L);

        Mockito.when(vacationService.getPendingRequests())
                .thenReturn(List.of());

        Mockito.when(auditService.getRecentLogs(10))
                .thenReturn(List.of());

        String view = controller.dashboard(model);

        assertEquals("staff-manager/dashboard", view);
        assertEquals(10L, model.get("totalEmployees"));
        assertEquals(0, model.get("pendingVacations"));
    }

    // ================= EMPLOYEE LIST =================

    @Test
    void shouldListAllEmployees() {
        ExtendedModelMap model = new ExtendedModelMap();

        Employee e1 = new MedicalSpecialist();
        Employee e2 = new Secretary();

        Mockito.when(employeeService.findAllEmployees())
                .thenReturn(List.of(e1, e2));

        String view = controller.listEmployees(null, model);

        assertEquals("staff-manager/employees", view);
        assertEquals(2, ((List<?>) model.get("employees")).size());
    }

    @Test
    void shouldFilterMedicalSpecialists() {
        ExtendedModelMap model = new ExtendedModelMap();

        Employee e1 = new MedicalSpecialist();
        Employee e2 = new Secretary();

        Mockito.when(employeeService.findAllEmployees())
                .thenReturn(List.of(e1, e2));

        String view = controller.listEmployees("MEDICAL_SPECIALIST", model);

        assertEquals("staff-manager/employees", view);
    }

    // ================= DELETE EMPLOYEE =================

    @Test
    void shouldDeleteEmployee() {
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.deleteEmployee(1L, model);

        Mockito.verify(employeeService).deleteEmployee(1L);

        assertEquals("staff-manager/result", view);
    }

    // ================= SHIFT =================

    @Test
    void shouldAssignShift() {
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.assignShift(
                1L,
                "2026-06-12",
                "NIGHT",
                model
        );

        Mockito.verify(shiftService).assignShift(
                Mockito.eq(1L),
                Mockito.eq(LocalDate.parse("2026-06-12")),
                Mockito.eq("NIGHT")
        );

        assertEquals("staff-manager/result", view);
    }

    // ================= VACATION =================

    @Test
    void shouldApproveVacation() {
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.approveVacation(1, model);

        Mockito.verify(vacationService).approveVacation(1);

        assertEquals("staff-manager/result", view);
    }

    @Test
    void shouldDenyVacation() {
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.denyVacation(2, model);

        Mockito.verify(vacationService).denyVacation(2);

        assertEquals("staff-manager/result", view);
    }
} */