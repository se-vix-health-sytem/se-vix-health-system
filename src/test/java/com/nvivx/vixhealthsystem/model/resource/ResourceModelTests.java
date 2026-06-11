package com.nvivx.vixhealthsystem.model.resource;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ResourceModelTests {

    // ========== Resource Tests ==========
    @Test
    void testResource() {
        BigDecimal price = BigDecimal.valueOf(15.99);
        Resource resource = new Resource("Bandages", "Sterile bandages", price);

        assertEquals("Bandages", resource.getName());
        assertEquals("Sterile bandages", resource.getDescription());
        assertEquals(price, resource.getPrice());

        resource.setId(100L);
        resource.setName("Gauze");
        resource.setPrice(BigDecimal.valueOf(5.50));

        assertEquals(100L, resource.getId());
        assertEquals("Gauze", resource.getName());
        assertEquals(BigDecimal.valueOf(5.50), resource.getPrice());
    }

    @Test
    void testResourceEquality() {
        Resource r1 = new Resource();
        Resource r2 = new Resource();

        r1.setId(1L);
        r2.setId(1L);
        assertEquals(r1, r2);

        r2.setId(2L);
        assertNotEquals(r1, r2);

        assertNotEquals(r1, "string");
        assertEquals(r1, r1);

        r1.setId(null);
        assertEquals(0, r1.hashCode());
    }

    // ========== Machinery Tests ==========
    @Test
    void testMachinery() {
        Machinery machine = new Machinery("MRI Scanner");
        assertEquals("MRI Scanner", machine.getName());
        assertEquals(MachineStatus.WORKING, machine.getStatus());

        machine.setStatus(MachineStatus.FAULTY);
        assertTrue(machine.isFaulty());

        machine.setStatus(MachineStatus.UNDER_MAINTENANCE);
        assertFalse(machine.isFaulty());

        SpecializedRoom room = new SpecializedRoom("R101", "Radiology");
        machine.setSpecializedRoom(room);
        assertEquals(room, machine.getSpecializedRoom());

        machine.setId(5L);
        assertEquals(5L, machine.getId());

        machine.updateStatus(); // Should not throw
    }

    // ========== Storage Tests ==========
    @Test
    void testStorage() throws Exception {
        Storage storage = new Storage();
        MedicalFacility facility = new MedicalFacility();
        facility.setId(1L);
        storage.setMedicalFacility(facility);
        assertEquals(facility, storage.getMedicalFacility());

        Resource r1 = new Resource("Syringes", "Disposable", BigDecimal.valueOf(0.50));
        r1.setId(1L);
        Resource r2 = new Resource("Gloves", "Medical", BigDecimal.valueOf(0.20));
        r2.setId(2L);

        storage.addResource(r1, 100);
        storage.addResource(r1, 50);
        storage.addResource(r2, 200);

        assertEquals(150, storage.getResources().get(r1));
        assertEquals(200, storage.getResources().get(r2));
        assertEquals(350, storage.getTotalQuantity());

        storage.removeResource(r1, 30);
        assertEquals(120, storage.getResources().get(r1));

        storage.removeResource(r1, 120);
        assertFalse(storage.getResources().containsKey(r1));

        assertThrows(Exception.class, () -> storage.removeResource(r1, 10));

        storage.addResource(r2, 100);
        assertThrows(Exception.class, () -> storage.removeResource(r2, 500));

        storage.setId(10L);
        assertEquals(10L, storage.getId());
    }
}