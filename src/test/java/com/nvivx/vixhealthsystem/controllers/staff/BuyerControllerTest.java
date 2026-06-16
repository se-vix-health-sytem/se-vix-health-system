package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.resources.InventoryService;
import com.nvivx.vixhealthsystem.service.resources.ResourceTakeLogStore;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @class BuyerControllerTest
 * @brief Unit tests for BuyerController (staff purchasing module).
 *
 * These tests validate the controller layer in isolation by mocking all dependencies.
 * No Spring context is loaded; only direct method calls are tested.
 *
 * Covered features:
 * - Dashboard data rendering
 * - Inventory management views
 * - Low stock listing
 * - Resource creation and updates
 * - Resource logs
 * - Profile handling
 * - Shift and vacation views
 */
class BuyerControllerTest {

    /// Mocked inventory service for resource management operations.
    private InventoryService inventoryService;

    /// Mocked employee service for retrieving buyer data.
    private EmployeeService employeeService;

    /// Mocked resource take log storage.
    private ResourceTakeLogStore takeLogStore;

    /// Mocked shift scheduling service.
    private ShiftService shiftService;

    /// Mocked vacation service.
    private VacationService vacationService;

    /// Controller under test.
    private BuyerController controller;

    /// Mocked HTTP session to simulate logged-in user.
    private HttpSession session;

    /**
     * @brief Initializes controller and mocked dependencies before each test.
     */
    @BeforeEach
    void setUp() {

        inventoryService = mock(InventoryService.class);
        employeeService = mock(EmployeeService.class);
        takeLogStore = mock(ResourceTakeLogStore.class);
        shiftService = mock(ShiftService.class);
        vacationService = mock(VacationService.class);

        controller = new BuyerController(
                inventoryService,
                employeeService,
                takeLogStore,
                shiftService,
                vacationService
        );

        session = mock(HttpSession.class);
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    /**
     * @brief Verifies that the buyer dashboard loads correctly.
     *
     * Ensures:
     * - Page title is set
     * - Buyer full name is computed correctly
     * - Inventory statistics are included in model
     */
    @Test
    void dashboard_shouldLoadCorrectData() {

        Buyer buyer = new Buyer();
        buyer.setName("John");
        buyer.setSurname("Doe");

        when(inventoryService.getLowStockResources()).thenReturn(List.of());
        when(inventoryService.getAllResources()).thenReturn(List.of());
        when(employeeService.findAllBuyers()).thenReturn(List.of(buyer));

        Model model = new ConcurrentModel();

        String view = controller.dashboard(model);

        assertEquals("buyer/dashboard", view);
        assertEquals("Purchasing Department Dashboard",
                model.getAttribute("pageTitle"));
        assertEquals("John Doe",
                model.getAttribute("buyerName"));
        assertEquals(0,
                model.getAttribute("lowStockCount"));
        assertEquals(0,
                model.getAttribute("totalResources"));
    }

    // =========================================================
    // INVENTORY
    // =========================================================

    /**
     * @brief Verifies that inventory page loads correctly.
     */
    @Test
    void viewInventory_shouldReturnPage() {

        when(inventoryService.getTotalInventory())
                .thenReturn(Map.of());

        Model model = new ConcurrentModel();

        String view =
                controller.viewInventory(model, session);

        assertEquals("buyer/inventory", view);
        assertEquals("Inventory Management",
                model.getAttribute("pageTitle"));
    }

    // =========================================================
    // LOW STOCK
    // =========================================================

    /**
     * @brief Verifies that low-stock resources are correctly displayed.
     */
    @Test
    void viewLowStock_shouldReturnList() {

        InventoryService.ResourceWithQuantity item =
                mock(InventoryService.ResourceWithQuantity.class);

        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Gloves");

        when(item.getResource()).thenReturn(resource);
        when(item.getQuantity()).thenReturn(10);

        when(inventoryService.getLowStockResources())
                .thenReturn(List.of(item));

        Model model = new ConcurrentModel();

        String view = controller.viewLowStock(model);

        assertEquals("buyer/inventory", view);
        assertTrue((Boolean) model.getAttribute("isLowStockView"));
        assertNotNull(model.getAttribute("resources"));
    }

    // =========================================================
    // ADD RESOURCE
    // =========================================================

    /**
     * @brief Verifies that a new resource can be added and stored correctly.
     *
     * Ensures:
     * - Resource creation is triggered
     * - Inventory update is executed
     * - Success message is shown
     */
    @Test
    void addResource_shouldReturnSuccessMessage() {

        Buyer buyer = new Buyer();
        buyer.setId(1L);

        when(session.getAttribute("user")).thenReturn(buyer);
        when(employeeService.findById(1L)).thenReturn(buyer);

        Resource resource = mock(Resource.class);
        when(resource.getId()).thenReturn(10L);

        when(inventoryService.createResource(
                anyString(),
                anyString(),
                any(BigDecimal.class)
        )).thenReturn(resource);

        Model model = new ConcurrentModel();

        String view = controller.addResource(
                "Gloves",
                "Medical gloves",
                10,
                5.0f,
                "units",
                session,
                model
        );

        assertEquals("buyer/result", view);

        verify(inventoryService)
                .addResourceToStorage(buyer, 10L, 10);

        assertTrue(model.getAttribute("message")
                .toString()
                .contains("Resource Added"));
    }

    // =========================================================
    // UPDATE QUANTITY
    // =========================================================

    /**
     * @brief Verifies adding quantity to an existing resource.
     */
    @Test
    void updateQuantity_add_shouldWork() {

        Buyer buyer = new Buyer();
        buyer.setId(1L);

        when(session.getAttribute("user")).thenReturn(buyer);
        when(employeeService.findById(1L)).thenReturn(buyer);

        Model model = new ConcurrentModel();

        String view = controller.updateQuantity(
                1L,
                5,
                "add",
                session,
                model
        );

        assertEquals("buyer/result", view);

        verify(inventoryService)
                .addResourceToStorage(buyer, 1L, 5);

        assertTrue(model.getAttribute("message")
                .toString()
                .contains("Added"));
    }

    /**
     * @brief Verifies removing quantity from an existing resource.
     */
    @Test
    void updateQuantity_remove_shouldWork() {

        Buyer buyer = new Buyer();
        buyer.setId(1L);

        when(session.getAttribute("user")).thenReturn(buyer);
        when(employeeService.findById(1L)).thenReturn(buyer);

        Model model = new ConcurrentModel();

        String view = controller.updateQuantity(
                1L,
                3,
                "remove",
                session,
                model
        );

        assertEquals("buyer/result", view);

        verify(inventoryService)
                .removeResourceFromStorage(buyer, 1L, 3);

        assertTrue(model.getAttribute("message")
                .toString()
                .contains("Removed"));
    }

    // =========================================================
    // RESOURCE LOG
    // =========================================================

    /**
     * @brief Verifies that resource log page loads correctly.
     */
    @Test
    void resourceLog_shouldLoad() {

        when(takeLogStore.getAll()).thenReturn(List.of());

        Model model = new ConcurrentModel();

        String view = controller.viewResourceLog(model);

        assertEquals("buyer/resource-log", view);
        assertEquals("Resource Take Log",
                model.getAttribute("pageTitle"));
    }

    // =========================================================
    // PROFILE
    // =========================================================

    /**
     * @brief Verifies redirect when no user is in session.
     */
    @Test
    void profile_shouldRedirectWhenNoUser() {

        when(session.getAttribute("user")).thenReturn(null);

        Model model = new ConcurrentModel();

        String view = controller.viewProfile(session, model);

        assertEquals("redirect:/login", view);
    }

    /**
     * @brief Verifies buyer profile page loads correctly.
     */
    @Test
    void profile_shouldLoadProfile() {

        Buyer buyer = new Buyer();
        buyer.setId(1L);

        when(session.getAttribute("user")).thenReturn(buyer);
        when(employeeService.findById(1L)).thenReturn(buyer);

        Model model = new ConcurrentModel();

        String view = controller.viewProfile(session, model);

        assertEquals("employee/profile", view);
        assertEquals("Buyer",
                model.getAttribute("roleLabel"));
    }

    // =========================================================
    // SHIFTS
    // =========================================================

    /**
     * @brief Verifies redirect to login when session is empty.
     */
    @Test
    void shifts_shouldRedirectWhenNoUser() {

        when(session.getAttribute("user")).thenReturn(null);

        Model model = new ConcurrentModel();

        String view = controller.viewMyShifts(session, model);

        assertEquals("redirect:/login", view);
    }

    /**
     * @brief Verifies shift and vacation data are loaded for buyer.
     */
    @Test
    void shifts_shouldLoad() {

        Buyer buyer = new Buyer();
        buyer.setId(1L);

        when(session.getAttribute("user")).thenReturn(buyer);
        when(shiftService.getShiftsForEmployee(1L)).thenReturn(List.of());
        when(vacationService.getApprovedRequestsForEmployee(1))
                .thenReturn(List.of());

        Model model = new ConcurrentModel();

        String view = controller.viewMyShifts(session, model);

        assertEquals("employee/my-shifts", view);
        assertEquals("My Shifts",
                model.getAttribute("pageTitle"));
    }
}