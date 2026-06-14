package com.nvivx.vixhealthsystem.service.integration;

import com.nvivx.vixhealthsystem.dto.PaymentRequest;
import com.nvivx.vixhealthsystem.dto.PaymentResponse;
import com.nvivx.vixhealthsystem.dto.PaymentStatus;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simulates payment processing for appointment fees (UC-Payment flow).
 *
 * In the current demo, every charge always succeeds and returns a randomly generated
 * transaction ID.  Completed transactions are stored in memory only — they do not
 * survive a server restart.  A real implementation would replace {@link #processPayment}
 * with an actual payment-gateway call (Stripe, Braintree, etc.) and persist
 * the result to the database.
 *
 * @see com.nvivx.vixhealthsystem.controllers.services.PaymentController
 * @see com.nvivx.vixhealthsystem.dto.PaymentRequest
 * @see com.nvivx.vixhealthsystem.dto.PaymentResponse
 */
@Service
public class PaymentService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final AuditService auditService;
    /** In-memory transaction store: transaction ID → status. Lost on restart. */
    private final Map<String, PaymentStatus> paymentStatusMap = new HashMap<>();

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with the audit collaborator.
     *
     * @param auditService  Records every payment event for traceability (NFR02).
     */
    public PaymentService(AuditService auditService) {
        this.auditService = auditService;
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /**
     * Returns the standard consultation fee for the given appointment.
     *
     * Currently a flat rate of €85.00 regardless of appointment type.
     * In a real system this would look up the fee schedule from the appointment's service type.
     *
     * @param appointmentId  ID of the appointment to price.
     * @return               Fee in euros.
     */
    public float getAppointmentCost(int appointmentId) {
        return 85.00f;
    }

    /**
     * Looks up the status of a previously processed transaction.
     *
     * @param paymentId  The transaction ID returned by {@link #processPayment}.
     * @return           The stored {@link PaymentStatus}, or a NOT_FOUND status if the
     *                   ID is unknown (e.g., after a server restart).
     */
    public PaymentStatus getPaymentStatus(String paymentId) {
        return paymentStatusMap.getOrDefault(paymentId, createNotFoundStatus());
    }

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Processes a simulated payment and returns a transaction result.
     *
     * Always succeeds in demo mode.  Generates a {@code TXN-XXXXXXXX} transaction ID,
     * stores the completed status in memory, and writes an audit entry.
     * The 500 ms delay is intentional — it mimics a real gateway round-trip for the demo.
     *
     * @param request  Payment details including appointment ID and amount.
     * @return         Response containing the transaction ID and SUCCESS status.
     */
    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        response.setTransactionId(transactionId);
        response.setStatus("SUCCESS");
        response.setMessage("Payment processed successfully (Demo Mode)");

        PaymentStatus status = new PaymentStatus();
        status.setStatus("COMPLETED");
        status.setTimestamp(LocalDateTime.now().toString());
        paymentStatusMap.put(transactionId, status);

        auditService.log("PROCESS_PAYMENT", "Appointment", String.valueOf(request.getAppointmentId()),
                String.format("Payment of €%.2f processed. Transaction: %s", request.getAmount(), transactionId));

        // Mimics real gateway latency so the demo UI doesn't feel instant
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        return response;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /** Returns a status object indicating the transaction ID was not found. */
    private PaymentStatus createNotFoundStatus() {
        PaymentStatus status = new PaymentStatus();
        status.setStatus("NOT_FOUND");
        status.setTimestamp(LocalDateTime.now().toString());
        return status;
    }
}