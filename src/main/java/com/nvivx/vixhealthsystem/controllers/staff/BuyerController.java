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

/**
 * @brief Controller for Buyer staff members : base URL {@code /buyer}.
 *
 * Buyers are responsible for procurement and inventory management.
 * This controller covers the full purchasing workflow:
 * viewing and updating stock levels, adding new resources, monitoring
 * low-stock alerts, reviewing the resource-take audit log, and accessing
 * personal shift and profile pages.
 *
 * Only accessible to users with {@code ROLE_BUYER}.
 *
 * Use cases covered: UC25 (resource management), UC30 (profile/shifts).
 *
 * @see InventoryService
 * @see ResourceTakeLogStore
 * @see ShiftService
 * @see VacationService
 */
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

    // =========================================================
    // DASHBOARD
    // =========================================================

    /**
     * GET /buyer/dashboard : render the buyer's overview dashboard.
     *
     * Populates low-stock count, total resource count, and the buyer's display
     * name (resolved from the first buyer in the database as a temporary measure
     * until session-backed identity is fully wired).
     *
     * @param model  Receives {@code lowStockCount}, {@code totalResources}, and
     *               {@code buyerName} attributes.
     * @return       Thymeleaf template {@code buyer/dashboard}.
     */
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

    // =========================================================
    // INVENTORY
    // =========================================================

    /**
     * GET /buyer/inventory : list all resources currently in storage.
     *
     * Aggregates stock quantities across all storage locations and wraps each
     * entry in an {@link InventoryItem} for the view.  Resources with fewer than
     * 50 units are flagged as low-stock.  An empty list is substituted and an
     * error banner is shown if the service call fails.
     *
     * @param model    Receives {@code resources} (sorted by name), {@code pageTitle},
     *                 and {@code isLowStockView} attributes.
     * @param session  HTTP session (retained for future session-based filtering).
     * @return         Thymeleaf template {@code buyer/inventory}.
     */
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

    /**
     * GET /buyer/inventory/low-stock : list only resources below the stock threshold.
     *
     * @param model  Receives {@code resources} (all items flagged as low-stock),
     *               {@code pageTitle}, and {@code isLowStockView=true}.
     * @return       Thymeleaf template {@code buyer/inventory}.
     */
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

    /**
     * GET /buyer/inventory/add : render the add-resource form.
     *
     * @param model  Receives {@code pageTitle}.
     * @return       Thymeleaf template {@code buyer/add-resource}.
     */
    @GetMapping("/inventory/add")
    public String showAddResourceForm(Model model) {
        model.addAttribute("pageTitle", "Add New Resource");
        return "buyer/add-resource";
    }

    /**
     * POST /buyer/inventory/add : create a new resource and add initial stock.
     *
     * Creates the resource entity via {@link InventoryService}, then calls the
     * domain method {@code Buyer.addResourceToStorage} to record the initial
     * quantity.  The acting buyer is resolved from the session.
     *
     * @param name        Human-readable resource name.
     * @param description Short description of the resource.
     * @param quantity    Initial quantity to add to storage; must be positive.
     * @param price       Unit price in euros.
     * @param unit        Unit of measure label (e.g., "units", "boxes").
     * @param session     HTTP session; used to resolve the authenticated Buyer via
     *                    {@code "user"} : needed because the Buyer domain object
     *                    contains the business logic for adding stock.
     * @param model       Receives a success or error {@code message} attribute.
     * @return            Thymeleaf template {@code buyer/result}.
     */
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

    /**
     * GET /buyer/inventory/{resourceId} : display details for a single resource.
     *
     * @param resourceId  Database ID of the resource to display.
     * @param model       Receives {@code resource}, {@code totalQuantity},
     *                    {@code isLowStock}, and {@code lowStockThreshold} attributes.
     * @return            Thymeleaf template {@code buyer/resource-details}, or
     *                    {@code buyer/result} with a not-found message when the ID
     *                    does not exist.
     */
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

    /**
     * POST /buyer/inventory/update-quantity : add or remove units for an existing resource.
     *
     * @param resourceId  ID of the resource whose stock level is being changed.
     * @param quantity    Number of units to add or remove; must be positive.
     * @param action      Either {@code "add"} or {@code "remove"}.
     * @param session     HTTP session; used to resolve the authenticated Buyer (the Buyer
     *                    domain object owns the storage mutation logic).
     * @param model       Receives a success or error {@code message} attribute.
     * @return            Thymeleaf template {@code buyer/result}.
     */
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

    // =========================================================
    // PROFILE
    // =========================================================

    /**
     * GET /buyer/my-shifts : display the buyer's assigned shifts and approved vacations.
     *
     * The session attribute {@code "user"} is read directly (rather than using the
     * Spring Security principal) to obtain the Buyer's database ID, which is then
     * used to query shifts and vacation records.
     *
     * @param session  HTTP session carrying the {@code "user"} Employee attribute.
     * @param model    Receives {@code shifts}, {@code vacations}, and
     *                 {@code dashboardLink} attributes.
     * @return         Thymeleaf template {@code employee/my-shifts}, or
     *                 {@code redirect:/login} when no session user is found.
     */
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

    /**
     * GET /buyer/profile : display the buyer's personal profile page.
     *
     * Reloads a fresh Employee entity from the database to avoid using a
     * potentially stale session object.  Falls back to the session entity
     * if the DB lookup fails.
     *
     * @param session  HTTP session carrying the {@code "user"} Employee attribute.
     * @param model    Receives {@code employee}, {@code roleLabel}, and
     *                 {@code dashboardLink} attributes.
     * @return         Thymeleaf template {@code employee/profile}, or
     *                 {@code redirect:/login} when no session user is found.
     */
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

    // =========================================================
    // RESOURCE TAKE LOG
    // =========================================================

    /**
     * GET /buyer/resource-log : show the full resource-take audit log.
     *
     * Displays every recorded instance of an employee taking a resource from
     * storage, giving the buyer visibility over consumption patterns.
     *
     * @param model  Receives {@code logs} (all {@link ResourceTakeLogStore} entries).
     * @return       Thymeleaf template {@code buyer/resource-log}.
     */
    @GetMapping("/resource-log")
    public String viewResourceLog(Model model) {
        model.addAttribute("logs", takeLogStore.getAll());
        model.addAttribute("pageTitle", "Resource Take Log");
        model.addAttribute("currentPage", "inventory");
        return "buyer/resource-log";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Resolve and reload the authenticated Buyer from the HTTP session.
     *
     * The session attribute {@code "user"} is read because the Spring Security
     * principal only carries the role string, not the full domain object needed
     * by Buyer's storage mutation methods.  A fresh entity is loaded from the
     * database to avoid Hibernate detached-object issues.
     *
     * @param session  HTTP session carrying the {@code "user"} attribute.
     * @return         A fully initialised {@link Buyer} from the database, or a
     *                 transient {@code new Buyer()} when the session holds no Buyer.
     */
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