package com.nvivx.vixhealthsystem.service.scheduling;

import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * Arrange = prepare fake data and mock behavior
 * Act = call the method being tested
 * Assert = check the result
 * Verify = check that mocks were called correctly
 */
class ShiftServiceTest {

    private AuditService auditService;
    private ShiftService shiftService;

    private static final String FILE_PATH =
            "src/main/resources/storage/shifts.json";

    /**
     * Runs before every test.
     *
     * It creates a mock AuditService and clears the shifts.json file.
     * This keeps every test independent.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Arrange: create mocked dependency
        auditService = mock(AuditService.class);

        // Arrange: clear the JSON file before each test
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        if (file.exists()) {
            new PrintWriter(file).close();
        }

        // Arrange: create the service using the mocked AuditService
        shiftService = new ShiftService(auditService);
    }

    /**
     * Tests that assignShift() correctly creates a new shift
     * for an employee on a specific date.
     */
    @Test
    void shouldAssignShiftSuccessfully() {
        // Arrange
        Long employeeId = 1L;
        LocalDate date = LocalDate.of(2026, 6, 12);
        String shiftType = "MORNING";

        // Act
        shiftService.assignShift(employeeId, date, shiftType);

        List<Shift> shifts =
                shiftService.getShiftsForEmployee(employeeId);

        // Assert
        assertEquals(1, shifts.size());

        Shift shift = shifts.get(0);

        assertEquals(employeeId, shift.getEmployeeId());
        assertEquals(date, shift.getDate());
        assertEquals("MORNING", shift.getShiftType());

        // Verify
        verify(auditService).log(
                eq("ASSIGN_SHIFT"),
                eq("Shift"),
                anyString(),
                contains("Assigned MORNING shift")
        );
    }

    /**
     * Tests that removeShift() removes an existing shift
     * from the JSON storage.
     */
    @Test
    void shouldRemoveShift() {
        // Arrange
        Long employeeId = 2L;
        LocalDate date = LocalDate.of(2026, 6, 13);

        shiftService.assignShift(employeeId, date, "EVENING");

        List<Shift> before =
                shiftService.getShiftsForEmployee(employeeId);

        assertEquals(1, before.size());

        Long shiftId = before.get(0).getId();

        // Act
        shiftService.removeShift(shiftId);

        List<Shift> after =
                shiftService.getShiftsForEmployee(employeeId);

        // Assert
        assertTrue(after.isEmpty());

        // Verify
        verify(auditService).log(
                eq("REMOVE_SHIFT"),
                eq("Shift"),
                eq(String.valueOf(shiftId)),
                contains("Removed shift")
        );
    }

    /**
     * Tests that isEmployeeOnShift() returns true
     * when the employee has a shift on that date.
     */
    @Test
    void shouldDetectEmployeeOnShift() {
        // Arrange
        Long employeeId = 3L;
        LocalDate date = LocalDate.of(2026, 6, 14);

        shiftService.assignShift(employeeId, date, "NIGHT");

        // Act
        boolean result =
                shiftService.isEmployeeOnShift(employeeId, date);

        // Assert
        assertTrue(result);

        // Verify
        verify(auditService).log(
                eq("ASSIGN_SHIFT"),
                eq("Shift"),
                anyString(),
                contains("Assigned NIGHT shift")
        );
    }

    /**
     * Tests that assigning a new shift on the same date
     * replaces the previous shift instead of creating duplicates.
     */
    @Test
    void shouldReplaceExistingShiftSameDay() {
        // Arrange
        Long employeeId = 4L;
        LocalDate date = LocalDate.of(2026, 6, 15);

        shiftService.assignShift(employeeId, date, "MORNING");

        // Act
        shiftService.assignShift(employeeId, date, "EVENING");

        List<Shift> shifts =
                shiftService.getShiftsForEmployee(employeeId);

        // Assert
        assertEquals(1, shifts.size());
        assertEquals("EVENING", shifts.get(0).getShiftType());

        // Verify
        verify(auditService, times(2)).log(
                eq("ASSIGN_SHIFT"),
                eq("Shift"),
                anyString(),
                contains("Assigned")
        );
    }
}