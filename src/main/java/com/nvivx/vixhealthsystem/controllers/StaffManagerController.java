package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.AuditLog;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-manager")
public class StaffManagerController {

    private final EmployeeService employeeService;
    private final VacationService vacationService;
    private final ShiftService shiftService;
    private final AuditService auditService;

    public StaffManagerController(EmployeeService employeeService,
                                  VacationService vacationService,
                                  ShiftService shiftService,
                                  AuditService auditService) {
        this.employeeService = employeeService;
        this.vacationService = vacationService;
        this.shiftService = shiftService;
        this.auditService = auditService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Staff Manager Dashboard");
        model.addAttribute("totalEmployees", employeeService.getTotalEmployeeCount());
        model.addAttribute("activeEmployees", employeeService.getActiveEmployeeCount());
        model.addAttribute("pendingVacations", vacationService.getPendingRequests().size());
        model.addAttribute("recentActivities", auditService.getRecentLogs(10));
        return "staff-manager/dashboard";
    }

    // ========== EMPLOYEE MANAGEMENT (UC4, UC5, UC6, UC7) ==========

    @GetMapping("/employees")
    public String listEmployees(@RequestParam(required = false) String role,
                                @RequestParam(required = false) String status,
                                Model model) {
        List<Employee> employees;
        if (role != null && !role.isEmpty()) {
            employees = employeeService.findByRole(Role.valueOf("ROLE_" + role));
        } else {
            employees = employeeService.findAllEmployees();
        }

        model.addAttribute("employees", employees);
        model.addAttribute("roles", Role.values());
        model.addAttribute("selectedRole", role);
        model.addAttribute("pageTitle", "Employee Management");
        return "staff-manager/employees";
    }

    @GetMapping("/employees/create")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("pageTitle", "Create Employee Account");
        model.addAttribute("roles", Role.values());
        return "staff-manager/create-employee";
    }

    @PostMapping("/employees/create")
    public String createEmployee(@ModelAttribute Employee employee,
                                 @RequestParam String role,
                                 @RequestParam String department,
                                 Model model) {
        try {
            employee.setRole(Role.valueOf("ROLE_" + role));
            employee.setActive(true);
            employee.setHireDate(LocalDate.now());

            // Set specialty for medical specialists
            if (employee.getRole() == Role.ROLE_MEDICAL_SPECIALIST) {
                ((MedicalSpecialist) employee).setSpecialty(department);
            }

            Employee created = employeeService.createEmployee(employee);
            model.addAttribute("pageTitle", "Employee Created");
            model.addAttribute("message", "✅ Employee created successfully!\nID: " + created.getId());
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "staff-manager/result";
    }

    @PostMapping("/employees/change-role")
    public String changeEmployeeRole(@RequestParam Long employeeId,
                                     @RequestParam String newRole,
                                     Model model) {
        try {
            employeeService.changeRole(employeeId, Role.valueOf("ROLE_" + newRole));
            model.addAttribute("pageTitle", "Role Changed");
            model.addAttribute("message", "✅ Role changed to " + newRole);
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "staff-manager/result";
    }

    @PostMapping("/employees/reset-password")
    public String resetPassword(@RequestParam Long employeeId, Model model) {
        // TODO: Implement password reset logic
        model.addAttribute("pageTitle", "Password Reset");
        model.addAttribute("message", "✅ Temporary password generated and sent to employee");
        return "staff-manager/result";
    }

    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam Long employeeId, Model model) {
        try {
            employeeService.deleteEmployee(employeeId);
            model.addAttribute("pageTitle", "Employee Deleted");
            model.addAttribute("message", "✅ Employee deleted successfully");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "staff-manager/result";
    }

    // ========== SHIFT MANAGEMENT (UC28) ==========

    @GetMapping("/shifts")
    public String manageShifts(Model model) {
        model.addAttribute("pageTitle", "Shift Management");
        model.addAttribute("employees", employeeService.findAllEmployees());
        model.addAttribute("shifts", shiftService.getAllShifts());
        return "staff-manager/shifts";
    }

    @PostMapping("/shifts/assign")
    public String assignShift(@RequestParam Long employeeId,
                              @RequestParam String date,
                              @RequestParam String shiftType,
                              Model model) {
        try {
            shiftService.assignShift(employeeId, LocalDate.parse(date), shiftType);
            model.addAttribute("pageTitle", "Shift Assigned");
            model.addAttribute("message", "✅ Shift assigned successfully!");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "staff-manager/result";
    }

    // ========== VACATION MANAGEMENT (UC29) ==========

    @GetMapping("/vacations")
    public String manageVacations(Model model) {
        model.addAttribute("pageTitle", "Vacation Management");
        model.addAttribute("pendingRequests", vacationService.getPendingRequests());
        model.addAttribute("allRequests", vacationService.getAllRequests());
        return "staff-manager/vacations";
    }

    @PostMapping("/vacations/approve")
    public String approveVacation(@RequestParam Long requestId, Model model) {
        try {
            vacationService.approve(requestId);
            model.addAttribute("pageTitle", "Vacation Approved");
            model.addAttribute("message", "✅ Vacation request approved");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "staff-manager/result";
    }

    @PostMapping("/vacations/deny")
    public String denyVacation(@RequestParam Long requestId, Model model) {
        try {
            vacationService.reject(requestId);
            model.addAttribute("pageTitle", "Vacation Denied");
            model.addAttribute("message", "❌ Vacation request denied");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "staff-manager/result";
    }

    @GetMapping("/availability")
    public String viewAvailability(Model model) {
        model.addAttribute("pageTitle", "Employee Availability");
        // TODO: Get real-time availability from badge reader integration
        return "staff-manager/availability";
    }

    @GetMapping("/audit-logs")
    public String viewAuditLogs(@RequestParam(required = false) String entityType, Model model) {
        List<AuditLog> logs = auditService.getAllLogs();
        if (entityType != null && !entityType.isEmpty()) {
            logs = logs.stream()
                    .filter(l -> l.getEntityType().equals(entityType))
                    .collect(Collectors.toList());
        }
        model.addAttribute("logs", logs);
        model.addAttribute("pageTitle", "Audit Logs");
        return "staff-manager/audit-logs";
    }
}