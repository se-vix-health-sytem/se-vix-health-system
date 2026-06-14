package com.nvivx.vixhealthsystem.dto;

import lombok.Data;

/**
 * @brief Request body for initiating a payment for an appointment (UC11 — Pay for Service).
 *
 * Card details are passed in plain text here for prototype purposes only;
 * a production build must route these fields through a PCI-DSS-compliant gateway
 * and must never persist them.
 *
 * @see PaymentResponse
 * @see PaymentStatus
 */
@Data
public class PaymentRequest {

    // =========================================================
    // FIELDS
    // =========================================================

    /** Identifier of the appointment this payment settles. */
    private int appointmentId;

    /** 16-digit card number — must not be stored after processing. */
    private String cardNumber;

    /** Card expiry in {@code MM/YY} format. */
    private String expiryDate;

    /** Card verification value — must not be stored after processing. */
    private String cvv;

    /** Amount to charge in the facility's local currency. */
    private float amount;
}
