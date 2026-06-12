package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.service.resources.InventoryService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    private final InventoryService inventoryService;
    private final EmployeeService employeeService;

    public BuyerController(InventoryService inventoryService,
                           EmployeeService employeeService) {
        this.inventoryService = inventoryService;
        this.employeeService = employeeService;
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
    public String viewInventory(Model model) {
        var resourcesWithQuantity = inventoryService.getTotalInventory();

        // Convert to list format for template
        var resourceList = resourcesWithQuantity.entrySet().stream()
                .map(entry -> new InventoryItem(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getKey().getDescription(),
                        entry.getValue(),
                        entry.getValue() < 50, // low stock threshold
                        entry.getKey().getPrice(),
                        "units" // default unit, can be enhanced later
                ))
                .toList();

        model.addAttribute("pageTitle", "Inventory Management");
        model.addAttribute("currentPage", "inventory");
        model.addAttribute("resources", resourceList);
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
                              Model model) {
        try {
            BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);
            var newResource = inventoryService.createResource(name, description, bigDecimalPrice);

            // Add to storage (using storage ID 1 as default - main hospital)
            // In production, you'd get the correct storage ID from the buyer's facility
            inventoryService.addResourceToStorage(1L, newResource.getId(), quantity);

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
                                 Model model) {
        try {
            if ("add".equals(action)) {
                inventoryService.addResourceToStorage(1L, resourceId, quantity);
                model.addAttribute("message", "✅ Added " + quantity + " units to inventory!");
            } else if ("remove".equals(action)) {
                inventoryService.removeResourceFromStorage(1L, resourceId, quantity);
                model.addAttribute("message", "✅ Removed " + quantity + " units from inventory!");
            }
            model.addAttribute("pageTitle", "Inventory Updated");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "buyer/result";
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