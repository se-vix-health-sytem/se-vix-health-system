package com.nvivx.vixhealthsystem.controllers.services;

import com.nvivx.vixhealthsystem.dto.PaymentRequest;
import com.nvivx.vixhealthsystem.dto.PaymentResponse;
import com.nvivx.vixhealthsystem.dto.PaymentStatus;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.integration.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @brief Controller for the payment workflow — base URL {@code /payment}.
 *
 * Manages the end-to-end payment flow for appointment fees: showing the
 * payment form, processing a real card payment via {@link PaymentService},
 * querying transaction status, and a fake-pay shortcut for demo/test purposes
 * that simulates a successful transaction without real card data.  After
 * a successful payment the appointment status is set to CONFIRMED.
 *
 * @see PaymentService
 * @see JsonAppointmentRepository
 */
@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final JsonAppointmentRepository appointmentRepository;

    public PaymentController(PaymentService paymentService,
                             JsonAppointmentRepository appointmentRepository) {
        this.paymentService = paymentService;
        this.appointmentRepository = appointmentRepository;
    }

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /payment/appointment/{appointmentId} — render the payment form for an appointment.
     *
     * Looks up the appointment to confirm it exists before presenting the
     * payment form; redirects back to the appointments list if it does not.
     *
     * @param appointmentId  Numeric ID of the appointment to pay for.
     * @param model          Receives {@code appointmentId}, {@code amount}, and {@code pageTitle}.
     * @return               Thymeleaf template {@code payment/payment-form}, or
     *                       redirect to {@code /patient/appointments?error=...} when
     *                       the appointment is not found.
     */
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

    /**
     * GET /payment/status/{paymentId} — fetch the current status of a payment transaction.
     *
     * @param paymentId  Transaction identifier returned by the payment gateway.
     * @return           {@link PaymentStatus} JSON body with status and any message.
     */
    @GetMapping("/status/{paymentId}")
    @ResponseBody
    public PaymentStatus getPaymentStatus(@PathVariable String paymentId) {
        return paymentService.getPaymentStatus(paymentId);
    }

    /**
     * GET /payment/confirmation/{appointmentId} — render the post-payment confirmation page.
     *
     * @param appointmentId  Numeric ID of the appointment that was just paid.
     * @param model          Receives {@code appointmentId}, {@code appointment}, and {@code pageTitle}.
     * @return               Thymeleaf template {@code payment/confirmation}.
     */
    @GetMapping("/confirmation/{appointmentId}")
    public String paymentConfirmation(@PathVariable int appointmentId, Model model) {
        var appointment = appointmentRepository.findById(appointmentId);
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("appointment", appointment);
        model.addAttribute("pageTitle", "Payment Confirmation");
        return "payment/confirmation";
    }

    // =========================================================
    // POST HANDLERS
    // =========================================================

    /**
     * POST /payment/process — process a card payment via the payment service.
     *
     * Accepts a full {@link PaymentRequest} (card number, CVV, expiry, amount)
     * and delegates to {@link PaymentService#processPayment}.  Returns the
     * gateway response as JSON; the caller is responsible for acting on the
     * status field.
     *
     * @param request  JSON body containing payment and appointment details.
     * @return         {@link PaymentResponse} JSON body with status, transaction ID,
     *                 and any error message.
     */
    @PostMapping("/process")
    @ResponseBody
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    /**
     * POST /payment/fake-pay — simulate a successful payment for demo purposes.
     *
     * Builds a hard-coded {@link PaymentRequest} with a test card number and
     * calls the real payment service path so that the same confirmation and
     * status-update logic runs as in a live transaction.  On success the
     * appointment is marked as paid and CONFIRMED before redirecting.
     *
     * @param appointmentId       Numeric ID of the appointment to mark as paid.
     * @param amount              Amount to simulate; shown in the success flash message.
     * @param redirectAttributes  Flash attributes carrying a success {@code message}
     *                            or an {@code error} back to the confirmation page.
     * @return                    Redirect to {@code /payment/confirmation/{appointmentId}}.
     */
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
            // Update appointment payment status and confirm it
            appointment.pay();
            appointmentRepository.save(appointment);

            redirectAttributes.addFlashAttribute("message",
                    "✅ Payment of €" + amount + " successful! Transaction ID: " + response.getTransactionId());
        } else {
            redirectAttributes.addFlashAttribute("error", "Payment failed: " + response.getMessage());
        }

        return "redirect:/payment/confirmation/" + appointmentId;
    }
}
