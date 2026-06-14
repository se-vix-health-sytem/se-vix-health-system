package com.nvivx.vixhealthsystem.model.enums;

/**
 * @brief Time slot of a {@link com.nvivx.vixhealthsystem.model.staff.Shift} assigned to an employee.
 *
 * Used by the staff manager when scheduling weekly rosters to cover the three
 * standard hospital shift windows across a 24-hour day.
 */
public enum ShiftType {
    /** Early shift, typically covering the first part of the working day. */
    MORNING,
    /** Mid-day shift covering the afternoon hours. */
    AFTERNOON,
    /** Overnight shift for continuous hospital coverage. */
    NIGHT
}
