package com.nvivx.vixhealthsystem.model.staff;

import com.nvivx.vixhealthsystem.model.enums.ShiftType;
import java.time.LocalDate;

/**
 * Represents a work shift assigned to an employee.
 * <p>
 * Shifts are stored as JSON (shifts.json), not in the SQL database.
 * Each shift is linked to an employee by ID and carries a date,
 * type and optional notes.
 */
public class Shift {

    /**
     * Unique shift identifier.
     */
    private Long id;

    /**
     * Identifier of the employee assigned to this shift.
     */
    private Long employeeId;

    /**
     * Date of the shift.
     */
    private LocalDate date;

    /**
     * Shift type (MORNING, AFTERNOON, NIGHT).
     */
    private ShiftType shiftType;

    /**
     * Optional notes for the shift.
     */
    private String notes;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required for JSON deserialization.
     */
    public Shift() {
    }

    /**
     * Creates a shift with the specified details.
     *
     * @param id         the shift identifier
     * @param employeeId the employee identifier
     * @param date       the shift date
     * @param shiftType  the shift type
     * @param notes      optional notes
     */
    public Shift(
            Long id,
            Long employeeId,
            LocalDate date,
            ShiftType shiftType,
            String notes
    ) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.shiftType = shiftType;
        this.notes = notes;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the unique shift identifier.
     *
     * @return the shift ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique shift identifier.
     *
     * @param id the shift ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the identifier of the employee assigned to this shift.
     *
     * @return the employee ID
     */
    public Long getEmployeeId() {
        return employeeId;
    }

    /**
     * Sets the identifier of the employee assigned to this shift.
     *
     * @param employeeId the employee ID to set
     */
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Returns the date of the shift.
     *
     * @return the shift date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date of the shift.
     *
     * @param date the shift date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns the shift type enum.
     */
    public ShiftType getShiftType() {
        return shiftType;
    }

    /**
     * Returns the shift type as a String (backward-compatible for templates and JSON).
     */
    public String getShiftTypeName() {
        return shiftType != null ? shiftType.name() : null;
    }

    /**
     * Sets the shift type using the enum.
     */
    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    /**
     * Sets the shift type from a String (backward-compatible for JSON deserialization).
     */
    public void setShiftType(String shiftType) {
        this.shiftType = shiftType != null ? ShiftType.valueOf(shiftType) : null;
    }

    /**
     * Returns the optional notes for the shift.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the optional notes for the shift.
     *
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
