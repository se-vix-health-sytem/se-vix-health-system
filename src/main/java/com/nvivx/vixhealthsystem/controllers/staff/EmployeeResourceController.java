package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.resources.InventoryService;
import com.nvivx.vixhealthsystem.service.resources.ResourceTakeLogStore;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * @brief Resource self-service controller for any authenticated employee : base URL {@code /employee/resources}.
 *
 * Any staff member (regardless of role) can view available stock and withdraw
 * resources for their own use.  Every take is recorded in {@link ResourceTakeLogStore}
 * for buyer/manager auditing.
 *
 * Use cases covered: UC25 (employee resource consumption with audit logging).
 *
 * @see InventoryService
 * @see ResourceTakeLogStore
 */
@Controller
@RequestMapping("/employee/resources")
public class EmployeeResourceController {

    private final InventoryService inventoryService;
    private final EmployeeService employeeService;
    private final ResourceTakeLogStore takeLogStore;

    public EmployeeResourceController(InventoryService inventoryService,
                                      EmployeeService employeeService,
                                      ResourceTakeLogStore takeLogStore) {
        this.inventoryService = inventoryService;
        this.employeeService = employeeService;
        this.takeLogStore = takeLogStore;
    }

    // =========================================================
    // RESOURCE BROWSING
    // =========================================================

    /**
     * GET /employee/resources : show all resources available in storage with a take form.
     *
     * @param session  HTTP session (available for future per-role filtering).
     * @param model    Receives {@code resources} (sorted by name) and {@code storages} attributes.
     * @return         Thymeleaf template {@code employee/take-resources}.
     */
    @GetMapping
    public String viewResources(HttpSession session, Model model,
                                RedirectAttributes redirectAttributes) {
        Employee sessionUser = (Employee) session.getAttribute("user");
        if (!sessionUser.hasFacilityStorage()) {
            redirectAttributes.addFlashAttribute("info",
                    "Your department does not have storage access. Resource management is not available for your role.");
            return resolveDashboard(session);
        }

        var inventory = inventoryService.getStorageInventory(sessionUser.getFacilityStorage().getId());
        String facilityName = sessionUser.getFacilityName();

        var resourceList = inventory.entrySet().stream()
                .map(e -> new ResourceRow(e.getKey().getId(), e.getKey().getName(),
                        e.getKey().getDescription(), e.getValue(), e.getValue() < 50))
                .sorted(java.util.Comparator.comparing(r -> r.name))
                .toList();

        model.addAttribute("resources", resourceList);
        model.addAttribute("facilityName", facilityName);
        model.addAttribute("pageTitle", "Take Resources");
        model.addAttribute("currentPage", "resources");
        return "employee/take-resources";
    }

    // =========================================================
    // RESOURCE TAKE
    // =========================================================

    /**
     * POST /employee/resources/take : withdraw a quantity of a resource from storage.
     *
     * Resolves the acting employee from the session (the {@code "user"} attribute is
     * used because the full Employee domain object is required by the inventory
     * service's audit path), then delegates to {@link InventoryService#removeResourceFromStorage}.
     * A flash attribute communicates success or failure back to the redirected view.
     *
     * @param resourceId          ID of the resource to withdraw.
     * @param quantity            Number of units to take; must not exceed available stock.
     * @param session             HTTP session carrying the {@code "user"} Employee attribute.
     * @param redirectAttributes  Flash attributes for the redirect to {@code /employee/resources}.
     * @return                    Redirect to {@code /employee/resources}.
     */
    @PostMapping("/take")
    public String takeResource(@RequestParam Long resourceId,
                               @RequestParam int quantity,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Employee sessionUser = (Employee) session.getAttribute("user");
        if (sessionUser == null) {
            redirectAttributes.addFlashAttribute("error", "Not authenticated.");
            return "redirect:/employee/resources";
        }
        if (!sessionUser.hasFacilityStorage()) {
            return resolveDashboard(session);
        }
        try {
            Employee employee = employeeService.findById(sessionUser.getId());
            inventoryService.removeResourceFromStorage(employee, resourceId, quantity);

            var resource = inventoryService.getResourceById(resourceId);
            redirectAttributes.addFlashAttribute("message",
                    "✅ Took " + quantity + " unit(s) of " + resource.getName() + " from storage.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }
        return "redirect:/employee/resources";
    }

    // =========================================================
    // HISTORY
    // =========================================================

    /**
     * GET /employee/resources/history : display the current employee's personal take history.
     *
     * @param session  HTTP session carrying the {@code "user"} Employee attribute.
     * @param model    Receives {@code logs} filtered to the authenticated employee's ID.
     * @return         Thymeleaf template {@code employee/resource-history}, or
     *                 {@code redirect:/login} when no session user is found.
     */
    @GetMapping("/history")
    public String viewMyHistory(HttpSession session, Model model) {
        Employee sessionUser = (Employee) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        var logs = takeLogStore.getByEmployee(sessionUser.getId());
        model.addAttribute("logs", logs);
        model.addAttribute("pageTitle", "My Resource History");
        model.addAttribute("currentPage", "resources");
        return "employee/resource-history";
    }

    private String resolveDashboard(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (role == null) return "redirect:/login";
        return switch (role) {
            case "MEDICALSPECIALIST" -> "redirect:/medical-specialist/dashboard";
            case "SECRETARY"         -> "redirect:/secretary/dashboard";
            case "TECHNICIAN"        -> "redirect:/technician/dashboard";
            case "STAFFMANAGER"      -> "redirect:/staff-manager/dashboard";
            case "BUYER"             -> "redirect:/buyer/dashboard";
            default -> "redirect:/login";
        };
    }

    public record ResourceRow(Long id, String name, String description, int quantity, boolean lowStock) {}
}
