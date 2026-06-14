package com.nvivx.vixhealthsystem.model.enums;

/**
 * @brief Review state of a {@link com.nvivx.vixhealthsystem.model.staff.VacationRequest}.
 *
 * A request starts as {@code PENDING} once an employee submits it, and the staff
 * manager then moves it to either {@code APPROVED} or {@code DENIED}.
 */
public enum VacationStatus {
    /** Request has been submitted and is awaiting review by the staff manager. */
    PENDING,
    /** Request has been reviewed and granted by the staff manager. */
    APPROVED,
    /** Request has been reviewed and refused by the staff manager. */
    DENIED
}
