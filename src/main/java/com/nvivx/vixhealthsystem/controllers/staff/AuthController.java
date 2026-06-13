package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.integration.FirebaseAuthService;
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
                               HttpServletRequest request,
                               HttpServletResponse response,
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

            String role = employee.getClass().getSimpleName().toUpperCase();
            session.setAttribute("user", employee);
            session.setAttribute("role", role);

            // Register with Spring Security so route-level protection works
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(new UsernamePasswordAuthenticationToken(
                username, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
            ));
            SecurityContextHolder.setContext(ctx);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);

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
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/";
    }


    @PostMapping("/select-role")
    public String processLogin(@RequestParam String role, HttpSession session) {
        Class<? extends Employee> employeeType = switch (role) {
            case "MEDICAL_SPECIALIST" -> com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist.class;
            case "TECHNICIAN"         -> com.nvivx.vixhealthsystem.model.person.employee.Technician.class;
            case "STAFF_MANAGER"      -> com.nvivx.vixhealthsystem.model.person.employee.StaffManager.class;
            case "SECRETARY"          -> com.nvivx.vixhealthsystem.model.person.employee.Secretary.class;
            case "BUYER"              -> com.nvivx.vixhealthsystem.model.person.employee.Buyer.class;
            default -> null;
        };
        if (employeeType == null) return "redirect:/login";

        java.util.List<? extends Employee> matches = employeeService.findByRole(employeeType);
        Employee demoUser = matches.isEmpty() ? null : matches.get(0);

        String springRole = "ROLE_" + role.replace("_", "");
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken(
            role, null, List.of(new SimpleGrantedAuthority(springRole))
        ));
        SecurityContextHolder.setContext(ctx);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);

        if (demoUser != null) {
            session.setAttribute("user", demoUser);
            session.setAttribute("role", demoUser.getClass().getSimpleName().toUpperCase());
        }

        return switch (role) {
            case "MEDICAL_SPECIALIST" -> "redirect:/medical-specialist/dashboard";
            case "TECHNICIAN"         -> "redirect:/technician/dashboard";
            case "STAFF_MANAGER"      -> "redirect:/staff-manager/dashboard";
            case "SECRETARY"          -> "redirect:/secretary/dashboard";
            case "BUYER"              -> "redirect:/buyer/dashboard";
            default -> "redirect:/login";
        };
    }
}