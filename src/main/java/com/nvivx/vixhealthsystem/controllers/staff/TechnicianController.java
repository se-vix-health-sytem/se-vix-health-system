package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.Technician;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.resources.MachineryService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    private final MachineryService machineryService;
    private final EmployeeService employeeService;
    private final ShiftService shiftService;
    private final VacationService vacationService;

    public TechnicianController(MachineryService machineryService,
                                EmployeeService employeeService,
                                ShiftService shiftService,
                                VacationService vacationService) {
        this.machineryService = machineryService;
        this.employeeService = employeeService;
        this.shiftService = shiftService;
        this.vacationService = vacationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Technician Dashboard");
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("totalMachines", machineryService.getTotalMachineCount());
        model.addAttribute("faultyCount", machineryService.getFaultyMachineCount());
        model.addAttribute("maintenanceCount", machineryService.getMaintenanceMachineCount());
        model.addAttribute("alertCount", machineryService.getFaultyMachineCount());
        model.addAttribute("activeAlerts", machineryService.getActiveAlerts());
        return "technician/dashboard";
    }

    @GetMapping("/machines")
    public String viewAllMachines(Model model) {
        var machines = machineryService.getAllMachines();
        model.addAttribute("pageTitle", "All Machines");
        model.addAttribute("currentPage", "machines");
        model.addAttribute("machines", machines);
        model.addAttribute("isFaultyView", false);
        return "technician/machines";
    }

    @GetMapping("/machines/faulty")
    public String viewFaultyMachines(HttpSession session, Model model) {
        // Domain: technician filters faulty machines via model method
        var faultyMachines = machineryService.getFaultyMachinesForTechnician(
                getTechnicianFromSession(session));
        model.addAttribute("pageTitle", "Faulty Machines");
        model.addAttribute("currentPage", "faultyMachines");
        model.addAttribute("machines", faultyMachines);
        model.addAttribute("isFaultyView", true);
        return "technician/machines";
    }

    @GetMapping("/machines/maintenance")
    public String viewMaintenanceMachines(Model model) {
        var maintenanceMachines = machineryService.getMachinesUnderMaintenance();
        model.addAttribute("pageTitle", "Machines Under Maintenance");
        model.addAttribute("currentPage", "machines");
        model.addAttribute("machines", maintenanceMachines);
        return "technician/machines";
    }

    @PostMapping("/machines/update-status")
    public String updateMachineStatus(@RequestParam Long machineId,
                                      @RequestParam String status,
                                      Model model) {
        try {
            MachineStatus newStatus = MachineStatus.valueOf(status);
            var machine = machineryService.updateMachineStatus(machineId, newStatus);

            String statusEmoji = newStatus == MachineStatus.WORKING ? "🟢" :
                    newStatus == MachineStatus.FAULTY ? "🔴" : "🟡";

            String alertMsg = newStatus == MachineStatus.FAULTY ?
                    "\n\n⚠️ ALERT: Maintenance team has been notified!" : "";

            model.addAttribute("pageTitle", "Machine Status Updated");
            model.addAttribute("message",
                    "✅ Machine Status Updated!\n\n" +
                            "Machine: " + machine.getName() + " (ID: " + machineId + ")\n" +
                            "New Status: " + statusEmoji + " " + newStatus + alertMsg);
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "technician/result";
    }

    @GetMapping("/alerts")
    public String viewAlerts(Model model) {
        var activeAlerts = machineryService.getActiveAlerts();
        model.addAttribute("pageTitle", "Machine Alerts");
        model.addAttribute("currentPage", "alerts");
        model.addAttribute("alerts", activeAlerts);
        model.addAttribute("alertCount", activeAlerts.size());
        return "technician/alerts";
    }

    @GetMapping("/my-shifts")
    public String viewMyShifts(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        List<VacationRequest> vacations = vacationService.getApprovedRequestsForEmployee(user.getId().intValue());
        model.addAttribute("shifts", shiftService.getShiftsForEmployee(user.getId()));
        model.addAttribute("vacations", vacations);
        model.addAttribute("dashboardLink", "/technician/dashboard");
        model.addAttribute("pageTitle", "My Shifts");
        model.addAttribute("currentPage", "myShifts");
        return "employee/my-shifts";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Employee sessionUser = (Employee) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        try {
            Employee fresh = employeeService.findById(sessionUser.getId());
            model.addAttribute("employee", fresh);
            model.addAttribute("pageTitle", "My Profile");
            model.addAttribute("currentPage", "profile");
            model.addAttribute("roleLabel", "Technician");
            model.addAttribute("dashboardLink", "/technician/dashboard");
            model.addAttribute("isSpecialist", false);
        } catch (Exception e) {
            model.addAttribute("employee", sessionUser);
            model.addAttribute("currentPage", "profile");
        }
        return "employee/profile";
    }

    // ========== HELPERS ==========

    private Technician getTechnicianFromSession(HttpSession session) {
        Employee user = (Employee) session.getAttribute("user");
        if (user instanceof Technician t) {
            Employee fresh = employeeService.findById(t.getId());
            if (fresh instanceof Technician tech) {
                return tech;
            }
        }
        return new Technician();
    }

    @GetMapping("/machines/maintenance-history")
    public String viewMaintenanceHistory(Model model) {
        var allMachines = machineryService.getAllMachines();
        model.addAttribute("pageTitle", "Machine History");
        model.addAttribute("machines", allMachines);
        return "technician/maintenance-history";
    }

    @GetMapping("/machines/{machineId}")
    public String viewMachineDetails(@PathVariable Long machineId, Model model) {
        try {
            var machine = machineryService.getMachineById(machineId);
            model.addAttribute("pageTitle", "Machine Details - " + machine.getName());
            model.addAttribute("currentPage", "machines");
            model.addAttribute("machine", machine);
            return "technician/machine-details";
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Machine Not Found");
            model.addAttribute("message", "Machine with ID " + machineId + " not found");
            return "technician/result";
        }
    }

    @PostMapping("/repair/{machineId}")
    public String repairMachine(@PathVariable Long machineId,
                                @RequestParam(required = false) String notes,
                                Model model) {
        try {
            var machine = machineryService.repairMachine(machineId);
            model.addAttribute("pageTitle", "Machine Repaired");
            model.addAttribute("message",
                    "✅ Machine repaired successfully!\n\n" +
                            "Machine: " + machine.getName() + "\n" +
                            "Status: 🟢 WORKING" +
                            (notes != null && !notes.isBlank() ? "\n\nNotes: " + notes : ""));
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "technician/result";
    }
}