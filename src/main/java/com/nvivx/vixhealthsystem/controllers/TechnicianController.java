package com.nvivx.vixhealthsystem.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/technician")

public class TechnicianController {
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Technician Dashboard");
        return "technician/dashboard";
    }

    @GetMapping("/machines")
    public String viewAllMachines(Model model) {
        model.addAttribute("pageTitle", "All Machines");
        model.addAttribute("message", "Machine list will be displayed here");
        return "technician/machines";
    }

    @GetMapping("/machines/faulty")
    public String viewFaultyMachines(Model model) {
        model.addAttribute("pageTitle", "Faulty Machines");
        model.addAttribute("message", "Faulty machines will be displayed here");
        return "technician/machines";
    }

    @PostMapping("/machines/update-status")
    public String updateMachineStatus(@RequestParam Long machineId,
                                      @RequestParam String status,
                                      Model model) {
        String statusEmoji = status.equals("WORKING") ? "🟢" :
                status.equals("FAULTY") ? "🔴" : "🟡";

        model.addAttribute("pageTitle", "Machine Status Updated");
        model.addAttribute("message",
                "✅ Machine Status Updated!\n\n" +
                        "Machine ID: " + machineId + "\n" +
                        "New Status: " + statusEmoji + " " + status + "\n\n" +
                        "Frontend → Backend communication successful!");
        return "technician/result";
    }


    // FR5.4 - View Machine Alerts
    @GetMapping("/alerts")
    public String viewAlerts(Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: Will call machineryService.getActiveAlerts()
        //         Machinery has status field, alerts are generated when status changes to FAULTY
        model.addAttribute("pageTitle", "Machine Alerts");
        model.addAttribute("message",
                "BACKEND RECEIVED: Viewing active machine malfunction alerts");
        return "technician/result";
    }

    // UC24 - View Maintenance History
    @GetMapping("/machines/maintenance-history")
    public String viewMaintenanceHistory(Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: Will call machineryService.getMaintenanceHistory(machineId)
        //         Machinery will have List<MaintenanceRecord> tracking all repairs
        model.addAttribute("pageTitle", "Maintenance History");
        model.addAttribute("message",
                "BACKEND RECEIVED: Viewing machine maintenance history");
        return "technician/result";
    }

    // UC6 - Credential Recovery (Technicians can also do this)
    @PostMapping("/recover-credentials")
    public String recoverCredentials(@RequestParam Long employeeId, Model model) {
        model.addAttribute("pageTitle", "Credentials Recovery Initiated");
        model.addAttribute("message",
                "✅ Credentials Recovery!\n\n" +
                        "Employee ID: " + employeeId + "\n" +
                        "A temporary password has been generated.\n" +
                        "The employee will be forced to change it on next login.\n\n" +
                        "Backend received the request successfully!");
        return "technician/result";
    }

    // FR5.3 - View Machine Details
    @GetMapping("/machines/{machineId}")
    public String viewMachineDetails(@PathVariable Long machineId, Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: Will call machineryService.getMachineDetails(machineId)
        //         Returns Machinery entity with all details, status, location
        model.addAttribute("pageTitle", "Machine Details");
        model.addAttribute("message",
                "BACKEND RECEIVED: Viewing details for machine #" + machineId);
        return "technician/result";
    }
}
