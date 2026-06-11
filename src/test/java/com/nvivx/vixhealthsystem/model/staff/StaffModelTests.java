package com.nvivx.vixhealthsystem.model.staff;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StaffModelTests {

    // ========== Shift Tests ==========
    @Test
    void testShift() {
        Shift shift = new Shift();
        shift.setId(1L);
        shift.setEmployeeId(10L);
        shift.setDate(LocalDate.of(2024, 12, 15));
        shift.setShiftType("MORNING");
        shift.setNotes("Regular shift");

        assertEquals(1L, shift.getId());
        assertEquals(10L, shift.getEmployeeId());
        assertEquals(LocalDate.of(2024, 12, 15), shift.getDate());
        assertEquals("MORNING", shift.getShiftType());
        assertEquals("Regular shift", shift.getNotes());
    }

    @Test
    void testShiftParameterized() {
        LocalDate date = LocalDate.of(2024, 12, 20);
        Shift shift = new Shift(2L, 20L, date, "NIGHT", "Weekend");

        assertEquals(2L, shift.getId());
        assertEquals(20L, shift.getEmployeeId());
        assertEquals(date, shift.getDate());
        assertEquals("NIGHT", shift.getShiftType());
        assertEquals("Weekend", shift.getNotes());
    }

    @Test
    void testShiftTypes() {
        Shift shift = new Shift();
        shift.setShiftType("MORNING");
        assertEquals("MORNING", shift.getShiftType());
        shift.setShiftType("AFTERNOON");
        assertEquals("AFTERNOON", shift.getShiftType());
        shift.setShiftType("NIGHT");
        assertEquals("NIGHT", shift.getShiftType());
    }

    // ========== VacationRequest Tests ==========
    @Test
    void testVacationRequest() {
        VacationRequest request = new VacationRequest();
        request.setId(1);
        request.setEmployeeId(5);
        request.setEmployeeName("John Doe");
        request.setStartDate(LocalDate.of(2024, 12, 20));
        request.setEndDate(LocalDate.of(2024, 12, 27));
        request.setReason("Family vacation");
        request.setStatus("PENDING");

        assertEquals(1, request.getId());
        assertEquals(5, request.getEmployeeId());
        assertEquals("John Doe", request.getEmployeeName());
        assertEquals(LocalDate.of(2024, 12, 20), request.getStartDate());
        assertEquals(LocalDate.of(2024, 12, 27), request.getEndDate());
        assertEquals("Family vacation", request.getReason());
        assertEquals("PENDING", request.getStatus());
    }

    @Test
    void testVacationRequestParameterized() {
        LocalDate start = LocalDate.of(2024, 6, 1);
        LocalDate end = LocalDate.of(2024, 6, 7);
        VacationRequest request = new VacationRequest(10, 100, "Jane Smith", start, end, "Medical leave", "APPROVED");

        assertEquals(10, request.getId());
        assertEquals(100, request.getEmployeeId());
        assertEquals("Jane Smith", request.getEmployeeName());
        assertEquals(start, request.getStartDate());
        assertEquals(end, request.getEndDate());
        assertEquals("Medical leave", request.getReason());
        assertEquals("APPROVED", request.getStatus());
    }

    @Test
    void testVacationRequestDaysRequested() {
        VacationRequest request = new VacationRequest();
        request.setStartDate(LocalDate.of(2024, 12, 20));
        request.setEndDate(LocalDate.of(2024, 12, 27));
        assertEquals(8, request.getDaysRequested());

        request.setStartDate(LocalDate.of(2024, 12, 20));
        request.setEndDate(LocalDate.of(2024, 12, 20));
        assertEquals(1, request.getDaysRequested());

        request.setStartDate(null);
        assertEquals(0, request.getDaysRequested());

        request.setStartDate(LocalDate.of(2024, 1, 28));
        request.setEndDate(LocalDate.of(2024, 2, 4));
        assertEquals(8, request.getDaysRequested());
    }

    @Test
    void testVacationRequestStatuses() {
        VacationRequest request = new VacationRequest();

        request.setStatus("PENDING");
        assertEquals("PENDING", request.getStatus());

        request.setStatus("APPROVED");
        assertEquals("APPROVED", request.getStatus());

        request.setStatus("DENIED");
        assertEquals("DENIED", request.getStatus());
    }
}