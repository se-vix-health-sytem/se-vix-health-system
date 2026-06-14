package com.nvivx.vixhealthsystem.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for all domain enums used in the VIX Health System.
 *
 * Guards against accidental enum renames or deletions by asserting both the
 * string names and the total cardinality of every enum type. Plain JUnit —
 * no Spring context loaded.
 *
 * @see AppointmentStatus
 * @see BedStatus
 * @see EmployeeType
 * @see MachineStatus
 * @see PaymentStatus
 * @see Role
 * @see ShiftType
 * @see VacationStatus
 */
class EnumsTest {

    /**
     * Verifies that AppointmentStatus exposes exactly the five lifecycle
     *        states the scheduling workflow depends on.
     */
    @Test
    void appointmentStatus_ShouldHaveCorrectValues() {
        assertEquals("PENDING", AppointmentStatus.PENDING.name());
        assertEquals("CONFIRMED", AppointmentStatus.CONFIRMED.name());
        assertEquals("CANCELLED", AppointmentStatus.CANCELLED.name());
        assertEquals("COMPLETED", AppointmentStatus.COMPLETED.name());
        assertEquals("RESCHEDULED", AppointmentStatus.RESCHEDULED.name());

        assertEquals(5, AppointmentStatus.values().length);
    }

    /**
     * Verifies that BedStatus contains exactly FREE and OCCUPIED,
     *        reflecting the binary availability model for inpatient beds.
     */
    @Test
    void bedStatus_ShouldHaveCorrectValues() {
        assertEquals("FREE", BedStatus.FREE.name());
        assertEquals("OCCUPIED", BedStatus.OCCUPIED.name());
        assertEquals(2, BedStatus.values().length);
    }

    /**
     * Verifies that EmployeeType covers all five staff categories
     *        required for role-based access control.
     */
    @Test
    void employeeType_ShouldHaveCorrectValues() {
        assertEquals("MEDICAL_SPECIALIST", EmployeeType.MEDICAL_SPECIALIST.name());
        assertEquals("SECRETARY", EmployeeType.SECRETARY.name());
        assertEquals("TECHNICIAN", EmployeeType.TECHNICIAN.name());
        assertEquals("BUYER", EmployeeType.BUYER.name());
        assertEquals("STAFF_MANAGER", EmployeeType.STAFF_MANAGER.name());
        assertEquals(5, EmployeeType.values().length);
    }

    /**
     * Verifies that MachineStatus exposes the three operational states
     *        used by the maintenance workflow.
     */
    @Test
    void machineStatus_ShouldHaveCorrectValues() {
        assertEquals("WORKING", MachineStatus.WORKING.name());
        assertEquals("FAULTY", MachineStatus.FAULTY.name());
        assertEquals("UNDER_MAINTENANCE", MachineStatus.UNDER_MAINTENANCE.name());
        assertEquals(3, MachineStatus.values().length);
    }

    /**
     * Verifies that PaymentStatus has exactly PAID and UNPAID,
     *        ensuring the billing flag remains unambiguous.
     */
    @Test
    void paymentStatus_ShouldHaveCorrectValues() {
        assertEquals("PAID", com.nvivx.vixhealthsystem.model.enums.PaymentStatus.PAID.name());
        assertEquals("UNPAID", com.nvivx.vixhealthsystem.model.enums.PaymentStatus.UNPAID.name());
        assertEquals(2, com.nvivx.vixhealthsystem.model.enums.PaymentStatus.values().length);
    }

    /**
     * Verifies that Role enumerates every Spring Security authority used
     *        in the system, covering patients and all employee types.
     */
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

    /**
     * Verifies that ShiftType covers the three standard work periods
     *        used when scheduling hospital staff.
     */
    @Test
    void shiftType_ShouldHaveCorrectValues() {
        assertEquals("MORNING", ShiftType.MORNING.name());
        assertEquals("AFTERNOON", ShiftType.AFTERNOON.name());
        assertEquals("NIGHT", ShiftType.NIGHT.name());
        assertEquals(3, ShiftType.values().length);
    }

    /**
     * Verifies that VacationStatus captures the full approval lifecycle:
     *        a request starts PENDING and can be either APPROVED or DENIED.
     */
    @Test
    void vacationStatus_ShouldHaveCorrectValues() {
        assertEquals("PENDING", VacationStatus.PENDING.name());
        assertEquals("APPROVED", VacationStatus.APPROVED.name());
        assertEquals("DENIED", VacationStatus.DENIED.name());
        assertEquals(3, VacationStatus.values().length);
    }
}