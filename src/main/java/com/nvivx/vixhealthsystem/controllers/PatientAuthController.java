package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.person.Patient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/patient")
public class PatientAuthController {

    private final MockDatabase mockDatabase;

    public PatientAuthController(MockDatabase mockDatabase) {
        this.mockDatabase = mockDatabase;
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirectUrl", redirect != null ? redirect : "dashboard");
        return "patient/login";
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam String fiscalCode,
                               @RequestParam(required = false) String redirectUrl,
                               HttpSession session,
                               Model model) {
        Patient patient = mockDatabase.findPatientByFiscalCode(fiscalCode);

        if (patient == null) {
            model.addAttribute("error", "Invalid fiscal code. Please try again.");
            return "patient/login";
        }

        session.setAttribute("patient", patient);

        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            return "redirect:/patient/" + redirectUrl;
        }
        return "redirect:/patient/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("patient");
        if (patient == null) {
            return "redirect:/patient/login";
        }
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "Patient Dashboard");
        return "patient/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("patient");
        if (patient == null) {
            return "redirect:/patient/login";
        }
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "My Profile");
        return "patient/profile";
    }

    @GetMapping("/records")
    public String records(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("patient");
        if (patient == null) {
            return "redirect:/patient/login";
        }
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "My Medical Records");
        return "patient/records";
    }

    @PostMapping("/delete")
    public String deleteAccount(@RequestParam(required = false) String confirm,
                                HttpSession session,
                                Model model) {
        Patient patient = (Patient) session.getAttribute("patient");
        if (patient == null) {
            return "redirect:/patient/login";
        }

        if (!"CONFIRM".equals(confirm)) {
            model.addAttribute("requireConfirm", true);
            model.addAttribute("patient", patient);
            return "patient/confirm-delete";
        }

        patientService.deletePatient(patient.getId());
        session.invalidate();

        model.addAttribute("message", "Your account has been successfully deleted.");
        return "redirect:/";
    }

}