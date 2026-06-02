package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.resource.Machinery;

import java.util.ArrayList;

public class SpecializedRoom extends Room{
    private String type;
    private ArrayList<Machinery> machineries;

    public SpecializedRoom(String number, String type) {
        super(number);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected ArrayList<Machinery> getMachineries() {
        return machineries;
    }

    protected ArrayList<Machinery> getFaultyMachines() {
        ArrayList<Machinery> out = new ArrayList<>();
        for (int i = 0; i < machineries.size(); i++) {
            if (machineries.get(i).isFaulty()) {
                out.add(machineries.get(i));
            }
        }
        return out;
    }
}
