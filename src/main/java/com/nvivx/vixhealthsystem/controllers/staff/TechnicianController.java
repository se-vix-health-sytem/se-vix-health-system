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

/**
 * @brief Controller for Technician staff members — base URL {@code /technician}.
 *
 * Technicians maintain the hospital's machinery fleet.  This controller covers
 * viewing all machines, filtering by faulty or maintenance status, updating
 * machine status, logging repairs, viewing active alerts, and the technician's
 * own shift and profile pages.
 *
 * Only accessible to users with {@code ROLE_TECHNICIAN}.
 *
 * Use cases covered: UC26 (machinery maintenance), UC27 (status updates),
 * UC28 (alerts), UC30 (profile/shifts).
 *
 * @see MachineryService
 * @see ShiftService
 * @see VacationService
 */
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

    // =========================================================
    // DASHBOARD
    // =========================================================

    /**
     * GET /technician/dashboard — render the technician's overview dashboard.
     *
     * @param model  Receives {@code totalMachines}, {@code faultyCount},
     *               {@code maintenanceCount}, {@code alertCount}, and {@code activeAlerts}.
     * @return       Thymeleaf template {@code technician/dashboard}.
     */
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

    // =========================================================
    // MACHINE MANAGEMENT
    // =========================================================

    /**
     * GET /technician/machines — list all machines in the hospital fleet.
     *
     * @param model  Receives {@code machines} and {@code isFaultyView=false}.
     * @return       Thymeleaf template {@code technician/machines}.
     */
    @GetMapping("/machines")
    public String viewAllMachines(Model model) {
        var machines = machineryService.getAllMachines();
        model.addAttribute("pageTitle", "All Machines");
        model.addAttribute("currentPage", "machines");
        model.addAttribute("machines", machines);
        model.addAttribute("isFaultyView", false);
        return "technician/machines";
    }

    /**
     * GET /technician/machines/faulty — list only machines with FAULTY status.
     *
     * Delegates to the domain method via {@link MachineryService#getFaultyMachinesForTechnician},
     * which applies technician-specific filtering logic.  The technician domain
     * object is resolved from the session.
     *
     * @param session  HTTP session carrying the {@code "user"} Employee attribute.
     * @param model    Receives {@code machines} and {@code isFaultyView=true}.
     * @return         Thymeleaf template {@code technician/machines}.
     */
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

    /**
     * GET /technician/machines/maintenance — list machines currently under maintenance.
     *
     * @param model  Receives {@code machines}.
     * @return       Thymeleaf template {@code technician/machines}.
     */
    @GetMapping("/machines/maintenance")
    public String viewMaintenanceMachines(Model model) {
        var maintenanceMachines = machineryService.getMachinesUnderMaintenance();
        model.addAttribute("pageTitle", "Machines Under Maintenance");
        model.addAttribute("currentPage", "machines");
        model.addAttribute("machines", maintenanceMachines);
        return "technician/machines";
    }

    /**
     * POST /technician/machines/update-status — change the operational status of a machine.
     *
     * @param machineId  Database ID of the machine to update.
     * @param status     String representation of a {@link MachineStatus} enum value
     *                   (e.g., {@code "WORKING"}, {@code "FAULTY"}, {@code "MAINTENANCE"}).
     * @param model      Receives a success or error {@code message} attribute.
     * @return           Thymeleaf template {@code technician/result}.
     */
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

    // =========================================================
    // ALERTS
    // =========================================================

    /**
     * GET /technician/alerts — display all active machine fault alerts.
     *
     * @param model  Receives {@code alerts} and {@code alertCount}.
     * @return       Thymeleaf template {@code technician/alerts}.
     */
    @GetMapping("/alerts")
    public String viewAlerts(Model model) {
        var activeAlerts = machineryService.getActiveAlerts();
        model.addAttribute("pageTitle", "Machine Alerts");
        model.addAttribute("currentPage", "alerts");
        model.addAttribute("alerts", activeAlerts);
        model.addAttribute("alertCount", activeAlerts.size());
        return "technician/alerts";
    }

    // =========================================================
    // PROFILE
    // =========================================================

    /**
     * GET /technician/my-shifts — display the technician's assigned shifts and approved vacations.
     *
     * @param session  HTTP session carrying the {@code "user"} Employee attribute.
     * @param model    Receives {@code shifts}, {@code vacations}, and {@code dashboardLink}.
     * @return         Thymeleaf template {@code employee/my-shifts}, or
     *                 {@code redirect:/login} when no session user is found.
     */
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

    /**
     * GET /technician/profile — display the technician's personal profile page.
     *
     * @param session  HTTP session carrying the {@code "user"} Employee attribute.
     * @param model    Receives {@code employee}, {@code roleLabel}, and {@code dashboardLink}.
     * @return         Thymeleaf template {@code employee/profile}, or
     *                 {@code redirect:/login} when no session user is found.
     */
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

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Resolve and reload the authenticated Technician from the HTTP session.
     *
     * The session {@code "user"} attribute is used because {@link MachineryService}
     * methods require the full Technician domain object.  Falls back to a transient
     * {@code new Technician()} when the session carries no Technician.
     *
     * @param session  HTTP session carrying the {@code "user"} attribute.
     * @return         A fully initialised {@link Technician} from the database, or a
     *                 transient instance as a fallback.
     */
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

    // =========================================================
    // MACHINE HISTORY & DETAILS
    // =========================================================

    /**
     * GET /technician/machines/maintenance-history — show the full maintenance history for all machines.
     *
     * @param model  Receives {@code machines} (all machines with their historical status records).
     * @return       Thymeleaf template {@code technician/maintenance-history}.
     */
    @GetMapping("/machines/maintenance-history")
    public String viewMaintenanceHistory(Model model) {
        var allMachines = machineryService.getAllMachines();
        model.addAttribute("pageTitle", "Machine History");
        model.addAttribute("machines", allMachines);
        return "technician/maintenance-history";
    }

    /**
     * GET /technician/machines/{machineId} — display the details of a single machine.
     *
     * @param machineId  Database ID of the machine to display.
     * @param model      Receives {@code machine} attribute.
     * @return           Thymeleaf template {@code technician/machine-details}, or
     *                   {@code technician/result} with a not-found message when the ID
     *                   does not exist.
     */
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

    /**
     * POST /technician/repair/{machineId} — mark a machine as repaired and set it to WORKING.
     *
     * @param machineId  Database ID of the machine that has been repaired.
     * @param notes      Optional free-text repair notes; ignored when blank.
     * @param model      Receives a success or error {@code message} attribute.
     * @return           Thymeleaf template {@code technician/result}.
     */
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