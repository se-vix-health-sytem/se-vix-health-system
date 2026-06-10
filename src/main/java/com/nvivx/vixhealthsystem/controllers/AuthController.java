package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final EmployeeService employeeService;
    private final PatientService patientService;

    public AuthController(EmployeeService employeeService, com.nvivx.vixhealthsystem.service.core.PatientService patientService) {
        this.employeeService = employeeService;
        this.patientService = patientService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid credentials");
        }
        return "login";
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        // DEMO authentication - NO REAL PASSWORD CHECK
        // In production, this would use external IDP (SPID/CIE for patients, Hospital IDP for staff)

        // Try to find employee by email (acting as username for demo)
        Employee employee = null;
        try {
            employee = employeeService.findByEmail(username);
        } catch (Exception e) {
            // Employee not found
        }

        if (employee != null) {
            // Store employee in session
            session.setAttribute("user", employee);
            session.setAttribute("role", employee.getClass().getSimpleName().toUpperCase());

            // Redirect based on employee type
            if (employee instanceof com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist) {
                return "redirect:/medical-specialist/dashboard";
            } else if (employee instanceof com.nvivx.vixhealthsystem.model.person.employee.Secretary) {
                return "redirect:/secretary/dashboard";
            } else if (employee instanceof com.nvivx.vixhealthsystem.model.person.employee.Technician) {
                return "redirect:/technician/dashboard";
            } else if (employee instanceof com.nvivx.vixhealthsystem.model.person.employee.Buyer) {
                return "redirect:/buyer/dashboard";
            } else if (employee instanceof com.nvivx.vixhealthsystem.model.person.employee.StaffManager) {
                return "redirect:/staff-manager/dashboard";
            }
        }

        // If not employee, try patient (using fiscal code as "username" for demo)
        var patientOpt = patientService.findByFiscalCode(username);
        if (patientOpt.isPresent()) {
            session.setAttribute("patient", patientOpt.get());
            session.setAttribute("role", "PATIENT");
            return "redirect:/patient/dashboard";
        }

        model.addAttribute("error", "Invalid credentials");
        return "login";
    }



    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }


    @PostMapping("/select-role")
    public String processLogin(@RequestParam String role, HttpSession session) {
        // For demo purposes only - creates a mock session
        session.setAttribute("demoRole", role);
        switch (role) {
            case "MEDICAL_SPECIALIST":
                return "redirect:/medical-specialist/dashboard";
            case "TECHNICIAN":
                return "redirect:/technician/dashboard";
            case "STAFF_MANAGER":
                return "redirect:/staff-manager/dashboard";
            case "SECRETARY":
                return "redirect:/secretary/dashboard";
            case "BUYER":
                return "redirect:/buyer/dashboard";
            default:
                return "redirect:/login";
        }
    }
}