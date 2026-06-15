package com.nvivx.vixhealthsystem.controllers.patient;

import com.nvivx.vixhealthsystem.service.core.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @brief Simulates SPID and CIE government-identity login pages for demo purposes.
 *
 * In a production deployment these endpoints would be replaced by redirects to
 * the real Italian government identity providers (SPID and CIE).  Here they
 * host simplified mock login pages that accept a patient's fiscal code as the
 * "assertion token", perform minimal validation, and then hand off to the real
 * patient auth callback in {@link PatientAuthController}.
 *
 * Auth flow:
 * {@code /patient/login} → {@code /mock-spid/login} → POST {@code /mock-spid/authenticate}
 *                        → redirect {@code /patient/spid-callback?fiscalCode=...}
 *                       → {@code /mock-cie/login}  → POST {@code /mock-cie/authenticate}
 *                        → redirect {@code /patient/cie-callback?fiscalCode=...}
 *
 * These endpoints are public (no authentication required) because they are the
 * entry point to the login flow.
 *
 * @see PatientAuthController
 * @see PatientService
 */
@Controller
public class MockSpidController {

    private final PatientService patientService;

    public MockSpidController(PatientService patientService) {
        this.patientService = patientService;
    }

    // =========================================================
    // SPID HANDLERS
    // =========================================================

    /**
     * GET /mock-spid/login — render the mock SPID login form.
     *
     * @return Thymeleaf template {@code mock-spid/login}.
     */
    @GetMapping("/mock-spid/login")
    public String spidLogin(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "mock-spid/login";
    }

    /**
     * POST /mock-spid/authenticate — validate the fiscal code and PIN, then redirect to the SPID callback.
     *
     * Checks that the fiscal code is exactly 16 characters, the PIN is at least
     * 6 digits long, and the patient exists in the database.  On success the
     * fiscal code is passed as a query parameter to the patient auth callback,
     * where it acts as the identity assertion token.
     *
     * @param fiscalCode  Italian fiscal code (codice fiscale); trimmed and uppercased.
     * @param pin         Numeric PIN; must be at least 6 digits.
     * @param model       Receives an {@code error} attribute on validation failure.
     * @return            Redirect to {@code /patient/spid-callback?fiscalCode=...} on success,
     *                    or the {@code mock-spid/login} template with an error message.
     */
    @PostMapping("/mock-spid/authenticate")
    public String spidAuthenticate(@RequestParam String fiscalCode,
                                   @RequestParam String pin,
                                   @RequestParam(required = false) String redirect,
                                   Model model) {
        String fc = fiscalCode.trim().toUpperCase();

        if (fc.length() != 16) {
            model.addAttribute("error", "Codice fiscale non valido. Deve essere di 16 caratteri.");
            model.addAttribute("redirect", redirect);
            return "mock-spid/login";
        }
        if (pin == null || pin.isBlank() || pin.length() < 6) {
            model.addAttribute("error", "PIN non valido. Inserisci almeno 6 cifre.");
            model.addAttribute("redirect", redirect);
            return "mock-spid/login";
        }

        if (patientService.findByFiscalCode(fc).isEmpty()) {
            model.addAttribute("error", "Nessun account trovato per il codice fiscale inserito.");
            model.addAttribute("redirect", redirect);
            return "mock-spid/login";
        }

        String redirectParam = (redirect != null && !redirect.isBlank()) ? "&redirect=" + redirect : "";
        return "redirect:/patient/spid-callback?fiscalCode=" + fc + redirectParam;
    }

    // =========================================================
    // CIE HANDLERS
    // =========================================================

    /**
     * GET /mock-cie/login — render the mock CIE login form.
     *
     * @return Thymeleaf template {@code mock-cie/login}.
     */
    @GetMapping("/mock-cie/login")
    public String cieLogin(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "mock-cie/login";
    }

    /**
     * POST /mock-cie/authenticate — validate the fiscal code and PIN, then redirect to the CIE callback.
     *
     * Follows the same validation rules as the SPID flow but without a PIN
     * length check (CIE uses a different credential scheme in production).
     *
     * @param fiscalCode  Italian fiscal code; trimmed and uppercased.
     * @param pin         PIN value; currently only presence is validated for CIE.
     * @param model       Receives an {@code error} attribute on validation failure.
     * @return            Redirect to {@code /patient/cie-callback?fiscalCode=...} on success,
     *                    or the {@code mock-cie/login} template with an error message.
     */
    @PostMapping("/mock-cie/authenticate")
    public String cieAuthenticate(@RequestParam String fiscalCode,
                                  @RequestParam String pin,
                                  @RequestParam(required = false) String redirect,
                                  Model model) {
        String fc = fiscalCode.trim().toUpperCase();

        if (fc.length() != 16) {
            model.addAttribute("error", "Codice fiscale non valido. Deve essere di 16 caratteri.");
            model.addAttribute("redirect", redirect);
            return "mock-cie/login";
        }

        if (patientService.findByFiscalCode(fc).isEmpty()) {
            model.addAttribute("error", "Nessun account trovato per il codice fiscale inserito.");
            model.addAttribute("redirect", redirect);
            return "mock-cie/login";
        }

        String redirectParam = (redirect != null && !redirect.isBlank()) ? "&redirect=" + redirect : "";
        return "redirect:/patient/cie-callback?fiscalCode=" + fc + redirectParam;
    }
}
