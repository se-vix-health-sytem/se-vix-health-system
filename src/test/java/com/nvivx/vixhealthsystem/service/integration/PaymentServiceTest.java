package com.nvivx.vixhealthsystem.service.integration;

import com.nvivx.vixhealthsystem.dto.PaymentRequest;
import com.nvivx.vixhealthsystem.dto.PaymentResponse;
import com.nvivx.vixhealthsystem.dto.PaymentStatus;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @brief Unit tests for PaymentService using a Mockito mock of AuditService.
 * Covers appointment cost retrieval, successful payment processing with audit logging,
 * status lookup for existing and missing transactions.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PaymentService service;

    /**
     * Tests that getAppointmentCost() returns the standard consultation cost.
     */
    @Test
    void shouldReturnAppointmentCost() {
        // Arrange
        int appointmentId = 1;

        // Act
        float cost = service.getAppointmentCost(appointmentId);

        // Assert
        assertEquals(85.00f, cost);

        // Verify
        verifyNoInteractions(auditService);
    }

    /**
     * Tests that processPayment() creates a successful payment response,
     * stores the payment status, and logs the payment.
     */
    @Test
    void shouldProcessPaymentSuccessfully() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setAppointmentId(10);
        request.setAmount(85.00f);
        request.setCardNumber("1234567812345678");
        request.setExpiryDate("12/26");
        request.setCvv("123");

        // Act
        PaymentResponse response = service.processPayment(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getTransactionId());
        assertTrue(response.getTransactionId().startsWith("TXN-"));
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Payment processed successfully (Demo Mode)", response.getMessage());

        // Verify
        verify(auditService).log(
                eq("PROCESS_PAYMENT"),
                eq("Appointment"),
                eq("10"),
                contains("Payment of €85.00 processed")
        );
    }

    /**
     * Tests that after a payment is processed,
     * getPaymentStatus() returns COMPLETED for that transaction.
     */
    @Test
    void shouldReturnCompletedStatusForExistingPayment() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setAppointmentId(20);
        request.setAmount(100.00f);

        PaymentResponse response = service.processPayment(request);

        // Act
        PaymentStatus status =
                service.getPaymentStatus(response.getTransactionId());

        // Assert
        assertNotNull(status);
        assertEquals("COMPLETED", status.getStatus());
        assertNotNull(status.getTimestamp());

        // Verify
        verify(auditService).log(
                eq("PROCESS_PAYMENT"),
                eq("Appointment"),
                eq("20"),
                contains("Payment of €100.00 processed")
        );
    }

    /**
     * Tests that getPaymentStatus() returns NOT_FOUND
     * when the transaction ID does not exist.
     */
    @Test
    void shouldReturnNotFoundStatusForMissingPayment() {
        // Arrange
        String missingPaymentId = "TXN-MISSING";

        // Act
        PaymentStatus status =
                service.getPaymentStatus(missingPaymentId);

        // Assert
        assertNotNull(status);
        assertEquals("NOT_FOUND", status.getStatus());
        assertNotNull(status.getTimestamp());

        // Verify
        verifyNoInteractions(auditService);
    }
}