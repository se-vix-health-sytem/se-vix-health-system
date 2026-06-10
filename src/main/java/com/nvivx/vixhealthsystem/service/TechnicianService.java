// Update TechnicianService.java - fix machine counting
package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.mock.MockDatabase;
import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.resource.Machinery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicianService {

    private final MockDatabase mockDatabase;

    public TechnicianService(MockDatabase mockDatabase) {
        this.mockDatabase = mockDatabase;
    }

    public List<Machinery> getAllMachines() {
        return mockDatabase.findAllMachinery();
    }

    public long getTotalMachineCount() {
        return mockDatabase.findAllMachinery().size();
    }

    public long getFaultyMachineCount() {
        return mockDatabase.findAllMachinery().stream()
                .filter(m -> m.getStatus() == MachineStatus.FAULTY)
                .count();
    }

    public long getUnderMaintenanceCount() {
        return mockDatabase.findAllMachinery().stream()
                .filter(m -> m.getStatus() == MachineStatus.UNDER_MAINTENANCE)
                .count();
    }

    public long getWorkingMachineCount() {
        return mockDatabase.findAllMachinery().stream()
                .filter(m -> m.getStatus() == MachineStatus.WORKING)
                .count();
    }

    public List<Machinery> getFaultyMachines() {
        return mockDatabase.findFaultyMachineries();
    }

    public void updateMachineStatus(int machineId, String newStatus) {
        Machinery machine = mockDatabase.findMachineryById(machineId);
        if (machine == null) {
            throw new IllegalArgumentException("Machine not found with id: " + machineId);
        }

        MachineStatus status;
        switch (newStatus.toUpperCase()) {
            case "WORKING":
                status = MachineStatus.WORKING;
                break;
            case "FAULTY":
                status = MachineStatus.FAULTY;
                break;
            case "UNDER_MAINTENANCE":
                status = MachineStatus.UNDER_MAINTENANCE;
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        machine.setStatus(status);
        mockDatabase.saveMachinery(machine);
    }

    public Machinery getMachineById(int machineId) {
        return mockDatabase.findMachineryById(machineId);
    }
}