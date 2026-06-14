package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.person.employee.Technician;
import com.nvivx.vixhealthsystem.model.resource.Machinery;
import com.nvivx.vixhealthsystem.repository.MachineryRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief Manages the lifecycle and status of medical equipment across all hospital facilities.
 *        Covers UC24 (machine malfunction reporting), FR5.3 (technician visibility), and
 *        FR5.4 (machine malfunction alerts).
 *
 * Annotated {@code @Transactional(readOnly=true)} at the class level; write methods override
 * with {@code @Transactional}.
 *
 * Two paths exist for retrieving faulty machines: the repository query path
 * ({@link #getFaultyMachines()}) and the domain-method path
 * ({@link #getFaultyMachinesForTechnician(Technician)}), which applies the {@link Machinery}
 * model's own {@code isFaulty()} logic and is preferred when the acting technician is known.
 *
 * @see com.nvivx.vixhealthsystem.model.resource.Machinery
 * @see AuditService
 */
@Service
@Transactional(readOnly = true)
public class MachineryService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final MachineryRepository machineryRepository;
    private final AuditService auditService;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with its required collaborators.
     *
     * @param machineryRepository  Persistence layer for {@link Machinery} entities.
     * @param auditService         Records every status change for traceability (NFR02).
     */
    public MachineryService(MachineryRepository machineryRepository,
                            AuditService auditService) {
        this.machineryRepository = machineryRepository;
        this.auditService = auditService;
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /** @brief Returns all machinery registered in the hospital network. */
    public List<Machinery> getAllMachines() {
        return machineryRepository.findAll();
    }

    /**
     * Looks up a machine by primary key, throwing when absent.
     *
     * @param id  Machine primary key.
     * @return    The matching {@link Machinery}; never {@code null}.
     * @throws RuntimeException When no machine with the given ID exists.
     */
    public Machinery getMachineById(Long id) {
        return machineryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found with id: " + id));
    }

    /**
     * Returns all machines that currently have the given {@link MachineStatus}.
     *
     * @param status  Target status to filter on.
     * @return        Non-null list of matching machines.
     */
    public List<Machinery> getMachinesByStatus(MachineStatus status) {
        return machineryRepository.findByStatus(status);
    }

    /** @brief Returns all machines with status {@link MachineStatus#WORKING} (FR5.3). */
    public List<Machinery> getWorkingMachines() {
        return machineryRepository.findByStatus(MachineStatus.WORKING);
    }

    /**
     * Get all faulty machines (UC24, FR5.4 - Machine malfunction alert).
     * Uses DB query directly for efficiency; see also
     * {@link #getFaultyMachinesForTechnician(Technician)} for the domain-method path.
     */
    public List<Machinery> getFaultyMachines() {
        return machineryRepository.findByStatus(MachineStatus.FAULTY);
    }

    /**
     * Returns all faulty machines as identified by the given technician.
     * Uses the Technician domain method, which filters by each machine's
     * own {@link Machinery#isFaulty()} logic.
     * Called from the TechnicianController where the acting technician is known.
     */
    public List<Machinery> getFaultyMachinesForTechnician(Technician technician) {
        List<Machinery> all = getAllMachines();
        return technician.getFaultyMachineList(all);
    }

    /** @brief Returns all machines currently under scheduled maintenance. */
    public List<Machinery> getMachinesUnderMaintenance() {
        return machineryRepository.findByStatus(MachineStatus.UNDER_MAINTENANCE);
    }

    /**
     * Returns all machines assigned to a specific specialised room.
     *
     * @param roomId  Primary key of the specialised room.
     * @return        Non-null list of machines in that room.
     */
    public List<Machinery> getMachinesByRoom(Long roomId) {
        return machineryRepository.findBySpecializedRoomId(roomId);
    }

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Sets a machine's operational status and writes an audit entry (FR5.3).
     *
     * Also calls {@link Machinery#updateStatus()} in case the domain method carries
     * additional side-effects (e.g., clearing maintenance timestamps).
     * Adds an alert note in the audit message when the new status is {@link MachineStatus#FAULTY}.
     *
     * @param machineId  ID of the machine to update.
     * @param newStatus  The target {@link MachineStatus}.
     * @return           The updated and re-persisted {@link Machinery}.
     * @throws RuntimeException When no machine with {@code machineId} exists.
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
     * Marks a machine as {@link MachineStatus#FAULTY} and logs the issue (FR5.4).
     *
     * The fault alert becomes visible on the technician dashboard via {@link #getActiveAlerts()}.
     *
     * @param machineId        ID of the machine that is malfunctioning.
     * @param issueDescription Free-text description of the observed fault.
     * @return                 The updated {@link Machinery} with FAULTY status.
     * @throws RuntimeException When no machine with {@code machineId} exists.
     */
    @Transactional
    public Machinery reportFaultyMachine(Long machineId, String issueDescription) {
        Machinery machine = updateMachineStatus(machineId, MachineStatus.FAULTY);

        auditService.log("REPORT_FAULTY_MACHINE", "Machinery", String.valueOf(machineId),
                "Fault reported: " + issueDescription);

        return machine;
    }

    /**
     * Marks a machine as {@link MachineStatus#WORKING} after repair is complete.
     *
     * @param machineId  ID of the repaired machine.
     * @return           The updated {@link Machinery} with WORKING status.
     * @throws RuntimeException When no machine with {@code machineId} exists.
     */
    @Transactional
    public Machinery repairMachine(Long machineId) {
        Machinery machine = updateMachineStatus(machineId, MachineStatus.WORKING);

        auditService.log("REPAIR_MACHINE", "Machinery", String.valueOf(machineId),
                "Machine repaired and back to WORKING status");

        return machine;
    }

    /**
     * Transitions a machine to {@link MachineStatus#UNDER_MAINTENANCE} status.
     *
     * @param machineId  ID of the machine to schedule for maintenance.
     * @param reason     Reason for the maintenance intervention.
     * @return           The updated {@link Machinery} with UNDER_MAINTENANCE status.
     * @throws RuntimeException When no machine with {@code machineId} exists.
     */
    @Transactional
    public Machinery scheduleMaintenance(Long machineId, String reason) {
        Machinery machine = updateMachineStatus(machineId, MachineStatus.UNDER_MAINTENANCE);

        auditService.log("SCHEDULE_MAINTENANCE", "Machinery", String.valueOf(machineId),
                "Maintenance scheduled: " + reason);

        return machine;
    }

    // =========================================================
    // READ OPERATIONS — COUNTS AND ALERTS
    // =========================================================

    /** @brief Returns the number of machines currently in FAULTY status (for dashboard badges). */
    public long getFaultyMachineCount() {
        return getFaultyMachines().size();
    }

    /** @brief Returns the number of machines currently under scheduled maintenance. */
    public long getMaintenanceMachineCount() {
        return getMachinesUnderMaintenance().size();
    }

    /** @brief Returns the number of machines currently in WORKING status. */
    public long getWorkingMachineCount() {
        return getWorkingMachines().size();
    }

    /** @brief Returns the total number of machines registered in the system. */
    public long getTotalMachineCount() {
        return machineryRepository.count();
    }

    /** @brief Returns {@code true} when at least one machine is currently FAULTY (for alert banners). */
    public boolean hasFaultyMachines() {
        return getFaultyMachineCount() > 0;
    }

    /**
     * Returns alert descriptors for all faulty machines, used to populate the
     *        technician dashboard alert panel (FR5.4).
     *
     * @return Non-null list of {@link AlertInfo} records; empty when no machines are FAULTY.
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

    // =========================================================
    // INNER CLASSES
    // =========================================================

    /**
     * Lightweight view-model for a faulty-machine alert shown on the technician dashboard.
     *
     * Can be promoted to a standalone DTO class if the alert model becomes more complex.
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