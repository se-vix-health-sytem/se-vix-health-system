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

class PaymentControllerTest {

    private PaymentService paymentService;
    private JsonAppointmentRepository appointmentRepository;
    private PaymentController controller;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        appointmentRepository = mock(JsonAppointmentRepository.class);
        controller = new PaymentController(paymentService, appointmentRepository);
    }

    //  SHOW PAYMENT PAGE

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

    @Test
    void shouldRedirectWhenAppointmentNotFound() {
        when(appointmentRepository.findById(1)).thenReturn(null);

        Model model = mock(Model.class);

        String view = controller.showPaymentPage(1, model);

        assertEquals("redirect:/patient/appointments?error=Appointment+not+found", view);
        verify(paymentService, never()).getAppointmentCost(anyInt());
    }

    //  PROCESS PAYMENT

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

    //  PAYMENT STATUS

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

    //  FAKE PAYMENT

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
        assertTrue(appointment.isPaymentStatus());

        verify(appointmentRepository).save(appointment);
        verify(redirectAttributes).addFlashAttribute(eq("message"), anyString());
    }

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
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void shouldHandleAppointmentNotFoundInFakePayment() {
        when(appointmentRepository.findById(1)).thenReturn(null);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String result = controller.fakePayment(1, 85.0f, redirectAttributes);

        assertEquals("redirect:/patient/appointments", result);

        verify(appointmentRepository, never()).save(any());
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    //  CONFIRMATION

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