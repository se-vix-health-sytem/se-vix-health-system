package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.service.resources.MachineryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    private final MachineryService machineryService;

    public TechnicianController(MachineryService machineryService) {
        this.machineryService = machineryService;
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
    public String viewFaultyMachines(Model model) {
        var faultyMachines = machineryService.getFaultyMachines();
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