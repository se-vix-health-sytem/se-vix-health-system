package com.nvivx.vixhealthsystem.model;

import com.nvivx.vixhealthsystem.model.enums.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnumTests {

    @Test
    void testAppointmentStatus() {
        assertEquals(4, AppointmentStatus.values().length);
        assertNotNull(AppointmentStatus.valueOf("PENDING"));
        assertNotNull(AppointmentStatus.valueOf("CONFIRMED"));
        assertNotNull(AppointmentStatus.valueOf("CANCELLED"));
        assertNotNull(AppointmentStatus.valueOf("COMPLETED"));
    }

    @Test
    void testBedStatus() {
        assertEquals(2, BedStatus.values().length);
        assertNotNull(BedStatus.valueOf("FREE"));
        assertNotNull(BedStatus.valueOf("OCCUPIED"));
    }

    @Test
    void testEmployeeType() {
        assertEquals(5, EmployeeType.values().length);
        assertNotNull(EmployeeType.valueOf("MEDICAL_SPECIALIST"));
        assertNotNull(EmployeeType.valueOf("SECRETARY"));
        assertNotNull(EmployeeType.valueOf("TECHNICIAN"));
        assertNotNull(EmployeeType.valueOf("BUYER"));
        assertNotNull(EmployeeType.valueOf("STAFF_MANAGER"));
    }

    @Test
    void testMachineStatus() {
        assertEquals(3, MachineStatus.values().length);
        assertNotNull(MachineStatus.valueOf("WORKING"));
        assertNotNull(MachineStatus.valueOf("FAULTY"));
        assertNotNull(MachineStatus.valueOf("UNDER_MAINTENANCE"));
    }

    @Test
    void testPaymentStatus() {
        assertEquals(2, PaymentStatus.values().length);
        assertNotNull(PaymentStatus.valueOf("PAID"));
        assertNotNull(PaymentStatus.valueOf("UNPAID"));
    }

    @Test
    void testRole() {
        assertEquals(6, Role.values().length);
        assertNotNull(Role.valueOf("ROLE_PATIENT"));
        assertNotNull(Role.valueOf("ROLE_MEDICAL_SPECIALIST"));
        assertNotNull(Role.valueOf("ROLE_SECRETARY"));
        assertNotNull(Role.valueOf("ROLE_TECHNICIAN"));
        assertNotNull(Role.valueOf("ROLE_STAFF_MANAGER"));
        assertNotNull(Role.valueOf("ROLE_BUYER"));
    }
}