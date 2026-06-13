package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.service.resources.InventoryService;
import com.nvivx.vixhealthsystem.service.resources.ResourceTakeLogStore;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    private final InventoryService inventoryService;
    private final EmployeeService employeeService;
    private final ResourceTakeLogStore takeLogStore;
    private final ShiftService shiftService;
    private final VacationService vacationService;

    public BuyerController(InventoryService inventoryService,
                           EmployeeService employeeService,
                           ResourceTakeLogStore takeLogStore,
                           ShiftService shiftService,
                           VacationService vacationService) {
        this.inventoryService = inventoryService;
        this.employeeService = employeeService;
        this.takeLogStore = takeLogStore;
        this.shiftService = shiftService;
        this.vacationService = vacationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var lowStockCount = inventoryService.getLowStockResources().size();
        var totalResources = inventoryService.getAllResources().size();

        // Get buyer info (current logged-in user - will be implemented with auth later)
        // For now, get first buyer from database or use placeholder
        var buyers = employeeService.findAllBuyers();
        String buyerName = buyers.isEmpty() ? "Buyer" : buyers.get(0).getName() + " " + buyers.get(0).getSurname();

        model.addAttribute("pageTitle", "Purchasing Department Dashboard");
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("totalResources", totalResources);
        model.addAttribute("buyerName", buyerName);
        return "buyer/dashboard";
    }

    @GetMapping("/inventory")
    public String viewInventory(Model model, HttpSession session) {
        try {
            var totalInventory = inventoryService.getTotalInventory();

            var resourceList = totalInventory.entrySet().stream()
                    .map(entry -> new InventoryItem(
                            entry.getKey().getId(),
                            entry.getKey().getName(),
                            entry.getKey().getDescription(),
                            entry.getValue(),
                            entry.getValue() < 50,
                            entry.getKey().getPrice(),
                            "units"
                    ))
                    .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                    .collect(Collectors.toList());

            model.addAttribute("resources", resourceList);
            model.addAttribute("pageTitle", "Inventory Management");
            model.addAttribute("currentPage", "inventory");
            model.addAttribute("isLowStockView", false);
        } catch (Exception e) {
            model.addAttribute("resources", new ArrayList<>());
            model.addAttribute("error", "Could not load inventory: " + e.getMessage());
        }
        return "buyer/inventory";
    }

    @GetMapping("/inventory/low-stock")
    public String viewLowStock(Model model) {
        var lowStockResources = inventoryService.getLowStockResources();

        var lowStockList = lowStockResources.stream()
                .map(item -> new InventoryItem(
                        item.getResource().getId(),
                        item.getResource().getName(),
                        item.getResource().getDescription(),
                        item.getQuantity(),
                        true,
                        item.getResource().getPrice(),
                        "units"
                ))
                .toList();

        model.addAttribute("pageTitle", "Low Stock Alerts");
        model.addAttribute("currentPage", "inventory");
        model.addAttribute("resources", lowStockList);
        model.addAttribute("isLowStockView", true);
        return "buyer/inventory";
    }

    @GetMapping("/inventory/add")
    public String showAddResourceForm(Model model) {
        model.addAttribute("pageTitle", "Add New Resource");
        return "buyer/add-resource";
    }

    @PostMapping("/inventory/add")
    public String addResource(@RequestParam String name,
                              @RequestParam String description,
                              @RequestParam int quantity,
                              @RequestParam float price,
                              @RequestParam String unit,
                              HttpSession session,
                              Model model) {
        try {
            Buyer buyer = getBuyerFromSession(session);
            BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);
            var newResource = inventoryService.createResource(name, description, bigDecimalPrice);

            // Domain: buyer adds resource to storage via model method
            inventoryService.addResourceToStorage(buyer, newResource.getId(), quantity);

            model.addAttribute("pageTitle", "Resource Added Successfully");
            model.addAttribute("message",
                    "✅ Resource Added!\n\n" +
                            "Name: " + name + "\n" +
                            "Quantity: " + quantity + " " + unit + "\n" +
                            "Price per unit: €" + price + "\n\n" +
                            "Resource ID: " + newResource.getId());
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "buyer/result";
    }

    @GetMapping("/inventory/{resourceId}")
    public String viewResourceDetails(@PathVariable Long resourceId, Model model) {
        try {
            var resource = inventoryService.getResourceById(resourceId);
            var totalQuantity = inventoryService.getTotalResourceQuantity(resourceId);
            var isLowStock = inventoryService.isResourceLowStock(resourceId);

            model.addAttribute("pageTitle", "Resource Details - " + resource.getName());
            model.addAttribute("currentPage", "inventory");
            model.addAttribute("resource", resource);
            model.addAttribute("totalQuantity", totalQuantity);
            model.addAttribute("isLowStock", isLowStock);
            model.addAttribute("lowStockThreshold", 50);
            return "buyer/resource-details";
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Resource Not Found");
            model.addAttribute("message", "Resource with ID " + resourceId + " not found");
            return "buyer/result";
        }
    }

    @PostMapping("/inventory/update-quantity")
    public String updateQuantity(@RequestParam Long resourceId,
                                 @RequestParam int quantity,
                                 @RequestParam String action,
                                 HttpSession session,
                                 Model model) {
        try {
            Buyer buyer = getBuyerFromSession(session);
            if ("add".equals(action)) {
                inventoryService.addResourceToStorage(buyer, resourceId, quantity);
                model.addAttribute("message", "✅ Added " + quantity + " units to inventory!");
            } else if ("remove".equals(action)) {
                inventoryService.removeResourceFromStorage(buyer, resourceId, quantity);
                model.addAttribute("message", "✅ Removed " + quantity + " units from inventory!");
            }
            model.addAttribute("pageTitle", "Inventory Updated");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "buyer/result";
    }

    // ========== PROFILE ==========

    @GetMapping("/my-shifts")
    public String viewMyShifts(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        List<VacationRequest> vacations = vacationService.getApprovedRequestsForEmployee(user.getId().intValue());
        model.addAttribute("shifts", shiftService.getShiftsForEmployee(user.getId()));
        model.addAttribute("vacations", vacations);
        model.addAttribute("dashboardLink", "/buyer/dashboard");
        model.addAttribute("pageTitle", "My Shifts");
        model.addAttribute("currentPage", "myShifts");
        return "employee/my-shifts";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Employee sessionUser = (Employee) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        try {
            Employee fresh = employeeService.findById(sessionUser.getId());
            model.addAttribute("employee", fresh);
            model.addAttribute("pageTitle", "My Profile");
            model.addAttribute("currentPage", "profile");
            model.addAttribute("roleLabel", "Buyer");
            model.addAttribute("dashboardLink", "/buyer/dashboard");
            model.addAttribute("isSpecialist", false);
        } catch (Exception e) {
            model.addAttribute("employee", sessionUser);
            model.addAttribute("currentPage", "profile");
        }
        return "employee/profile";
    }

    // ========== RESOURCE TAKE LOG ==========

    @GetMapping("/resource-log")
    public String viewResourceLog(Model model) {
        model.addAttribute("logs", takeLogStore.getAll());
        model.addAttribute("pageTitle", "Resource Take Log");
        model.addAttribute("currentPage", "inventory");
        return "buyer/resource-log";
    }

    // ========== HELPERS ==========

    private Buyer getBuyerFromSession(HttpSession session) {
        Employee user = (Employee) session.getAttribute("user");
        if (user instanceof Buyer b) {
            Employee fresh = employeeService.findById(b.getId());
            if (fresh instanceof Buyer buyer) {
                return buyer;
            }
        }
        return new Buyer();
    }

    // Inner class for inventory display
    public static class InventoryItem {
        private final Long id;
        private final String name;
        private final String description;
        private final int quantity;
        private final boolean lowStock;
        private final BigDecimal price;
        private final String unit;

        public InventoryItem(Long id, String name, String description, int quantity,
                             boolean lowStock, BigDecimal price, String unit) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.quantity = quantity;
            this.lowStock = lowStock;
            this.price = price;
            this.unit = unit;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getQuantity() { return quantity; }
        public boolean isLowStock() { return lowStock; }
        public BigDecimal getPrice() { return price; }
        public String getUnit() { return unit; }
    }
}