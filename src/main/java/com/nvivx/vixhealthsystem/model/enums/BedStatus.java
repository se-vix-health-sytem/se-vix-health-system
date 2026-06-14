package com.nvivx.vixhealthsystem.model.enums;

/**
 * @brief Availability state of a bed inside an {@link com.nvivx.vixhealthsystem.model.facility.InternationRoom}.
 *
 * Derived at runtime from occupancy counts rather than persisted directly,
 * so the value always reflects the current patient list of the room.
 */
public enum BedStatus {
    /** At least one bed in the room is unoccupied. */
    FREE,
    /** Every bed in the room is taken. */
    OCCUPIED
}
