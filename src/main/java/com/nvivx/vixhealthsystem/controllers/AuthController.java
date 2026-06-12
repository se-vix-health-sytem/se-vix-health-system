package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.integration.FirebaseAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    private final FirebaseAuthService firebaseAuthService;
    private final EmployeeService employeeService;
    private final PatientService patientService;

    public AuthController(FirebaseAuthService firebaseAuthService, EmployeeService employeeService, com.nvivx.vixhealthsystem.service.core.PatientService patientService) {
        this.firebaseAuthService = firebaseAuthService;
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

        try {
            String firebaseUid =
                    firebaseAuthService.signInWithEmailAndPassword(username, password);

            Employee employee =
                    employeeService.findByFirebaseUid(firebaseUid);

            if (employee == null) {
                model.addAttribute("error", "Employee account not linked to Firebase");
                return "login";
            }

            session.setAttribute("user", employee);
            session.setAttribute("role", employee.getClass().getSimpleName().toUpperCase());

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

        } catch (Exception e) {
            model.addAttribute("error", "Invalid Firebase credentials");
            return "login";
        }

        model.addAttribute("error", "Invalid role");
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