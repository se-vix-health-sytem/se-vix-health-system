package com.nvivx.vixhealthsystem.model.enums;

/**
 * @brief Payment state of a patient {@link com.nvivx.vixhealthsystem.model.medical.Appointment}.
 *
 * Used by the payment controller to determine whether a visit fee has been
 * settled before the appointment can be marked as completed.
 */
public enum PaymentStatus {
    /** The appointment fee has been paid. */
    PAID,
    /** The appointment fee has not yet been paid. */
    UNPAID
}
