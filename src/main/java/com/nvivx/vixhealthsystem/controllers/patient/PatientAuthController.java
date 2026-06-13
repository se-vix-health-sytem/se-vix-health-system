package com.nvivx.vixhealthsystem.controllers.patient;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientAuthController {

    private final PatientService patientService;
    private final AuditService auditService;

    public PatientAuthController(PatientService patientService, AuditService auditService) {
        this.patientService = patientService;
        this.auditService = auditService;
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
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Model model) {
        var patientOpt = patientService.findByFiscalCode(fiscalCode);

        if (patientOpt.isEmpty()) {
            model.addAttribute("error", "Invalid fiscal code. Please try again.");
            return "patient/login";
        }

        Patient patient = patientOpt.get();
        session.removeAttribute("user");
        session.setAttribute("patient", patient);
        session.setAttribute("role", "PATIENT");

        // Register with Spring Security so route-level protection works
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken(
            fiscalCode, null, List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
        ));
        SecurityContextHolder.setContext(ctx);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);
        auditService.log("PATIENT_LOGIN", "Patient", String.valueOf(patient.getId()),
            "Patient " + fiscalCode + " logged in");

        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            return "redirect:/patient/" + redirectUrl;
        }
        return "redirect:/patient/dashboard";
    }

    /**
     * Callback from the mock SPID portal — fiscalCode is the "assertion token".
     * Creates the patient session exactly like the direct authenticate endpoint.
     */
    @GetMapping("/spid-callback")
    public String spidCallback(@RequestParam String fiscalCode,
                               HttpSession session,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Model model) {
        return createPatientSession(fiscalCode, session, model);
    }

    /**
     * Callback from the mock CIE portal — same flow as SPID.
     */
    @GetMapping("/cie-callback")
    public String cieCallback(@RequestParam String fiscalCode,
                              HttpSession session,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {
        return createPatientSession(fiscalCode, session, model);
    }

    private String createPatientSession(String fiscalCode, HttpSession session, Model model) {
        var patientOpt = patientService.findByFiscalCode(fiscalCode.trim().toUpperCase());
        if (patientOpt.isEmpty()) {
            model.addAttribute("error", "Autenticazione fallita. Riprova.");
            return "patient/login";
        }
        Patient patient = patientOpt.get();
        session.removeAttribute("user");
        session.setAttribute("patient", patient);
        session.setAttribute("role", "PATIENT");

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken(
            fiscalCode, null, List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
        ));
        SecurityContextHolder.setContext(ctx);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);
        auditService.log("PATIENT_LOGIN", "Patient", String.valueOf(patient.getId()),
            "Patient " + fiscalCode + " logged in");
        return "redirect:/patient/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }
        patient = patientService.findById(patient.getId());
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "Patient Dashboard");
        model.addAttribute("currentPage", "dashboard");
        return "patient/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Patient patient = getLoggedInPatient(session);
        if (patient != null) {
            auditService.log("PATIENT_LOGOUT", "Patient", String.valueOf(patient.getId()),
                "Patient " + patient.getFiscalCode() + " logged out");
        }
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/patient/login";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "My Profile");
        model.addAttribute("currentPage", "profile");
        return "patient/profile";
    }

    @GetMapping("/records")
    public String records(HttpSession session, Model model) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }
        // Reload from DB so medicalRecord and its collections are attached to the current Hibernate session
        patient = patientService.findById(patient.getId());
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "My Medical Records");
        model.addAttribute("currentPage", "records");
        return "patient/records";
    }

    @PostMapping("/delete")
    public String deleteAccount(@RequestParam(required = false) String confirm,
                                HttpSession session,
                                Model model) {
        Patient patient = getLoggedInPatient(session);
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

        model.addAttribute("message", "Your account has been successfully anonymized and deleted.");
        return "redirect:/";
    }

    private Patient getLoggedInPatient(HttpSession session) {
        return (Patient) session.getAttribute("patient");
    }
}