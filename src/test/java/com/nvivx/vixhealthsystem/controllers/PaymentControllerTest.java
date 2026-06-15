package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.services.PaymentController;
import com.nvivx.vixhealthsystem.dto.PaymentRequest;
import com.nvivx.vixhealthsystem.dto.PaymentResponse;
import com.nvivx.vixhealthsystem.dto.PaymentStatus;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.integration.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @class PaymentControllerTest
 * @brief Unit tests for PaymentController (payment processing module).
 *
 * These tests cover the full payment workflow:
 * - Payment page rendering
 * - Payment processing
 * - Payment status retrieval
 * - Fake payment success/failure scenarios
 * - Confirmation page rendering
 */
class PaymentControllerTest {

    /// Mocked payment service handling external payment logic.
    private PaymentService paymentService;

    /// Mocked appointment repository.
    private JsonAppointmentRepository appointmentRepository;

    /// Controller under test.
    private PaymentController controller;

    /**
     * @brief Initializes controller and mocks before each test.
     */
    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        appointmentRepository = mock(JsonAppointmentRepository.class);

        controller = new PaymentController(paymentService, appointmentRepository);
    }

    // =========================================================
    // PAYMENT PAGE
    // =========================================================

    /**
     * @brief Verifies that the payment page loads with correct data.
     */
    @Test
    void shouldShowPaymentPage() {

        Appointment appointment = new Appointment();

        when(appointmentRepository.findById(1)).thenReturn(appointment);
        when(paymentService.getAppointmentCost(1)).thenReturn(85.0f);

        Model model = mock(Model.class);

        String view = controller.showPaymentPage(1, model);

        assertEquals("payment/payment-form", view);

        verify(model).addAttribute("appointmentId", 1);
        verify(model).addAttribute("amount", 85.0f);
        verify(paymentService).getAppointmentCost(1);
    }

    /**
     * @brief Verifies redirect when appointment does not exist.
     */
    @Test
    void shouldRedirectWhenAppointmentNotFound() {

        when(appointmentRepository.findById(1)).thenReturn(null);

        Model model = mock(Model.class);

        String view = controller.showPaymentPage(1, model);

        assertEquals(
                "redirect:/patient/appointments?error=Appointment+not+found",
                view
        );

        verify(paymentService, never()).getAppointmentCost(anyInt());
    }

    // =========================================================
    // PAYMENT PROCESSING
    // =========================================================

    /**
     * @brief Verifies successful payment processing.
     */
    @Test
    void shouldProcessPayment() {

        PaymentRequest request = new PaymentRequest();

        PaymentResponse response = new PaymentResponse();
        response.setStatus("SUCCESS");

        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenReturn(response);

        PaymentResponse result = controller.processPayment(request);

        assertEquals("SUCCESS", result.getStatus());

        verify(paymentService).processPayment(request);
    }

    // =========================================================
    // PAYMENT STATUS
    // =========================================================

    /**
     * @brief Verifies retrieval of payment status by transaction ID.
     */
    @Test
    void shouldReturnPaymentStatus() {

        PaymentStatus status = new PaymentStatus();
        status.setStatus("COMPLETED");

        when(paymentService.getPaymentStatus("TXN123"))
                .thenReturn(status);

        PaymentStatus result = controller.getPaymentStatus("TXN123");

        assertEquals("COMPLETED", result.getStatus());

        verify(paymentService).getPaymentStatus("TXN123");
    }

    // =========================================================
    // FAKE PAYMENT FLOW
    // =========================================================

    /**
     * @brief Verifies successful fake payment flow.
     */
    @Test
    void shouldHandleFakePaymentSuccess() {

        Appointment appointment = new Appointment();

        PaymentResponse response = new PaymentResponse();
        response.setStatus("SUCCESS");
        response.setTransactionId("TXN123");

        when(appointmentRepository.findById(1)).thenReturn(appointment);
        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenReturn(response);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String result = controller.fakePayment(1, 85.0f, redirectAttributes);

        assertEquals("redirect:/payment/confirmation/1", result);

        assertTrue(appointment.isPaid());

        verify(appointmentRepository).save(appointment);
        verify(redirectAttributes)
                .addFlashAttribute(eq("message"), anyString());
    }

    /**
     * @brief Verifies fake payment failure handling.
     */
    @Test
    void shouldHandleFakePaymentFailure() {

        Appointment appointment = new Appointment();

        PaymentResponse response = new PaymentResponse();
        response.setStatus("FAILED");

        when(appointmentRepository.findById(1)).thenReturn(appointment);
        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenReturn(response);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String result = controller.fakePayment(1, 85.0f, redirectAttributes);

        assertEquals("redirect:/payment/confirmation/1", result);

        verify(appointmentRepository, never()).save(any());
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), anyString());
    }

    /**
     * @brief Verifies fake payment behavior when appointment is missing.
     */
    @Test
    void shouldHandleAppointmentNotFoundInFakePayment() {

        when(appointmentRepository.findById(1)).thenReturn(null);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String result = controller.fakePayment(1, 85.0f, redirectAttributes);

        assertEquals("redirect:/patient/appointments", result);

        verify(appointmentRepository, never()).save(any());
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), anyString());
    }

    // =========================================================
    // CONFIRMATION PAGE
    // =========================================================

    /**
     * @brief Verifies payment confirmation page rendering.
     */
    @Test
    void shouldReturnConfirmationPage() {

        Appointment appointment = new Appointment();

        when(appointmentRepository.findById(1)).thenReturn(appointment);

        Model model = mock(Model.class);

        String view = controller.paymentConfirmation(1, model);

        assertEquals("payment/confirmation", view);

        verify(model).addAttribute("appointmentId", 1);
        verify(model).addAttribute("appointment", appointment);
    }
}