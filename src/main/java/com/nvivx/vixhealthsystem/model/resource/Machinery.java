package com.nvivx.vixhealthsystem.model.resource;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;

public class Machinery {
    private String name;
    private MachineStatus status;

    public Machinery(String name) {
        this.name = name;
        updateStatus();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateStatus() {
        //TO DO
    }

    public MachineStatus getStatus() {
        return status;
    }

    public boolean isFaulty() {
        return status == MachineStatus.FAULTY;
    }
}
