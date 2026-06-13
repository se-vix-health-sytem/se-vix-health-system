package com.nvivx.vixhealthsystem.model.resource;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MachineryTest {
    private Machinery machinery;
    private SpecializedRoom room;

    @BeforeEach
    void setUp() {
        machinery = new Machinery("MRI Scanner");
        machinery.setId(1L);

        room = new SpecializedRoom("S101", "Radiology");
        machinery.setSpecializedRoom(room);
    }

    @Test
    void constructor_ShouldInitializeWithNameAndCallUpdateStatus() {
        Machinery newMachine = new Machinery("X-Ray Machine");
        assertEquals("X-Ray Machine", newMachine.getName());
        assertNotNull(newMachine.getStatus());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, machinery.getId());
        assertEquals("MRI Scanner", machinery.getName());
        assertEquals(room, machinery.getSpecializedRoom());
    }

    @Test
    void setStatus_ShouldUpdateStatus() {
        machinery.setStatus(MachineStatus.WORKING);
        assertEquals(MachineStatus.WORKING, machinery.getStatus());

        machinery.setStatus(MachineStatus.FAULTY);
        assertEquals(MachineStatus.FAULTY, machinery.getStatus());
    }

    @Test
    void updateStatus_ShouldSetRandomStatus() {
        // Run multiple times to verify it sets different statuses
        boolean sawWorking = false;
        boolean sawFaulty = false;
        boolean sawMaintenance = false;

        for (int i = 0; i < 100; i++) {
            machinery.updateStatus();
            MachineStatus status = machinery.getStatus();
            if (status == MachineStatus.WORKING) sawWorking = true;
            if (status == MachineStatus.FAULTY) sawFaulty = true;
            if (status == MachineStatus.UNDER_MAINTENANCE) sawMaintenance = true;
        }

        assertTrue(sawWorking);
        assertTrue(sawFaulty);
        assertTrue(sawMaintenance);
    }

    @Test
    void isFaulty_ShouldReturnTrueWhenStatusIsFaulty() {
        machinery.setStatus(MachineStatus.FAULTY);
        assertTrue(machinery.isFaulty());
    }

    @Test
    void isFaulty_ShouldReturnFalseWhenStatusIsNotFaulty() {
        machinery.setStatus(MachineStatus.WORKING);
        assertFalse(machinery.isFaulty());

        machinery.setStatus(MachineStatus.UNDER_MAINTENANCE);
        assertFalse(machinery.isFaulty());
    }
}