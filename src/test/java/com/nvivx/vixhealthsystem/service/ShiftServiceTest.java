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

class ShiftServiceTest {

    private AuditService auditService;
    private ShiftService shiftService;

    private static final String FILE_PATH =
            "src/main/resources/storage/shifts.json";

    @BeforeEach
    void setUp() throws Exception {
        auditService = mock(AuditService.class);

        // ⚠️ IMPORTANT: reset file before each test (prevents flaky dates like your 06-29 issue)
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.getParentFile().mkdirs();
            new PrintWriter(file).close(); // clear file
        }

        shiftService = new ShiftService(auditService);
    }

    @Test
    void shouldAssignShiftSuccessfully() {
        Long employeeId = 1L;
        LocalDate date = LocalDate.of(2026, 6, 12);

        shiftService.assignShift(employeeId, date, "MORNING");

        List<Shift> shifts = shiftService.getShiftsForEmployee(employeeId);

        assertEquals(1, shifts.size());

        Shift shift = shifts.get(0);

        assertEquals(employeeId, shift.getEmployeeId());
        assertEquals(date, shift.getDate());
        assertEquals("MORNING", shift.getShiftType());

        verify(auditService).log(
                eq("ASSIGN_SHIFT"),
                eq("Shift"),
                anyString(),
                contains("Assigned MORNING shift")
        );
    }

    @Test
    void shouldRemoveShift() {
        Long employeeId = 2L;
        LocalDate date = LocalDate.of(2026, 6, 13);

        shiftService.assignShift(employeeId, date, "EVENING");

        List<Shift> before = shiftService.getShiftsForEmployee(employeeId);
        assertEquals(1, before.size());

        Long shiftId = before.get(0).getId();

        shiftService.removeShift(shiftId);

        List<Shift> after = shiftService.getShiftsForEmployee(employeeId);
        assertTrue(after.isEmpty());

        verify(auditService).log(
                eq("REMOVE_SHIFT"),
                eq("Shift"),
                eq(String.valueOf(shiftId)),
                contains("Removed shift")
        );
    }

    @Test
    void shouldDetectEmployeeOnShift() {
        Long employeeId = 3L;
        LocalDate date = LocalDate.of(2026, 6, 14);

        shiftService.assignShift(employeeId, date, "NIGHT");

        assertTrue(shiftService.isEmployeeOnShift(employeeId, date));
    }

    @Test
    void shouldReplaceExistingShiftSameDay() {
        Long employeeId = 4L;
        LocalDate date = LocalDate.of(2026, 6, 15);

        shiftService.assignShift(employeeId, date, "MORNING");
        shiftService.assignShift(employeeId, date, "EVENING");

        List<Shift> shifts = shiftService.getShiftsForEmployee(employeeId);

        // only 1 shift should exist (old replaced)
        assertEquals(1, shifts.size());
        assertEquals("EVENING", shifts.get(0).getShiftType());
    }
}