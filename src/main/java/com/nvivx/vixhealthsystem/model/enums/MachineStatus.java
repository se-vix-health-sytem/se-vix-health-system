package com.nvivx.vixhealthsystem.model.enums;

/**
 * @brief Operational state of a {@link com.nvivx.vixhealthsystem.model.resource.Machinery} unit.
 *
 * Technicians use this status to prioritise repair and maintenance work across
 * the equipment installed in specialized rooms.
 */
public enum MachineStatus {
    /** Machine is operational and available for use. */
    WORKING,
    /** Machine has failed and requires repair before it can be used again. */
    FAULTY,
    /** Machine is temporarily out of service for scheduled maintenance. */
    UNDER_MAINTENANCE
}
