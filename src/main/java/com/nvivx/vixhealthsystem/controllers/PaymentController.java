// src/main/java/com/nvivx/vixhealthsystem/controllers/PaymentController.java
package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.dto.PaymentRequest;
import com.nvivx.vixhealthsystem.dto.PaymentResponse;
import com.nvivx.vixhealthsystem.dto.PaymentStatus;
import com.nvivx.vixhealthsystem.service.integration.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/appointment/{appointmentId}")
    public String showPaymentPage(@PathVariable int appointmentId, Model model) {
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
        // Simple fake payment for demo
        PaymentRequest request = new PaymentRequest();
        request.setAppointmentId(appointmentId);
        request.setAmount(amount);
        request.setCardNumber("4111111111111111");
        request.setCvv("123");
        request.setExpiryDate("12/25");

        PaymentResponse response = paymentService.processPayment(request);

        if ("SUCCESS".equals(response.getStatus())) {
            redirectAttributes.addFlashAttribute("message",
                    "✅ Payment of €" + amount + " successful! Transaction ID: " + response.getTransactionId());
        } else {
            redirectAttributes.addFlashAttribute("error", "Payment failed: " + response.getMessage());
        }

        return "redirect:/payment/confirmation/" + appointmentId;
    }
}