package com.nvivx.vixhealthsystem.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnumsTest {

    @Test
    void appointmentStatus_ShouldHaveCorrectValues() {
        assertEquals("PENDING", AppointmentStatus.PENDING.name());
        assertEquals("CONFIRMED", AppointmentStatus.CONFIRMED.name());
        assertEquals("CANCELLED", AppointmentStatus.CANCELLED.name());
        assertEquals("COMPLETED", AppointmentStatus.COMPLETED.name());
        assertEquals("RESCHEDULED", AppointmentStatus.RESCHEDULED.name());

        assertEquals(5, AppointmentStatus.values().length);
    }

    @Test
    void bedStatus_ShouldHaveCorrectValues() {
        assertEquals("FREE", BedStatus.FREE.name());
        assertEquals("OCCUPIED", BedStatus.OCCUPIED.name());
        assertEquals(2, BedStatus.values().length);
    }

    @Test
    void employeeType_ShouldHaveCorrectValues() {
        assertEquals("MEDICAL_SPECIALIST", EmployeeType.MEDICAL_SPECIALIST.name());
        assertEquals("SECRETARY", EmployeeType.SECRETARY.name());
        assertEquals("TECHNICIAN", EmployeeType.TECHNICIAN.name());
        assertEquals("BUYER", EmployeeType.BUYER.name());
        assertEquals("STAFF_MANAGER", EmployeeType.STAFF_MANAGER.name());
        assertEquals(5, EmployeeType.values().length);
    }

    @Test
    void machineStatus_ShouldHaveCorrectValues() {
        assertEquals("WORKING", MachineStatus.WORKING.name());
        assertEquals("FAULTY", MachineStatus.FAULTY.name());
        assertEquals("UNDER_MAINTENANCE", MachineStatus.UNDER_MAINTENANCE.name());
        assertEquals(3, MachineStatus.values().length);
    }

    @Test
    void paymentStatus_ShouldHaveCorrectValues() {
        assertEquals("PAID", com.nvivx.vixhealthsystem.model.enums.PaymentStatus.PAID.name());
        assertEquals("UNPAID", com.nvivx.vixhealthsystem.model.enums.PaymentStatus.UNPAID.name());
        assertEquals(2, com.nvivx.vixhealthsystem.model.enums.PaymentStatus.values().length);
    }

    @Test
    void role_ShouldHaveCorrectValues() {
        assertEquals("ROLE_PATIENT", Role.ROLE_PATIENT.name());
        assertEquals("ROLE_MEDICAL_SPECIALIST", Role.ROLE_MEDICAL_SPECIALIST.name());
        assertEquals("ROLE_SECRETARY", Role.ROLE_SECRETARY.name());
        assertEquals("ROLE_TECHNICIAN", Role.ROLE_TECHNICIAN.name());
        assertEquals("ROLE_STAFF_MANAGER", Role.ROLE_STAFF_MANAGER.name());
        assertEquals("ROLE_BUYER", Role.ROLE_BUYER.name());
        assertEquals(6, Role.values().length);
    }

    @Test
    void shiftType_ShouldHaveCorrectValues() {
        assertEquals("MORNING", ShiftType.MORNING.name());
        assertEquals("AFTERNOON", ShiftType.AFTERNOON.name());
        assertEquals("NIGHT", ShiftType.NIGHT.name());
        assertEquals(3, ShiftType.values().length);
    }

    @Test
    void vacationStatus_ShouldHaveCorrectValues() {
        assertEquals("PENDING", VacationStatus.PENDING.name());
        assertEquals("APPROVED", VacationStatus.APPROVED.name());
        assertEquals("DENIED", VacationStatus.DENIED.name());
        assertEquals(3, VacationStatus.values().length);
    }
}