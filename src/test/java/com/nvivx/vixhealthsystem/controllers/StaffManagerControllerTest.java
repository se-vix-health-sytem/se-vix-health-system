package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.staff.StaffManagerController;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.resources.ResourceTakeLogStore;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;

import jakarta.servlet.http.HttpSession;

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

    private final ResourceTakeLogStore takeLogStore =
            Mockito.mock(ResourceTakeLogStore.class);

    private final HttpSession session =
            Mockito.mock(HttpSession.class);

    private final StaffManagerController controller =
            new StaffManagerController(
                    employeeService,
                    vacationService,
                    shiftService,
                    auditService,
                    appointmentRepository,
                    takeLogStore
            );

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

    @Test
    void shouldAssignShift() {
        ExtendedModelMap model = new ExtendedModelMap();

        Mockito.when(vacationService.getApprovedRequestsForEmployee(1))
                .thenReturn(List.of());

        String view = controller.assignShift(
                1L,
                "2026-06-12",
                "NIGHT",
                model
        );

        Mockito.verify(shiftService).assignShift(
                1L,
                LocalDate.parse("2026-06-12"),
                "NIGHT"
        );

        assertEquals("staff-manager/result", view);
    }

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
}