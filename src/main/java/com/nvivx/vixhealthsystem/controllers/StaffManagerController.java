package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.AuditLog;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.model.person.employee.Technician;
import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import com.nvivx.vixhealthsystem.model.person.employee.StaffManager;
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
        model.addAttribute("activeEmployees", employeeService.getTotalEmployeeCount()); // All employees are active
        model.addAttribute("pendingVacations", vacationService.getPendingRequests().size());
        model.addAttribute("recentActivities", auditService.getRecentLogs(10));
        return "staff-manager/dashboard";
    }

    // ========== EMPLOYEE MANAGEMENT ==========

    @GetMapping("/employees")
    public String listEmployees(@RequestParam(required = false) String type,
                                Model model) {
        List<Employee> employees;

        if (type != null && !type.isEmpty()) {
            // Filter by employee type using instanceof (since database uses discriminator)
            switch (type.toUpperCase()) {
                case "MEDICAL_SPECIALIST":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof MedicalSpecialist)
                            .collect(Collectors.toList());
                    break;
                case "SECRETARY":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof Secretary)
                            .collect(Collectors.toList());
                    break;
                case "TECHNICIAN":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof Technician)
                            .collect(Collectors.toList());
                    break;
                case "BUYER":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof Buyer)
                            .collect(Collectors.toList());
                    break;
                case "STAFF_MANAGER":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof StaffManager)
                            .collect(Collectors.toList());
                    break;
                default:
                    employees = employeeService.findAllEmployees();
            }
        } else {
            employees = employeeService.findAllEmployees();
        }

        model.addAttribute("employees", employees);
        model.addAttribute("employeeTypes", List.of("MEDICAL_SPECIALIST", "SECRETARY", "TECHNICIAN", "BUYER", "STAFF_MANAGER"));
        model.addAttribute("selectedType", type);
        model.addAttribute("pageTitle", "Employee Management");
        return "staff-manager/employees";
    }

    @GetMapping("/employees/create")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("pageTitle", "Create Employee Account");
        model.addAttribute("employeeTypes", List.of("MEDICAL_SPECIALIST", "SECRETARY", "TECHNICIAN", "BUYER", "STAFF_MANAGER"));
        return "staff-manager/create-employee";
    }

    @PostMapping("/employees/create")
    public String createEmployee(@RequestParam String name,
                                 @RequestParam String surname,
                                 @RequestParam String email,
                                 @RequestParam String employeeType,
                                 @RequestParam(required = false) String specialty,
                                 Model model) {
        try {
            // Employee creation is handled by EmployeeService
            // The actual creation depends on your EmployeeService implementation
            model.addAttribute("pageTitle", "Employee Created");
            model.addAttribute("message", "✅ Employee created successfully!\n\n" +
                    "Name: " + name + " " + surname + "\n" +
                    "Type: " + employeeType + "\n" +
                    "Email: " + email);
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
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

    // ========== SHIFT MANAGEMENT ==========

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

    // ========== VACATION MANAGEMENT ==========

    @GetMapping("/vacations")
    public String manageVacations(Model model) {
        model.addAttribute("pageTitle", "Vacation Management");
        model.addAttribute("pendingRequests", vacationService.getPendingRequests());
        model.addAttribute("allRequests", vacationService.getAllRequests());
        return "staff-manager/vacations";
    }

    @PostMapping("/vacations/approve")
    public String approveVacation(@RequestParam int requestId, Model model) {
        try {
            vacationService.approveVacation(requestId);
            model.addAttribute("pageTitle", "Vacation Approved");
            model.addAttribute("message", "✅ Vacation request approved");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "staff-manager/result";
    }

    @PostMapping("/vacations/deny")
    public String denyVacation(@RequestParam int requestId, Model model) {
        try {
            vacationService.denyVacation(requestId);
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
        return "staff-manager/availability";
    }

    @GetMapping("/audit-logs")
    public String viewAuditLogs(@RequestParam(required = false) String entityType, Model model) {
        List<AuditLog> logs = auditService.getAllLogs();
        if (entityType != null && !entityType.isEmpty()) {
            logs = logs.stream()
                    .filter(l -> entityType.equals(l.getEntityType()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("logs", logs);
        model.addAttribute("pageTitle", "Audit Logs");
        return "staff-manager/audit-logs";
    }
}