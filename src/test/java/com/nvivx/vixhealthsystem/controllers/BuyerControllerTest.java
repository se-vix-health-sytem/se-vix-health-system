package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.staff.BuyerController;
import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.resources.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BuyerControllerTest {

    private final InventoryService inventoryService = mock(InventoryService.class);
    private final EmployeeService employeeService = mock(EmployeeService.class);
    private final BuyerController controller =
            new BuyerController(inventoryService, employeeService);

    // DASHBOARD

    @Test
    void shouldLoadDashboard() {
        Buyer buyer = new Buyer();
        buyer.setName("John");
        buyer.setSurname("Doe");

        when(employeeService.findAllBuyers()).thenReturn(List.of(buyer));
        when(inventoryService.getLowStockResources()).thenReturn(List.of());
        when(inventoryService.getAllResources()).thenReturn(List.of());

        Model model = new ConcurrentModel();

        String view = controller.dashboard(model);

        assertEquals("buyer/dashboard", view);
        assertEquals("Purchasing Department Dashboard", model.getAttribute("pageTitle"));
        assertEquals("John Doe", model.getAttribute("buyerName"));

        verify(employeeService).findAllBuyers();
        verify(inventoryService).getLowStockResources();
        verify(inventoryService).getAllResources();
    }

    //  INVENTORY

    @Test
    void shouldShowInventoryPage() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Gloves");
        resource.setDescription("Medical gloves");
        resource.setPrice(BigDecimal.valueOf(10));

        when(inventoryService.getTotalInventory())
                .thenReturn(Map.of(resource, 100));

        Model model = new ConcurrentModel();

        String view = controller.viewInventory(model);

        assertEquals("buyer/inventory", view);
        assertEquals("Inventory Management", model.getAttribute("pageTitle"));

        verify(inventoryService).getTotalInventory();
    }

    // LOW STOCK

    @Test
    void shouldShowLowStockPage() {
        InventoryService.ResourceWithQuantity item =
                mock(InventoryService.ResourceWithQuantity.class);

        Resource resource = new Resource();
        resource.setName("Masks");

        when(item.getResource()).thenReturn(resource);
        when(item.getQuantity()).thenReturn(20);
        when(inventoryService.getLowStockResources())
                .thenReturn(List.of(item));

        Model model = new ConcurrentModel();

        String view = controller.viewLowStock(model);

        assertEquals("buyer/inventory", view);
        assertTrue(model.containsAttribute("resources"));
    }

    // ADD RESOURCE

    @Test
    void shouldAddResourceSuccessfully() {
        Resource created = new Resource();
        created.setId(10L);

        when(inventoryService.createResource(anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(created);

        Model model = new ConcurrentModel();

        String view = controller.addResource(
                "Gloves",
                "Latex gloves",
                50,
                10f,
                "units",
                model
        );

        assertEquals("buyer/result", view);
        assertTrue(model.getAttribute("message").toString().contains("Resource Added"));

        verify(inventoryService).createResource(anyString(), anyString(), any(BigDecimal.class));
        verify(inventoryService).addResourceToStorage(1L, 10L, 50);
    }

    //  UPDATE

    @Test
    void shouldAddQuantity() {
        Model model = new ConcurrentModel();

        String view = controller.updateQuantity(1L, 10, "add", model);

        assertEquals("buyer/result", view);
        assertTrue(model.getAttribute("message").toString().contains("Added"));

        verify(inventoryService).addResourceToStorage(1L, 1L, 10);
    }

    @Test
    void shouldRemoveQuantity() {
        Model model = new ConcurrentModel();

        String view = controller.updateQuantity(1L, 5, "remove", model);

        assertEquals("buyer/result", view);
        assertTrue(model.getAttribute("message").toString().contains("Removed"));

        verify(inventoryService).removeResourceFromStorage(1L, 1L, 5);
    }
}