package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.service.BuyerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var lowStockCount = buyerService.getLowStockResources().size();
        var totalResources = buyerService.getAllResources().size();

        model.addAttribute("pageTitle", "Purchasing Department Dashboard");
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("totalResources", totalResources);
        return "buyer/dashboard";
    }

    @GetMapping("/inventory")
    public String viewInventory(Model model) {
        var resources = buyerService.getAllResources();
        model.addAttribute("pageTitle", "Inventory Management");
        model.addAttribute("resources", resources);
        return "buyer/inventory";
    }

    @GetMapping("/inventory/low-stock")
    public String viewLowStock(Model model) {
        var lowStockResources = buyerService.getLowStockResources();
        model.addAttribute("pageTitle", "Low Stock Alerts");
        model.addAttribute("resources", lowStockResources);
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
            var newResource = buyerService.addResource(name, description, quantity, price, unit);
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
    public String viewResourceDetails(@PathVariable int resourceId, Model model) {
        var resource = buyerService.getResourceById(resourceId);
        if (resource == null) {
            model.addAttribute("pageTitle", "Resource Not Found");
            model.addAttribute("message", "Resource with ID " + resourceId + " not found");
            return "buyer/result";
        }
        model.addAttribute("pageTitle", "Resource Details - " + resource.getName());
        model.addAttribute("resource", resource);
        return "buyer/resource-details";
    }
}