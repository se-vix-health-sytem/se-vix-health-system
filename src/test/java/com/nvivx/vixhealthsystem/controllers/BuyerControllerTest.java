/* package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.staff.BuyerController;
import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
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

class BuyerControllerTest {

    private InventoryService inventoryService;
    private EmployeeService employeeService;
    private ResourceTakeLogStore takeLogStore;
    private ShiftService shiftService;
    private VacationService vacationService;

    private BuyerController controller;
    private HttpSession session;

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

    // ================= DASHBOARD =================

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
        assertEquals("Purchasing Department Dashboard", model.getAttribute("pageTitle"));
        assertEquals("John Doe", model.getAttribute("buyerName"));
        assertEquals(0, model.getAttribute("lowStockCount"));
        assertEquals(0, model.getAttribute("totalResources"));
    }

    // ================= INVENTORY =================

    @Test
    void viewInventory_shouldReturnPage() {
        when(inventoryService.getTotalInventory()).thenReturn(Map.of());

        Model model = new ConcurrentModel();

        String view = controller.viewInventory(model, session);

        assertEquals("buyer/inventory", view);
        assertEquals("Inventory Management", model.getAttribute("pageTitle"));
    }

    // ================= LOW STOCK =================

    @Test
    void viewLowStock_shouldReturnList() {
        InventoryService.ResourceWithQuantity item =
                mock(InventoryService.ResourceWithQuantity.class);

        when(item.getResource()).thenReturn(new com.nvivx.vixhealthsystem.model.resource.Resource());
        when(item.getQuantity()).thenReturn(10);

        when(inventoryService.getLowStockResources()).thenReturn(List.of(item));

        Model model = new ConcurrentModel();

        String view = controller.viewLowStock(model);

        assertEquals("buyer/inventory", view);
        assertTrue((Boolean) model.getAttribute("isLowStockView"));
        assertNotNull(model.getAttribute("resources"));
    }

    // ================= ADD RESOURCE =================

    @Test
    void addResource_shouldReturnSuccessMessage() {
        Buyer buyer = new Buyer();
        buyer.setId(1L);

        Employee emp = buyer;
        when(session.getAttribute("user")).thenReturn(emp);

        var resource = mock(com.nvivx.vixhealthsystem.model.resource.Resource.class);
        when(resource.getId()).thenReturn(10L);

        when(inventoryService.createResource(anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(resource);

        when(employeeService.findById(anyLong())).thenReturn(buyer);

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
        assertTrue(model.getAttribute("message").toString().contains("Resource Added"));
    }

    // ================= UPDATE QUANTITY =================

    @Test
    void updateQuantity_add_shouldWork() {
        Model model = new ConcurrentModel();

        String view = controller.updateQuantity(1L, 5, "add", model);

        assertEquals("buyer/result", view);
        assertTrue(model.getAttribute("message").toString().contains("Added"));

        verify(inventoryService).addResourceToStorage(1L, 1L, 5);
    }

    @Test
    void updateQuantity_remove_shouldWork() {
        Model model = new ConcurrentModel();

        String view = controller.updateQuantity(1L, 3, "remove", model);

        assertEquals("buyer/result", view);
        assertTrue(model.getAttribute("message").toString().contains("Removed"));

        verify(inventoryService).removeResourceFromStorage(1L, 1L, 3);
    }

    // ================= RESOURCE LOG =================

    @Test
    void resourceLog_shouldLoad() {
        when(takeLogStore.getAll()).thenReturn(List.of());

        Model model = new ConcurrentModel();

        String view = controller.viewResourceLog(model);

        assertEquals("buyer/resource-log", view);
        assertEquals("Resource Take Log", model.getAttribute("pageTitle"));
    }

    // ================= PROFILE GUARD =================

    @Test
    void profile_shouldRedirectWhenNoUser() {
        when(session.getAttribute("user")).thenReturn(null);

        Model model = new ConcurrentModel();

        String view = controller.viewProfile(session, model);

        assertEquals("redirect:/login", view);
    }

    // ================= SHIFTS =================

    @Test
    void shifts_shouldRedirectWhenNoUser() {
        when(session.getAttribute("user")).thenReturn(null);

        Model model = new ConcurrentModel();

        String view = controller.viewMyShifts(session, model);

        assertEquals("redirect:/login", view);
    }
}
*/