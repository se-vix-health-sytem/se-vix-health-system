package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.DevCredentialStore;
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

/**
 * @brief Authentication controller for staff (employees) of VIX Health System.
 *
 * Handles login, Firebase-backed credential verification, role-based dashboard
 * redirection, and logout for all employee roles (MedicalSpecialist, Secretary,
 * Technician, Buyer, StaffManager).  Also exposes a dev-only role-selector
 * endpoint that bypasses Firebase for local testing.
 *
 * Base URL: no prefix (login/logout live at the root context).
 *
 * Use cases covered:
 * - UC01 Staff login via Firebase email/password
 * - Dev quick-login via pre-seeded credential store
 *
 * @see FirebaseAuthService
 * @see EmployeeService
 * @see DevCredentialStore
 */
@Controller
public class AuthController {
    private final FirebaseAuthService firebaseAuthService;
    private final EmployeeService employeeService;
    private final PatientService patientService;
    private final DevCredentialStore devCredentialStore;

    public AuthController(FirebaseAuthService firebaseAuthService,
                          EmployeeService employeeService,
                          PatientService patientService,
                          DevCredentialStore devCredentialStore) {
        this.firebaseAuthService = firebaseAuthService;
        this.employeeService = employeeService;
        this.patientService = patientService;
        this.devCredentialStore = devCredentialStore;
    }

    // =========================================================
    // LOGIN / LOGOUT
    // =========================================================

    /**
     * GET /login : render the staff login form.
     *
     * Populates the model with pre-seeded dev credentials so testers can
     * one-click into any role from the login page.
     *
     * @param error  Non-null when Spring Security or a previous authenticate()
     *               call redirected here with {@code ?error}; triggers an
     *               "Invalid credentials" banner.
     * @param model  Spring MVC model; receives {@code error} and
     *               {@code devCredentials} attributes.
     * @return       The {@code login} Thymeleaf template.
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid credentials");
        }
        model.addAttribute("devCredentials", devCredentialStore.getAll());
        return "login";
    }

    /**
     * POST /authenticate : verify Firebase credentials and open a staff session.
     *
     * Calls Firebase to exchange email/password for a UID, looks up the matching
     * Employee, registers a Spring Security context in the HTTP session, then
     * redirects to the role-specific dashboard.  Returns to the login page with
     * an error attribute on any failure.
     *
     * The session attribute {@code "user"} stores the Employee entity instead of
     * using the Spring Security principal because employee domain objects carry
     * additional context (department, specialty, etc.) that the principal does not.
     *
     * @param username  Employee's Firebase email address.
     * @param password  Firebase account password (plain text over HTTPS).
     * @param session   HTTP session; receives {@code "user"} and {@code "role"} attributes.
     * @param request   Raw servlet request (passed to session-security plumbing).
     * @param response  Raw servlet response (passed to session-security plumbing).
     * @param model     Receives {@code "error"} when authentication fails.
     * @return          Redirect to the role-specific dashboard, or the {@code login}
     *                  template with an error message.
     */
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
                model.addAttribute("devCredentials", devCredentialStore.getAll());
                return "login";
            }

            String role = employee.getClass().getSimpleName().toUpperCase();
            session.setAttribute("user", employee);
            session.setAttribute("role", role);
            session.setAttribute("canTakeResources", employee.hasFacilityStorage());

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
            model.addAttribute("devCredentials", devCredentialStore.getAll());
            return "login";
        }

        model.addAttribute("error", "Invalid role");
        model.addAttribute("devCredentials", devCredentialStore.getAll());
        return "login";
    }



    /**
     * GET /logout : invalidate the staff session and clear the security context.
     *
     * @param session  HTTP session to invalidate.
     * @return         Redirect to the public home page {@code /}.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/";
    }


    // =========================================================
    // DEV QUICK-LOGIN
    // =========================================================

    /**
     * POST /select-role : dev-only role selector that bypasses Firebase.
     *
     * Looks up the first employee of the requested type from the database,
     * seeds the HTTP session, registers a Spring Security context, then
     * redirects to the matching dashboard.  Not intended for production use.
     *
     * @param role     One of: {@code MEDICAL_SPECIALIST}, {@code TECHNICIAN},
     *                 {@code STAFF_MANAGER}, {@code SECRETARY}, {@code BUYER}.
     * @param session  HTTP session; receives {@code "user"} and {@code "role"} attributes.
     * @return         Redirect to the role-specific dashboard, or {@code redirect:/login}
     *                 when the role string is unrecognized.
     */
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
            session.setAttribute("canTakeResources", demoUser.hasFacilityStorage());
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