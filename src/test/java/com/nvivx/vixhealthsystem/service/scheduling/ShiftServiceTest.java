package com.nvivx.vixhealthsystem.service.scheduling;

import com.nvivx.vixhealthsystem.model.enums.ShiftType;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShiftServiceTest {

    private AuditService auditService;
    private ShiftService shiftService;

    private static final String FILE_PATH =
            "src/main/resources/storage/shifts.json";

    private String originalFileContent;
    private boolean fileExistedBeforeTest;

    /**
     * Runs before every test.
     *
     * Creates a mocked AuditService,
     * backs up the original JSON file,
     * and initializes an empty shifts.json
     * so tests remain independent.
     */
    @BeforeEach
    void setUp() throws Exception {
        auditService = mock(AuditService.class);

        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        fileExistedBeforeTest = file.exists();

        if (fileExistedBeforeTest) {
            originalFileContent = Files.readString(file.toPath());
        } else {
            originalFileContent = null;
        }

        Files.writeString(file.toPath(), "[]");

        shiftService = new ShiftService(auditService);
    }

    /**
     * Runs after every test.
     *
     * Restores the original content of
     * shifts.json so test execution does not
     * modify application data permanently.
     */
    @AfterEach
    void tearDown() throws Exception {
        File file = new File(FILE_PATH);

        if (fileExistedBeforeTest) {
            Files.writeString(file.toPath(), originalFileContent);
        } else {
            Files.deleteIfExists(file.toPath());
        }
    }

    /**
     * Removes the generated shiftTypeName field
     * from the JSON file so Jackson can deserialize
     * Shift objects correctly during tests.
     */
    private void removeShiftTypeNameFromJson() throws Exception {
        File file = new File(FILE_PATH);

        String content = Files.readString(file.toPath());

        content = content.replaceAll(
                ",\\s*\"shiftTypeName\"\\s*:\\s*\"[^\"]*\"",
                ""
        );

        Files.writeString(file.toPath(), content);
    }

    /**
     * Tests that assignShift() correctly creates a new shift
     * for an employee and stores it in the JSON file.
     */

    @Test
    void shouldAssignShiftSuccessfully() throws Exception {
        Long employeeId = 1L;
        LocalDate date = LocalDate.of(2026, 6, 12);

        shiftService.assignShift(employeeId, date, "MORNING");
        removeShiftTypeNameFromJson();

        List<Shift> shifts = shiftService.getShiftsForEmployee(employeeId);

        assertEquals(1, shifts.size());

        Shift shift = shifts.get(0);

        assertEquals(1L, shift.getId());
        assertEquals(employeeId, shift.getEmployeeId());
        assertEquals(date, shift.getDate());
        assertEquals(ShiftType.MORNING, shift.getShiftType());
        assertEquals("Assigned by Staff Manager", shift.getNotes());

        verify(auditService).log(
                eq("ASSIGN_SHIFT"),
                eq("Shift"),
                eq("1"),
                contains("Assigned MORNING shift")
        );
    }

    /**
     * Tests that getAllShifts() returns every shift
     * currently stored in the JSON file.
     */

    @Test
    void shouldReturnAllShifts() throws Exception {
        shiftService.assignShift(1L, LocalDate.of(2026, 6, 12), "MORNING");
        removeShiftTypeNameFromJson();

        shiftService.assignShift(2L, LocalDate.of(2026, 6, 13), "NIGHT");
        removeShiftTypeNameFromJson();

        List<Shift> result = shiftService.getAllShifts();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals(2L, result.get(1).getEmployeeId());
    }

    /**
     * Tests that getShiftsForEmployee() returns only
     * the shifts assigned to the requested employee.
     */

    @Test
    void shouldGetShiftsForEmployee() throws Exception {
        shiftService.assignShift(1L, LocalDate.of(2026, 6, 12), "MORNING");
        removeShiftTypeNameFromJson();

        shiftService.assignShift(2L, LocalDate.of(2026, 6, 12), "NIGHT");
        removeShiftTypeNameFromJson();

        shiftService.assignShift(1L, LocalDate.of(2026, 6, 13), "AFTERNOON");
        removeShiftTypeNameFromJson();

        List<Shift> result = shiftService.getShiftsForEmployee(1L);

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(s -> s.getEmployeeId().equals(1L)));
    }

    /**
     * Tests that getShiftsForWeek() returns only shifts
     * that fall within the specified week range.
     */

    @Test
    void shouldGetShiftsForWeek() throws Exception {
        LocalDate weekStart = LocalDate.of(2026, 6, 8);

        shiftService.assignShift(1L, LocalDate.of(2026, 6, 8), "MORNING");
        removeShiftTypeNameFromJson();

        shiftService.assignShift(2L, LocalDate.of(2026, 6, 14), "NIGHT");
        removeShiftTypeNameFromJson();

        shiftService.assignShift(3L, LocalDate.of(2026, 6, 15), "AFTERNOON");
        removeShiftTypeNameFromJson();

        List<Shift> result = shiftService.getShiftsForWeek(weekStart);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s ->
                !s.getDate().isBefore(weekStart)
                        && !s.getDate().isAfter(weekStart.plusDays(6))
        ));
    }

    /**
     * Tests that getShiftsForEmployeeBetweenDates()
     * returns only shifts for one employee within
     * the specified date interval.
     */

    @Test
    void shouldGetShiftsForEmployeeBetweenDates() throws Exception {
        Long employeeId = 1L;

        shiftService.assignShift(employeeId, LocalDate.of(2026, 6, 10), "MORNING");
        removeShiftTypeNameFromJson();

        shiftService.assignShift(employeeId, LocalDate.of(2026, 6, 12), "NIGHT");
        removeShiftTypeNameFromJson();

        shiftService.assignShift(employeeId, LocalDate.of(2026, 6, 20), "AFTERNOON");
        removeShiftTypeNameFromJson();

        shiftService.assignShift(2L, LocalDate.of(2026, 6, 12), "MORNING");
        removeShiftTypeNameFromJson();

        List<Shift> result =
                shiftService.getShiftsForEmployeeBetweenDates(
                        employeeId,
                        LocalDate.of(2026, 6, 10),
                        LocalDate.of(2026, 6, 15)
                );

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(s -> s.getEmployeeId().equals(employeeId)));
    }

    /**
     * Tests that isEmployeeOnShift() returns true
     * when an employee has a shift on the given date.
     */

    @Test
    void shouldDetectEmployeeOnShift() throws Exception {
        Long employeeId = 3L;
        LocalDate date = LocalDate.of(2026, 6, 14);

        shiftService.assignShift(employeeId, date, "NIGHT");
        removeShiftTypeNameFromJson();

        boolean result = shiftService.isEmployeeOnShift(employeeId, date);

        assertTrue(result);
    }

    /**
     * Tests that isEmployeeOnShift() returns false
     * when an employee has no shift on the given date.
     */
    @Test
    void shouldReturnFalseWhenEmployeeIsNotOnShift() throws Exception {
        Long employeeId = 3L;
        LocalDate date = LocalDate.of(2026, 6, 14);

        shiftService.assignShift(employeeId, LocalDate.of(2026, 6, 15), "NIGHT");
        removeShiftTypeNameFromJson();

        boolean result = shiftService.isEmployeeOnShift(employeeId, date);

        assertFalse(result);
    }

    /**
     * Tests that assigning a second shift on the same day
     * replaces the previous shift instead of creating duplicates.
     */
    @Test
    void shouldReplaceExistingShiftSameDay() throws Exception {
        Long employeeId = 4L;
        LocalDate date = LocalDate.of(2026, 6, 15);

        shiftService.assignShift(employeeId, date, "MORNING");
        removeShiftTypeNameFromJson();

        shiftService.assignShift(employeeId, date, "AFTERNOON");
        removeShiftTypeNameFromJson();

        List<Shift> shifts = shiftService.getShiftsForEmployee(employeeId);

        assertEquals(1, shifts.size());
        assertEquals(ShiftType.AFTERNOON, shifts.get(0).getShiftType());

        verify(auditService, times(2)).log(
                eq("ASSIGN_SHIFT"),
                eq("Shift"),
                anyString(),
                contains("Assigned")
        );
    }


    /**
     * Tests that removeShift() removes an existing shift
     * from the JSON storage.
     */
    @Test
    void shouldRemoveShift() throws Exception {
        Long employeeId = 2L;
        LocalDate date = LocalDate.of(2026, 6, 13);

        shiftService.assignShift(employeeId, date, "AFTERNOON");
        removeShiftTypeNameFromJson();

        List<Shift> before =
                shiftService.getShiftsForEmployee(employeeId);

        assertEquals(1, before.size());

        Long shiftId = before.get(0).getId();

        shiftService.removeShift(shiftId);
        removeShiftTypeNameFromJson();

        List<Shift> after =
                shiftService.getShiftsForEmployee(employeeId);

        assertTrue(after.isEmpty());

        verify(auditService).log(
                eq("REMOVE_SHIFT"),
                eq("Shift"),
                eq(String.valueOf(shiftId)),
                contains("Removed shift")
        );
    }

    /**
     * Tests that removing a non-existing shift
     * does not create an audit log entry.
     */
    @Test
    void shouldNotLogWhenRemovingNonExistingShift() throws Exception {
        shiftService.removeShift(999L);
        removeShiftTypeNameFromJson();

        List<Shift> result = shiftService.getAllShifts();

        assertTrue(result.isEmpty());

        verify(auditService, never()).log(
                eq("REMOVE_SHIFT"),
                anyString(),
                anyString(),
                anyString()
        );
    }

    /**
     * Tests that assignShift() throws an exception
     * when an invalid shift type is provided.
     */
    @Test
    void shouldThrowWhenShiftTypeIsInvalid() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> shiftService.assignShift(
                        1L,
                        LocalDate.of(2026, 6, 12),
                        "EVENING"
                )
        );

        assertTrue(exception.getMessage().contains("No enum constant"));

        verifyNoInteractions(auditService);
    }
}