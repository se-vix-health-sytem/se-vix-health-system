package com.nvivx.vixhealthsystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/select-role")
    public String processLogin(@RequestParam String role) {
        // NO real authentication, just redirect based on role choice
        switch (role) {
            case "DOCTOR":
                return "redirect:/doctor/dashboard";
            case "TECHNICIAN":
                return "redirect:/technician/dashboard";
            case "STAFF_MANAGER":
                return "redirect:/staff-manager/dashboard";
            default:
                return "redirect:/login";
        }
    }
}
