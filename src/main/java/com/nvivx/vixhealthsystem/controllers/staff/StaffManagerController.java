package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.AuditLog;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.model.person.employee.Technician;
import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import com.nvivx.vixhealthsystem.model.person.employee.StaffManager;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-manager")
public class StaffManagerController {

    private final EmployeeService employeeService;
    private final VacationService vacationService;
    private final ShiftService shiftService;
    private final AuditService auditService;
    private final JsonAppointmentRepository appointmentRepository;

    public StaffManagerController(EmployeeService employeeService,
                                  VacationService vacationService,
                                  ShiftService shiftService,
                                  AuditService auditService,
                                  JsonAppointmentRepository appointmentRepository) {
        this.employeeService = employeeService;
        this.vacationService = vacationService;
        this.shiftService = shiftService;
        this.auditService = auditService;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Staff Manager Dashboard");
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("totalEmployees", employeeService.getTotalEmployeeCount());
        model.addAttribute("activeEmployees", employeeService.getTotalEmployeeCount());
        model.addAttribute("pendingVacations", vacationService.getPendingRequests().size());
        model.addAttribute("recentActivities", auditService.getRecentLogs(10));
        return "staff-manager/dashboard";
    }

    // ========== EMPLOYEE MANAGEMENT ==========

    @GetMapping("/employees")
    public String listEmployees(@RequestParam(required = false) String type, Model model) {
        List<Employee> employees;
        if (type != null && !type.isEmpty()) {
            switch (type.toUpperCase()) {
                case "MEDICAL_SPECIALIST":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof MedicalSpecialist).collect(Collectors.toList());
                    break;
                case "SECRETARY":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof Secretary).collect(Collectors.toList());
                    break;
                case "TECHNICIAN":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof Technician).collect(Collectors.toList());
                    break;
                case "BUYER":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof Buyer).collect(Collectors.toList());
                    break;
                case "STAFF_MANAGER":
                    employees = employeeService.findAllEmployees().stream()
                            .filter(e -> e instanceof StaffManager).collect(Collectors.toList());
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
        model.addAttribute("currentPage", "employees");
        return "staff-manager/employees";
    }

    @GetMapping("/employees/create")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("pageTitle", "Create Employee Account");
        model.addAttribute("currentPage", "createEmployee");
        model.addAttribute("employeeTypes", List.of("MEDICAL_SPECIALIST", "SECRETARY", "TECHNICIAN", "BUYER", "STAFF_MANAGER"));
        model.addAttribute("departments", employeeService.findAllDepartments());
        return "staff-manager/create-employee";
    }

    @PostMapping("/employees/create")
    public String createEmployee(@RequestParam String name,
                                 @RequestParam String surname,
                                 @RequestParam String email,
                                 @RequestParam String employeeType,
                                 @RequestParam(required = false) String specialty,
                                 @RequestParam(required = false) String licenseNumber,
                                 @RequestParam(required = false) Long departmentId,
                                 @RequestParam(required = false) String hireDate,
                                 Model model) {
        try {
            Employee employee;
            switch (employeeType.toUpperCase()) {
                case "MEDICAL_SPECIALIST":
                    MedicalSpecialist ms = new MedicalSpecialist();
                    ms.setSpecialty(specialty);
                    ms.setLicenseNumber(licenseNumber);
                    employee = ms;
                    break;
                case "SECRETARY":
                    employee = new Secretary();
                    break;
                case "TECHNICIAN":
                    employee = new Technician();
                    break;
                case "BUYER":
                    employee = new Buyer();
                    break;
                case "STAFF_MANAGER":
                    employee = new StaffManager();
                    break;
                default:
                    model.addAttribute("pageTitle", "Error");
                    model.addAttribute("message", "❌ Unknown employee type: " + employeeType);
                    model.addAttribute("currentPage", "createEmployee");
                    return "staff-manager/result";
            }
            employee.setName(name);
            employee.setSurname(surname);
            employee.setEmail(email);
            employee.setHireDate(hireDate != null && !hireDate.isEmpty()
                    ? LocalDate.parse(hireDate) : LocalDate.now());
            if (departmentId != null) {
                Department dept = employeeService.findDepartmentById(departmentId);
                employee.setDepartment(dept);
            }
            Employee saved = employeeService.createEmployee(employee);
            model.addAttribute("pageTitle", "Employee Created");
            model.addAttribute("message", "✅ Employee created successfully!\n\n" +
                    "Name: " + saved.getName() + " " + saved.getSurname() + "\n" +
                    "Type: " + employeeType + "\n" +
                    "Email: " + saved.getEmail() + "\n" +
                    "ID: " + saved.getId());
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error creating employee: " + e.getMessage());
        }
        model.addAttribute("currentPage", "createEmployee");
        return "staff-manager/result";
    }

    @PostMapping("/employees/reset-password")
    public String resetEmployeePassword(@RequestParam Long employeeId, Model model) {
        try {
            employeeService.requestEmployeePasswordReset(employeeId);

            model.addAttribute("pageTitle", "Password Reset Requested");
            model.addAttribute("message", "✅ Password reset link generated. Check the console.");

        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException(
                    "Unable to generate password reset link: " + e.getMessage(),
                    e
            );
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
        model.addAttribute("currentPage", "employees");
        return "staff-manager/result";
    }

    // ========== SHIFT MANAGEMENT ==========

    @GetMapping("/shifts")
    public String manageShifts(Model model) {
        model.addAttribute("pageTitle", "Shift Management");
        model.addAttribute("currentPage", "shifts");
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
            LocalDate shiftDate = LocalDate.parse(date);
            // Block assignment if employee has an approved vacation on that date
            boolean onApprovedVacation = vacationService
                    .getApprovedRequestsForEmployee(employeeId.intValue())
                    .stream()
                    .anyMatch(v -> !shiftDate.isBefore(v.getStartDate()) && !shiftDate.isAfter(v.getEndDate()));
            if (onApprovedVacation) {
                model.addAttribute("pageTitle", "Shift Conflict");
                model.addAttribute("message",
                        "❌ Cannot assign shift on " + date + ": employee has an approved vacation on that date.");
                model.addAttribute("currentPage", "shifts");
                return "staff-manager/result";
            }
            shiftService.assignShift(employeeId, shiftDate, shiftType);
            model.addAttribute("pageTitle", "Shift Assigned");
            model.addAttribute("message", "✅ Shift assigned successfully!");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        model.addAttribute("currentPage", "shifts");
        return "staff-manager/result";
    }

    // ========== VACATION MANAGEMENT ==========

    @GetMapping("/vacations")
    public String manageVacations(Model model) {
        model.addAttribute("pageTitle", "Vacation Management");
        model.addAttribute("currentPage", "vacations");
        model.addAttribute("pendingRequests", vacationService.getPendingRequests());
        model.addAttribute("allRequests", vacationService.getAllRequests());
        model.addAttribute("employees", employeeService.findAllEmployees());
        return "staff-manager/vacations";
    }

    @PostMapping("/vacations/add")
    public String addVacationRequest(@RequestParam Long employeeId,
                                     @RequestParam String startDate,
                                     @RequestParam String endDate,
                                     @RequestParam(required = false) String reason,
                                     Model model) {
        try {
            Employee employee = employeeService.findById(employeeId);
            String employeeName = employee.getName() + " " + employee.getSurname();
            vacationService.addVacationRequest(
                    employeeId.intValue(), employeeName,
                    LocalDate.parse(startDate), LocalDate.parse(endDate),
                    reason != null ? reason : "");
            model.addAttribute("pageTitle", "Vacation Request Added");
            model.addAttribute("message", "✅ Vacation request added for " + employeeName);
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        model.addAttribute("currentPage", "vacations");
        return "staff-manager/result";
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
        model.addAttribute("currentPage", "vacations");
        return "staff-manager/result";
    }

    @PostMapping("/vacations/deny")
    public String denyVacation(@RequestParam int requestId, Model model) {
        try {
            vacationService.denyVacation(requestId);
            model.addAttribute("pageTitle", "Vacation Denied");
            model.addAttribute("message", "✅ Vacation request denied");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        model.addAttribute("currentPage", "vacations");
        return "staff-manager/result";
    }

    // ========== AVAILABILITY ==========

    @GetMapping("/availability")
    public String viewAvailability(Model model) {
        List<Employee> employees = employeeService.findAllEmployees();
        List<Shift> allShifts = shiftService.getAllShifts();
        Map<Long, List<Shift>> shiftsByEmployee = allShifts.stream()
                .collect(Collectors.groupingBy(Shift::getEmployeeId));
        // Build a map of employeeId -> approved vacation requests
        Map<Long, List<VacationRequest>> vacationsByEmployee = new java.util.HashMap<>();
        for (Employee emp : employees) {
            List<VacationRequest> approved = vacationService.getApprovedRequestsForEmployee(emp.getId().intValue());
            if (!approved.isEmpty()) {
                vacationsByEmployee.put(emp.getId(), approved);
            }
        }
        // Build a map of medicalSpecialistId -> upcoming appointments
        Map<Long, List<Appointment>> appointmentsBySpecialist = new java.util.HashMap<>();
        try {
            List<Appointment> allAppointments = appointmentRepository.findAll();
            for (Employee emp : employees) {
                if (emp instanceof MedicalSpecialist) {
                    List<Appointment> empAppts = allAppointments.stream()
                        .filter(a -> a.getMedicalSpecialist() != null
                                  && emp.getId().equals(a.getMedicalSpecialist().getId())
                                  && !"CANCELLED".equals(a.getStatus()))
                        .sorted(java.util.Comparator.comparing(
                            a -> a.getDateTime() != null ? a.getDateTime() : java.time.LocalDateTime.MAX))
                        .collect(Collectors.toList());
                    if (!empAppts.isEmpty()) {
                        appointmentsBySpecialist.put(emp.getId(), empAppts);
                    }
                }
            }
        } catch (Exception ignored) {}
        model.addAttribute("pageTitle", "Employee Availability");
        model.addAttribute("currentPage", "availability");
        model.addAttribute("employees", employees);
        model.addAttribute("shiftsByEmployee", shiftsByEmployee);
        model.addAttribute("vacationsByEmployee", vacationsByEmployee);
        model.addAttribute("appointmentsBySpecialist", appointmentsBySpecialist);
        return "staff-manager/availability";
    }

    // ========== AUDIT LOGS ==========

    @GetMapping("/audit-logs")
    public String viewAuditLogs(@RequestParam(required = false) String entityType, Model model) {
        List<AuditLog> allLogs = auditService.getAllLogs().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
        List<String> entityTypes = allLogs.stream()
                .map(AuditLog::getEntityType)
                .filter(et -> et != null && !et.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        List<AuditLog> logs = (entityType != null && !entityType.isEmpty())
                ? auditService.getLogsByEntityType(entityType).stream()
                    .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                    .collect(Collectors.toList())
                : allLogs;
        model.addAttribute("logs", logs);
        model.addAttribute("entityTypes", entityTypes);
        model.addAttribute("selectedEntityType", entityType);
        model.addAttribute("pageTitle", "Audit Logs");
        model.addAttribute("currentPage", "auditLogs");
        return "staff-manager/audit-logs";
    }
}
