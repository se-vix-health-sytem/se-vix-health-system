package com.nvivx.vixhealthsystem.dto;

import lombok.Data;

/**
 * @brief Response payload returned after a payment attempt.
 *
 * {@code transactionId} is the external gateway reference that can be used for
 * reconciliation or refund requests.  {@code message} carries a human-readable
 * outcome description suitable for display in the UI.
 *
 * @see PaymentRequest
 * @see PaymentStatus
 */
@Data
public class PaymentResponse {

    // =========================================================
    // FIELDS
    // =========================================================

    /** Unique identifier assigned by the payment gateway for this transaction. */
    private String transactionId;

    /**
     * Outcome of the payment attempt
     * (e.g. {@code "SUCCESS"}, {@code "DECLINED"}, {@code "ERROR"}).
     */
    private String status;

    /** Human-readable message describing the outcome, suitable for UI display. */
    private String message;
}
