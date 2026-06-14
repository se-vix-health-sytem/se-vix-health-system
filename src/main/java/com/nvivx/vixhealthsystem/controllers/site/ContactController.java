package com.nvivx.vixhealthsystem.controllers.site;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @brief Controller for the public contact form — no base URL prefix.
 *
 * Presents a simple contact form and handles its submission.  In the current
 * implementation the message is logged to the application log rather than
 * forwarded to an email service; this is a deliberate placeholder that keeps
 * the demo self-contained without requiring SMTP configuration.
 */
@Controller
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /contact — render the contact form page.
     *
     * @param model  Receives {@code pageTitle}.
     * @return       Thymeleaf template {@code site/contact}.
     */
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact Us");
        return "site/contact";
    }

    // =========================================================
    // POST HANDLERS
    // =========================================================

    /**
     * POST /contact/submit — log a contact-form submission and redirect with a confirmation.
     *
     * The message content is currently written to the application log only.
     * A real deployment would forward the message to the hospital's support
     * mailbox here instead.  The redirect-after-POST pattern prevents duplicate
     * submissions on browser reload.
     *
     * @param name               Submitter's display name.
     * @param email              Submitter's email address for follow-up.
     * @param message            Free-text message body.
     * @param redirectAttributes Flash attribute {@code successMessage} shown on the
     *                           contact page after the redirect.
     * @return                   Redirect to {@code /contact}.
     */
    @PostMapping("/contact/submit")
    public String submitContact(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String message,
                                RedirectAttributes redirectAttributes) {
        log.info("Contact form submission from {} <{}>: {}", name, email, message);
        redirectAttributes.addFlashAttribute("successMessage", "Thank you for your message! We'll get back to you soon.");
        return "redirect:/contact";
    }
}
