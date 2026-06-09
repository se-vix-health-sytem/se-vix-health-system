package com.nvivx.vixhealthsystem.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff-manager")

public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Staff Manager Dashboard");
        return "staff-manager/dashboard";
    }

    @GetMapping("/employees/create")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("pageTitle", "Create Employee Account");
        return "staff-manager/create-employee";
    }

    @PostMapping("/employees/create")
    public String createEmployee(@RequestParam String name,
                                 @RequestParam String surname,
                                 @RequestParam String email,
                                 @RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String role,
                                 Model model) {
        model.addAttribute("pageTitle", "Employee Created Successfully");
        model.addAttribute("message",
                "✅ Employee Account Created!\n\n" +
                        "Name: " + name + " " + surname + "\n" +
                        "Email: " + email + "\n" +
                        "Username: " + username + "\n" +
                        "Role: " + role + "\n\n" +
                        "This data was sent from the form to the backend.\n" +
                        "In production, this creates the Employee in the database.");
        return "staff-manager/result";
    }

    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam Long employeeId, Model model) {
        model.addAttribute("pageTitle", "Employee Deleted");
        model.addAttribute("message",
                "✅ Employee Deleted!\n\n" +
                        "Employee ID: " + employeeId + " has been removed from the system.\n\n" +
                        "Backend processed the deletion request.");
        return "staff-manager/result";
    }

    @PostMapping("/employees/recover-credentials")
    public String recoverCredentials(@RequestParam Long employeeId, Model model) {
        model.addAttribute("pageTitle", "Credentials Recovery Initiated");
        model.addAttribute("message",
                "✅ Credentials Recovery!\n\n" +
                        "Employee ID: " + employeeId + "\n" +
                        "Temporary password generated.\n" +
                        "Employee must change password on next login.\n\n" +
                        "Backend processed the recovery request!");
        return "staff-manager/result";
    }

    // UC7 - View All Employees
    @GetMapping("/employees")
    public String listAllEmployees(Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: Will call adminService.getAllEmployees()
        //         Returns List<Employee> filtered by department if specified
        model.addAttribute("pageTitle", "All Employees");
        model.addAttribute("message",
                "BACKEND RECEIVED: Viewing complete employee list");
        return "staff-manager/result";
    }

    // UC7 - Change Employee Role
    @PostMapping("/employees/change-role")
    public String changeEmployeeRole(@RequestParam Long employeeId,
                                     @RequestParam String newRole,
                                     Model model) {
        model.addAttribute("pageTitle", "Role Changed Successfully");
        model.addAttribute("message",
                "✅ Employee Role Updated!\n\n" +
                        "Employee ID: " + employeeId + "\n" +
                        "New Role: " + newRole + "\n\n" +
                        "Backend received and processed the role change.");
        return "staff-manager/result";
    }

    // UC28 - Manage Shifts Page
    @GetMapping("/shifts")
    public String manageShifts(Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: Will call staffScheduleService.getWeeklySchedule()
        //         Shows calendar with all employees' shifts for the week
        model.addAttribute("pageTitle", "Shift Management");
        model.addAttribute("message",
                "BACKEND RECEIVED: Opening shift management dashboard");
        return "staff-manager/result";
    }

    // UC28 - Assign Shift
    @PostMapping("/shifts/assign")
    public String assignShift(@RequestParam Long employeeId,
                              @RequestParam String date,
                              @RequestParam String shiftType,
                              Model model) {
        String shiftEmoji = shiftType.equals("MORNING") ? "🌅" :
                shiftType.equals("AFTERNOON") ? "☀️" : "🌙";

        model.addAttribute("pageTitle", "Shift Assigned Successfully");
        model.addAttribute("message",
                "✅ Shift Assigned!\n\n" +
                        "Employee ID: " + employeeId + "\n" +
                        "Date: " + date + "\n" +
                        "Shift: " + shiftEmoji + " " + shiftType + "\n\n" +
                        "Backend processed the shift assignment!");
        return "staff-manager/result";
    }

    // UC29 - Vacation Management Page
    @GetMapping("/vacations")
    public String manageVacations(Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: Will call staffScheduleService.getPendingVacationRequests()
        //         Shows all vacation requests with approve/reject buttons
        model.addAttribute("pageTitle", "Vacation Requests");
        model.addAttribute("message",
                "BACKEND RECEIVED: Viewing pending vacation requests");
        return "staff-manager/result";
    }

    // UC29 - Approve/Reject Vacation
    @PostMapping("/vacations/approve")
    public String approveVacation(@RequestParam Long requestId,
                                  @RequestParam boolean approved,
                                  Model model) {
        String statusEmoji = approved ? "✅" : "❌";
        String status = approved ? "APPROVED" : "REJECTED";

        model.addAttribute("pageTitle", "Vacation Request " + status);
        model.addAttribute("message",
                statusEmoji + " Vacation Request " + status + "!\n\n" +
                        "Request ID: " + requestId + "\n" +
                        "Status: " + status + "\n\n" +
                        "Backend processed the vacation decision!");
        return "staff-manager/result";
    }

    // FR6.4 - Employee Availability Dashboard
    @GetMapping("/availability")
    public String viewAvailability(Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: Will call staffScheduleService.getEmployeeAvailability()
        //         Integrates with badge reader system (FR6.6) for real-time status
        model.addAttribute("pageTitle", "Employee Availability");
        model.addAttribute("message",
                "BACKEND RECEIVED: Viewing real-time employee availability dashboard");
        return "staff-manager/result";
    }
}
