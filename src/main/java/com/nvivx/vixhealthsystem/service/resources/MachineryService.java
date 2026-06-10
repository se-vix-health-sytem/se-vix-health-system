package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.resource.Machinery;
import com.nvivx.vixhealthsystem.repository.MachineryRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing medical machinery (UC24, FR5.3, FR5.4)
 * Technicians use this to monitor and maintain equipment
 */
@Service
@Transactional(readOnly = true)
public class MachineryService {

    private final MachineryRepository machineryRepository;
    private final AuditService auditService;

    public MachineryService(MachineryRepository machineryRepository,
                            AuditService auditService) {
        this.machineryRepository = machineryRepository;
        this.auditService = auditService;
    }

    /**
     * Get all machines in the hospital network
     */
    public List<Machinery> getAllMachines() {
        return machineryRepository.findAll();
    }

    /**
     * Get machine by ID
     */
    public Machinery getMachineById(Long id) {
        return machineryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found with id: " + id));
    }

    /**
     * Get all machines with a specific status
     */
    public List<Machinery> getMachinesByStatus(MachineStatus status) {
        return machineryRepository.findByStatus(status);
    }

    /**
     * Get all working machines (FR5.3 - Technicians visibility)
     */
    public List<Machinery> getWorkingMachines() {
        return machineryRepository.findByStatus(MachineStatus.WORKING);
    }

    /**
     * Get all faulty machines (UC24, FR5.4 - Machine malfunction alert)
     */
    public List<Machinery> getFaultyMachines() {
        return machineryRepository.findByStatus(MachineStatus.FAULTY);
    }

    /**
     * Get machines under maintenance
     */
    public List<Machinery> getMachinesUnderMaintenance() {
        return machineryRepository.findByStatus(MachineStatus.UNDER_MAINTENANCE);
    }

    /**
     * Get machines by room (specialized room)
     */
    public List<Machinery> getMachinesByRoom(Long roomId) {
        return machineryRepository.findBySpecializedRoomId(roomId);
    }

    /**
     * Update machine status (FR5.3 - Technicians update machine status)
     */
    @Transactional
    public Machinery updateMachineStatus(Long machineId, MachineStatus newStatus) {
        Machinery machine = getMachineById(machineId);
        MachineStatus oldStatus = machine.getStatus();

        machine.setStatus(newStatus);

        // Also call updateStatus() in case it has additional logic
        machine.updateStatus();

        Machinery saved = machineryRepository.save(machine);

        // Log the status change
        String alertMessage = "";
        if (newStatus == MachineStatus.FAULTY) {
            alertMessage = " ⚠️ ALERT: Machine malfunction detected!";
        }

        auditService.log("UPDATE_MACHINE_STATUS", "Machinery", String.valueOf(machineId),
                "Status changed from " + oldStatus + " to " + newStatus + alertMessage);

        return saved;
    }

    /**
     * Mark a machine as faulty (convenience method for FR5.4)
     * This triggers an alert that technicians can see
     */
    @Transactional
    public Machinery reportFaultyMachine(Long machineId, String issueDescription) {
        Machinery machine = updateMachineStatus(machineId, MachineStatus.FAULTY);

        auditService.log("REPORT_FAULTY_MACHINE", "Machinery", String.valueOf(machineId),
                "Fault reported: " + issueDescription);

        return machine;
    }

    /**
     * Mark a machine as working (after repair)
     */
    @Transactional
    public Machinery repairMachine(Long machineId) {
        Machinery machine = updateMachineStatus(machineId, MachineStatus.WORKING);

        auditService.log("REPAIR_MACHINE", "Machinery", String.valueOf(machineId),
                "Machine repaired and back to WORKING status");

        return machine;
    }

    /**
     * Mark a machine for maintenance
     */
    @Transactional
    public Machinery scheduleMaintenance(Long machineId, String reason) {
        Machinery machine = updateMachineStatus(machineId, MachineStatus.UNDER_MAINTENANCE);

        auditService.log("SCHEDULE_MAINTENANCE", "Machinery", String.valueOf(machineId),
                "Maintenance scheduled: " + reason);

        return machine;
    }

    /**
     * Get count of faulty machines (for dashboard alerts)
     */
    public long getFaultyMachineCount() {
        return getFaultyMachines().size();
    }

    /**
     * Get count of machines under maintenance
     */
    public long getMaintenanceMachineCount() {
        return getMachinesUnderMaintenance().size();
    }

    /**
     * Get count of working machines
     */
    public long getWorkingMachineCount() {
        return getWorkingMachines().size();
    }

    /**
     * Get total machine count
     */
    public long getTotalMachineCount() {
        return machineryRepository.count();
    }

    /**
     * Check if any machine is faulty (for alert banners)
     */
    public boolean hasFaultyMachines() {
        return getFaultyMachineCount() > 0;
    }

    /**
     * Get active alerts (all faulty machines with details)
     * Used for FR5.4 - display alerts on technician dashboard
     */
    public List<AlertInfo> getActiveAlerts() {
        return getFaultyMachines().stream()
                .map(machine -> new AlertInfo(
                        machine.getId(),
                        machine.getName(),
                        machine.getStatus(),
                        machine.getSpecializedRoom() != null ?
                                machine.getSpecializedRoom().getNumber() : "Unknown Location",
                        "Machine reported faulty. Immediate attention required."
                ))
                .collect(Collectors.toList());
    }

    /**
     * Inner class for alert information (can be moved to separate DTO if needed)
     */
    public static class AlertInfo {
        private final Long id;
        private final String name;
        private final MachineStatus status;
        private final String location;
        private final String message;

        public AlertInfo(Long id, String name, MachineStatus status, String location, String message) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.location = location;
            this.message = message;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public MachineStatus getStatus() { return status; }
        public String getLocation() { return location; }
        public String getMessage() { return message; }
    }
}