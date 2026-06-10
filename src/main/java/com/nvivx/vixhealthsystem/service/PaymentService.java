// src/main/java/com/nvivx/vixhealthsystem/service/PaymentService.java
package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.dto.PaymentRequest;
import com.nvivx.vixhealthsystem.dto.PaymentResponse;
import com.nvivx.vixhealthsystem.dto.PaymentStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final AuditService auditService;
    private final Map<String, PaymentStatus> paymentStatusMap = new HashMap<>();

    public PaymentService(AuditService auditService) {
        this.auditService = auditService;
    }

    public float getAppointmentCost(int appointmentId) {
        // Different costs based on appointment type
        return 85.00f; // Standard consultation
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();

        // SIMULATED payment processing - always succeeds for demo
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        response.setTransactionId(transactionId);
        response.setStatus("SUCCESS");
        response.setMessage("Payment processed successfully (Demo Mode)");

        // Store payment status
        PaymentStatus status = new PaymentStatus();
        status.setStatus("COMPLETED");
        status.setTimestamp(LocalDateTime.now().toString());
        paymentStatusMap.put(transactionId, status);

        // Log the payment
        auditService.log("PROCESS_PAYMENT", "Appointment", String.valueOf(request.getAppointmentId()),
                String.format("Payment of €%.2f processed. Transaction: %s", request.getAmount(), transactionId));

        // Simulate slight delay
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        return response;
    }

    public PaymentStatus getPaymentStatus(String paymentId) {
        return paymentStatusMap.getOrDefault(paymentId, createNotFoundStatus());
    }

    private PaymentStatus createNotFoundStatus() {
        PaymentStatus status = new PaymentStatus();
        status.setStatus("NOT_FOUND");
        status.setTimestamp(LocalDateTime.now().toString());
        return status;
    }
}