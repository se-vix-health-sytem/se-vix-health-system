package com.nvivx.vixhealthsystem.controllers.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @brief Controller for public legal and compliance pages — no base URL prefix.
 *
 * Serves the static legal documents required by Italian and EU law: privacy
 * policy, GDPR notice, terms of use, and accessibility statement.  Each
 * handler simply routes to the corresponding Thymeleaf fragment; no dynamic
 * data is needed.
 */
@Controller
public class LegalController {

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /privacy — render the privacy policy page.
     * @return Thymeleaf template {@code fragments/footer/privacy-policy}.
     */
    @GetMapping("/privacy")
    public String privacy() { return "fragments/footer/privacy-policy"; }

    /**
     * GET /gdpr — render the GDPR data processing notice.
     * @return Thymeleaf template {@code fragments/footer/gdpr}.
     */
    @GetMapping("/gdpr")
    public String gdpr() { return "fragments/footer/gdpr"; }

    /**
     * GET /terms — render the terms of use page.
     * @return Thymeleaf template {@code fragments/footer/terms-of-use}.
     */
    @GetMapping("/terms")
    public String terms() { return "fragments/footer/terms-of-use"; }

    /**
     * GET /accessibility — render the accessibility statement.
     * @return Thymeleaf template {@code fragments/footer/accessibility}.
     */
    @GetMapping("/accessibility")
    public String accessibility() { return "fragments/footer/accessibility"; }
}
