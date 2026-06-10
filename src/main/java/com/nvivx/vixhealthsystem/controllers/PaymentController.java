package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.dto.PaymentRequest;
import com.nvivx.vixhealthsystem.dto.PaymentResponse;
import com.nvivx.vixhealthsystem.dto.PaymentStatus;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.integration.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final JsonAppointmentRepository appointmentRepository;  // ADD THIS

    public PaymentController(PaymentService paymentService,
                             JsonAppointmentRepository appointmentRepository) {  // ADD TO CONSTRUCTOR
        this.paymentService = paymentService;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/appointment/{appointmentId}")
    public String showPaymentPage(@PathVariable int appointmentId, Model model) {
        // Check if appointment exists
        var appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            return "redirect:/patient/appointments?error=Appointment+not+found";
        }

        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("amount", paymentService.getAppointmentCost(appointmentId));
        model.addAttribute("pageTitle", "Complete Payment");
        return "payment/payment-form";
    }

    @PostMapping("/process")
    @ResponseBody
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    @GetMapping("/status/{paymentId}")
    @ResponseBody
    public PaymentStatus getPaymentStatus(@PathVariable String paymentId) {
        return paymentService.getPaymentStatus(paymentId);
    }

    @PostMapping("/fake-pay")
    public String fakePayment(@RequestParam int appointmentId,
                              @RequestParam float amount,
                              RedirectAttributes redirectAttributes) {
        // Check if appointment exists
        var appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            redirectAttributes.addFlashAttribute("error", "Appointment not found");
            return "redirect:/patient/appointments";
        }

        // Simple fake payment for demo
        PaymentRequest request = new PaymentRequest();
        request.setAppointmentId(appointmentId);
        request.setAmount(amount);
        request.setCardNumber("4111111111111111");
        request.setCvv("123");
        request.setExpiryDate("12/25");

        PaymentResponse response = paymentService.processPayment(request);

        if ("SUCCESS".equals(response.getStatus())) {
            // Update appointment payment status
            appointment.setPaymentStatus(true);
            appointmentRepository.save(appointment);

            redirectAttributes.addFlashAttribute("message",
                    "✅ Payment of €" + amount + " successful! Transaction ID: " + response.getTransactionId());
        } else {
            redirectAttributes.addFlashAttribute("error", "Payment failed: " + response.getMessage());
        }

        return "redirect:/payment/confirmation/" + appointmentId;
    }
}