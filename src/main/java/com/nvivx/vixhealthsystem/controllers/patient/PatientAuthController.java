package com.nvivx.vixhealthsystem.controllers.patient;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

/**
 * @brief Controller for patient authentication, dashboard, profile, and account management : base URL {@code /patient}.
 *
 * Patients authenticate using their Italian fiscal code (codice fiscale) either
 * directly through the portal form or via the mock SPID/CIE government-identity
 * flow in {@link MockSpidController}.  On successful authentication a patient
 * object is stored in the HTTP session and a Spring Security context is
 * registered so that route-level access rules work correctly.  Every significant
 * action (login, logout, deletion) is written to the audit log.
 *
 * @see MockSpidController
 * @see PatientService
 * @see AuditService
 */
@Controller
@RequestMapping("/patient")
public class PatientAuthController {

    private final PatientService patientService;
    private final AuditService auditService;

    public PatientAuthController(PatientService patientService, AuditService auditService) {
        this.patientService = patientService;
        this.auditService = auditService;
    }

    // =========================================================
    // LOGIN / LOGOUT
    // =========================================================

    /**
     * GET /patient/login : render the patient login page.
     *
     * @param redirect  Optional relative path to redirect to after a successful
     *                  login; stored in the model for the login form to forward.
     * @param model     Receives {@code redirectUrl}.
     * @return          Thymeleaf template {@code patient/login}.
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirectUrl", redirect != null ? redirect : "dashboard");
        return "patient/login";
    }

    /**
     * POST /patient/authenticate : log in a patient using their fiscal code.
     *
     * Looks up the patient by fiscal code, stores the entity in the HTTP session
     * under the {@code "patient"} key, registers a Spring Security context so
     * that route guards work, and audits the login event.  Redirects to an
     * optional custom URL or to the dashboard by default.
     *
     * @param fiscalCode   Italian fiscal code used as the patient's identity.
     * @param redirectUrl  Optional relative path (without {@code /patient/}) to
     *                     redirect to after login; defaults to {@code dashboard}.
     * @param session      HTTP session; receives {@code "patient"} and {@code "role"} attributes.
     * @param request      Raw servlet request (passed to Spring Security plumbing).
     * @param response     Raw servlet response (passed to Spring Security plumbing).
     * @param model        Receives an {@code error} attribute on failed login.
     * @return             Redirect to the target URL, or {@code patient/login} with an error.
     */
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
     * GET /patient/logout : invalidate the patient session and redirect to the login page.
     *
     * @param session  HTTP session to invalidate.
     * @return         Redirect to {@code /patient/login}.
     */
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

    // =========================================================
    // SPID / CIE CALLBACKS
    // =========================================================

    /**
     * GET /patient/spid-callback : receive the fiscal code from the mock SPID portal and create a session.
     *
     * The fiscal code is treated as the identity assertion token (in production
     * this would be a signed SAML response).  Delegates to
     * {@link #createPatientSession} which is shared with the CIE flow.
     *
     * @param fiscalCode  Fiscal code passed as a query parameter by the mock SPID portal.
     * @param session     HTTP session to populate.
     * @param request     Raw servlet request (retained for future filter hooks).
     * @param response    Raw servlet response (retained for future filter hooks).
     * @param model       Receives an {@code error} attribute on lookup failure.
     * @return            Redirect to {@code /patient/dashboard}, or {@code patient/login}
     *                    with an error on failure.
     */
    @GetMapping("/spid-callback")
    public String spidCallback(@RequestParam String fiscalCode,
                               @RequestParam(required = false) String redirect,
                               HttpSession session,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Model model) {
        return createPatientSession(fiscalCode, redirect, session, model);
    }

    /**
     * GET /patient/cie-callback : receive the fiscal code from the mock CIE portal and create a session.
     *
     * Identical flow to the SPID callback; both ultimately call
     * {@link #createPatientSession}.
     *
     * @param fiscalCode  Fiscal code passed by the mock CIE portal.
     * @param session     HTTP session to populate.
     * @param request     Raw servlet request (retained for future filter hooks).
     * @param response    Raw servlet response (retained for future filter hooks).
     * @param model       Receives an {@code error} attribute on lookup failure.
     * @return            Redirect to {@code /patient/dashboard}, or {@code patient/login}
     *                    with an error on failure.
     */
    @GetMapping("/cie-callback")
    public String cieCallback(@RequestParam String fiscalCode,
                              @RequestParam(required = false) String redirect,
                              HttpSession session,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {
        return createPatientSession(fiscalCode, redirect, session, model);
    }

    // =========================================================
    // DASHBOARD, PROFILE, RECORDS
    // =========================================================

    /**
     * GET /patient/dashboard : render the patient's personal dashboard.
     *
     * Reloads a fresh Patient entity from the database so that any session
     * staleness does not affect the data displayed (e.g., newly booked
     * appointments added by another session or by a secretary).
     *
     * @param session  HTTP session; must contain a {@code "patient"} attribute.
     * @param model    Receives {@code patient} and {@code pageTitle} attributes.
     * @return         Thymeleaf template {@code patient/dashboard}, or
     *                 {@code redirect:/patient/login} when no session patient is found.
     */
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

    /**
     * GET /patient/profile : display the patient's personal profile.
     *
     * @param session  HTTP session; must contain a {@code "patient"} attribute.
     * @param model    Receives {@code patient} and {@code pageTitle} attributes.
     * @return         Thymeleaf template {@code patient/profile}, or
     *                 {@code redirect:/patient/login} when no session patient is found.
     */
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

    /**
     * GET /patient/records : display the patient's medical records.
     *
     * Reloads from the database to ensure that the medical record and its
     * collections (diagnoses, prescriptions, etc.) are attached to an active
     * Hibernate session and load correctly without lazy-loading exceptions.
     *
     * @param session  HTTP session; must contain a {@code "patient"} attribute.
     * @param model    Receives {@code patient} and {@code pageTitle} attributes.
     * @return         Thymeleaf template {@code patient/records}, or
     *                 {@code redirect:/patient/login} when no session patient is found.
     */
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

    /**
     * GET /patient/profile/edit : show the profile edit form pre-filled with current data.
     */
    @GetMapping("/profile/edit")
    public String editProfile(HttpSession session, Model model) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) return "redirect:/patient/login";
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "Edit Profile");
        model.addAttribute("currentPage", "profile");
        return "patient/profile-edit";
    }

    /**
     * POST /patient/profile/update : apply profile changes and refresh the session patient.
     */
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String surname,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phoneNumber,
                                @RequestParam(required = false) String birthPlace,
                                @RequestParam(required = false) String birthDate,
                                @RequestParam(required = false) String gender,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) return "redirect:/patient/login";

        Patient carrier = new Patient();
        carrier.setName(name);
        carrier.setSurname(surname);
        carrier.setEmail(email != null && email.isBlank() ? null : email);
        carrier.setPhoneNumber(phoneNumber != null && phoneNumber.isBlank() ? null : phoneNumber);
        carrier.setBirthPlace(birthPlace);

        if (birthDate != null && !birthDate.isBlank()) {
            try { carrier.setBirthDate(LocalDate.parse(birthDate)); }
            catch (DateTimeParseException ignored) {}
        }

        if (gender != null && !gender.isBlank()) {
            carrier.setGender(gender.charAt(0));
        }

        Patient updated = patientService.updatePatient(patient.getId(), carrier);
        session.setAttribute("patient", updated);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/patient/profile";
    }

    // =========================================================
    // ACCOUNT DELETION
    // =========================================================

    /**
     * POST /patient/delete : anonymize and delete the patient's account.
     *
     * Requires the patient to submit the string {@code "CONFIRM"} as the
     * {@code confirm} parameter; any other value (or an absent value) renders
     * a confirmation page instead.  Medical records are retained for five years
     * per GDPR Article 17(3)(c) even though personal identifiers are anonymized.
     *
     * @param confirm  Must equal {@code "CONFIRM"} (case-sensitive) to proceed.
     * @param session  HTTP session; invalidated on successful deletion.
     * @param model    Receives {@code requireConfirm=true} when confirmation is needed,
     *                 or a {@code message} attribute on success.
     * @return         Redirect to {@code /} on success, the {@code patient/confirm-delete}
     *                 template when confirmation is pending, or
     *                 {@code redirect:/patient/login} when no session patient is found.
     */
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

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Shared session-creation logic used by both the SPID and CIE callbacks.
     *
     * Looks up the patient by fiscal code, populates the session attributes,
     * registers a Spring Security context, and audits the login event.
     *
     * @param fiscalCode  Fiscal code received from the identity provider callback;
     *                    trimmed and uppercased before the database lookup.
     * @param session     HTTP session to populate with {@code "patient"} and {@code "role"}.
     * @param model       Receives an {@code error} attribute on lookup failure.
     * @return            Redirect to {@code /patient/dashboard} on success,
     *                    or {@code patient/login} with an error message on failure.
     */
    private String createPatientSession(String fiscalCode, String redirect, HttpSession session, Model model) {
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

        if (redirect != null && !redirect.isBlank() && !redirect.equals("dashboard")) {
            return "redirect:/patient/" + redirect;
        }
        return "redirect:/patient/dashboard";
    }

    /**
     * Retrieve the logged-in patient from the HTTP session.
     *
     * @param session  HTTP session that may contain a {@code "patient"} attribute.
     * @return         The session {@link Patient}, or {@code null} when absent.
     */
    private Patient getLoggedInPatient(HttpSession session) {
        return (Patient) session.getAttribute("patient");
    }
}
