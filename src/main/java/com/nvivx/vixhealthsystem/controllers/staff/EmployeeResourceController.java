package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.resource.Storage;
import com.nvivx.vixhealthsystem.repository.StorageRepository;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.resources.InventoryService;
import com.nvivx.vixhealthsystem.service.resources.ResourceTakeLogStore;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Allows any authenticated employee to take resources from storage.
 * UC25 — Employee resource consumption with full audit logging.
 */
@Controller
@RequestMapping("/employee/resources")
public class EmployeeResourceController {

    private final InventoryService inventoryService;
    private final EmployeeService employeeService;
    private final StorageRepository storageRepository;
    private final ResourceTakeLogStore takeLogStore;

    public EmployeeResourceController(InventoryService inventoryService,
                                      EmployeeService employeeService,
                                      StorageRepository storageRepository,
                                      ResourceTakeLogStore takeLogStore) {
        this.inventoryService = inventoryService;
        this.employeeService = employeeService;
        this.storageRepository = storageRepository;
        this.takeLogStore = takeLogStore;
    }

    /** Show all resources available in storage, plus a take form. */
    @GetMapping
    public String viewResources(HttpSession session, Model model) {
        List<Storage> storages = storageRepository.findAll();
        var totalInventory = inventoryService.getTotalInventory();

        var resourceList = totalInventory.entrySet().stream()
                .map(e -> new ResourceRow(e.getKey().getId(), e.getKey().getName(),
                        e.getKey().getDescription(), e.getValue(), e.getValue() < 50))
                .sorted(java.util.Comparator.comparing(r -> r.name))
                .toList();

        model.addAttribute("resources", resourceList);
        model.addAttribute("storages", storages);
        model.addAttribute("pageTitle", "Take Resources");
        model.addAttribute("currentPage", "resources");
        return "employee/take-resources";
    }

    /** Process a resource take request. */
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

    /** Employee's personal resource-take history. */
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

    public record ResourceRow(Long id, String name, String description, int quantity, boolean lowStock) {}
}
