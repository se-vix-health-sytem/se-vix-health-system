package com.nvivx.vixhealthsystem.controllers.site;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact Us");
        return "site/contact";  // Add "site/" prefix
    }

    @PostMapping("/contact/submit")
    public String submitContact(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String message,
                                RedirectAttributes redirectAttributes) {
        System.out.println("=== CONTACT FORM ===");
        System.out.println("From: " + name + " <" + email + ">");
        System.out.println("Message: " + message);
        System.out.println("==================");

        redirectAttributes.addFlashAttribute("successMessage", "Thank you for your message! We'll get back to you soon.");
        return "redirect:/contact";
    }
}