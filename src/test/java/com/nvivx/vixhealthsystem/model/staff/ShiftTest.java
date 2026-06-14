package com.nvivx.vixhealthsystem.model.staff;

import com.nvivx.vixhealthsystem.model.enums.ShiftType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Shift using plain JUnit (no Spring context).
 * Covers field setters/getters, the parameterized constructor, String-to-enum ShiftType
 * conversion, null string handling, and invalid string rejection.
 */
class ShiftTest {
    private Shift shift;

    @BeforeEach
    void setUp() {
        shift = new Shift();
        shift.setId(1L);
        shift.setEmployeeId(100L);
        shift.setDate(LocalDate.of(2024, 6, 15));
        shift.setShiftType(ShiftType.MORNING);
        shift.setNotes("Regular shift");
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, shift.getId());
        assertEquals(100L, shift.getEmployeeId());
        assertEquals(LocalDate.of(2024, 6, 15), shift.getDate());
        assertEquals(ShiftType.MORNING, shift.getShiftType());
        assertEquals("MORNING", shift.getShiftTypeName());
        assertEquals("Regular shift", shift.getNotes());
    }

    @Test
    void parameterizedConstructor_ShouldInitializeShift() {
        Shift newShift = new Shift(2L, 200L, LocalDate.of(2024, 6, 16), ShiftType.NIGHT, "Night duty");

        assertEquals(2L, newShift.getId());
        assertEquals(200L, newShift.getEmployeeId());
        assertEquals(LocalDate.of(2024, 6, 16), newShift.getDate());
        assertEquals(ShiftType.NIGHT, newShift.getShiftType());
        assertEquals("Night duty", newShift.getNotes());
    }

    @Test
    void setShiftType_WithString_ShouldConvertToEnum() {
        shift.setShiftType("AFTERNOON");
        assertEquals(ShiftType.AFTERNOON, shift.getShiftType());
        assertEquals("AFTERNOON", shift.getShiftTypeName());
    }

    @Test
    void setShiftType_WithNullString_ShouldSetNullEnum() {
        shift.setShiftType((String) null);
        assertNull(shift.getShiftType());
        assertNull(shift.getShiftTypeName());
    }

    @Test
    void setShiftType_WithInvalidString_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            shift.setShiftType("INVALID");
        });
    }
}