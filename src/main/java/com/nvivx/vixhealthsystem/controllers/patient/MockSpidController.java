package com.nvivx.vixhealthsystem.controllers.patient;

import com.nvivx.vixhealthsystem.service.core.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Simulates SPID and CIE identity provider portals for demo purposes.
 * In production these would be real external redirects; here we host
 * look-alike pages ourselves and redirect back to the patient callback.
 *
 * Flow:
 *   /patient/login  →  /mock-spid/login  →  POST /mock-spid/authenticate
 *                                         →  redirect /patient/spid-callback?fiscalCode=...
 *                  →  /mock-cie/login    →  POST /mock-cie/authenticate
 *                                         →  redirect /patient/cie-callback?fiscalCode=...
 */
@Controller
public class MockSpidController {

    private final PatientService patientService;

    public MockSpidController(PatientService patientService) {
        this.patientService = patientService;
    }

    // ── SPID ────────────────────────────────────────────────────────────────

    @GetMapping("/mock-spid/login")
    public String spidLogin() {
        return "mock-spid/login";
    }

    @PostMapping("/mock-spid/authenticate")
    public String spidAuthenticate(@RequestParam String fiscalCode,
                                   @RequestParam String pin,
                                   Model model) {
        String fc = fiscalCode.trim().toUpperCase();

        if (fc.length() != 16) {
            model.addAttribute("error", "Codice fiscale non valido. Deve essere di 16 caratteri.");
            return "mock-spid/login";
        }
        if (pin == null || pin.isBlank() || pin.length() < 6) {
            model.addAttribute("error", "PIN non valido. Inserisci almeno 6 cifre.");
            return "mock-spid/login";
        }

        if (patientService.findByFiscalCode(fc).isEmpty()) {
            model.addAttribute("error", "Nessun account trovato per il codice fiscale inserito.");
            return "mock-spid/login";
        }

        // Hand off to the patient auth callback — fiscalCode acts as the "token"
        return "redirect:/patient/spid-callback?fiscalCode=" + fc;
    }

    // ── CIE ─────────────────────────────────────────────────────────────────

    @GetMapping("/mock-cie/login")
    public String cieLogin() {
        return "mock-cie/login";
    }

    @PostMapping("/mock-cie/authenticate")
    public String cieAuthenticate(@RequestParam String fiscalCode,
                                  @RequestParam String pin,
                                  Model model) {
        String fc = fiscalCode.trim().toUpperCase();

        if (fc.length() != 16) {
            model.addAttribute("error", "Codice fiscale non valido. Deve essere di 16 caratteri.");
            return "mock-cie/login";
        }

        if (patientService.findByFiscalCode(fc).isEmpty()) {
            model.addAttribute("error", "Nessun account trovato per il codice fiscale inserito.");
            return "mock-cie/login";
        }

        return "redirect:/patient/cie-callback?fiscalCode=" + fc;
    }
}
