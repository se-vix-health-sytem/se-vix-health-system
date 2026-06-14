package com.nvivx.vixhealthsystem.dto;

import lombok.Data;

/**
 * @brief Snapshot of a payment's current state at a given point in time.
 *
 * Used by status-polling endpoints to let the UI show real-time payment progress
 * without re-triggering the charge.
 *
 * @see PaymentResponse
 */
@Data
public class PaymentStatus {

    // =========================================================
    // FIELDS
    // =========================================================

    /**
     * Current payment state
     * (e.g. {@code "PENDING"}, {@code "COMPLETED"}, {@code "FAILED"}).
     */
    private String status;

    /** ISO-8601 timestamp of when this status was recorded. */
    private String timestamp;
}
