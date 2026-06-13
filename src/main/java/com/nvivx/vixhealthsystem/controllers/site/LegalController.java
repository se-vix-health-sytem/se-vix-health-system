package com.nvivx.vixhealthsystem.controllers.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/privacy")
    public String privacy() { return "fragments/footer/privacy-policy"; }

    @GetMapping("/gdpr")
    public String gdpr() { return "fragments/footer/gdpr"; }

    @GetMapping("/terms")
    public String terms() { return "fragments/footer/terms-of-use"; }

    @GetMapping("/accessibility")
    public String accessibility() { return "fragments/footer/accessibility"; }
}
