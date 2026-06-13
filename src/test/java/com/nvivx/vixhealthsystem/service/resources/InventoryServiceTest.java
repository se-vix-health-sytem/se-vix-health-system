package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.resource.Storage;
import com.nvivx.vixhealthsystem.repository.ResourceRepository;
import com.nvivx.vixhealthsystem.repository.StorageRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryService.
 *
 * These tests use Mockito mocks instead of the real database.
 *
 * Arrange = prepare fake data and mock behavior
 * Act = call the method being tested
 * Assert = check the result
 * Verify = check that mocks were called correctly
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InventoryService service;

    @Test
    void shouldReturnAllResources() {
        // Arrange
        Resource r1 = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        r1.setId(1L);

        Resource r2 = new Resource("Masks", "Surgical masks", BigDecimal.valueOf(10));
        r2.setId(2L);

        when(resourceRepository.findAll())
                .thenReturn(List.of(r1, r2));

        // Act
        List<Resource> result = service.getAllResources();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Gloves", result.get(0).getName());

        // Verify
        verify(resourceRepository).findAll();
    }

    @Test
    void shouldGetResourceById() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        // Act
        Resource result = service.getResourceById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Gloves", result.getName());

        // Verify
        verify(resourceRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenResourceNotFound() {
        // Arrange
        when(resourceRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.getResourceById(99L)
        );

        assertTrue(exception.getMessage().contains("Resource not found"));

        // Verify
        verify(resourceRepository).findById(99L);
    }

    @Test
    void shouldSearchResourcesByName() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        when(resourceRepository.findByNameContainingIgnoreCase("glo"))
                .thenReturn(List.of(resource));

        // Act
        List<Resource> result = service.searchResourcesByName("glo");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Gloves", result.get(0).getName());

        // Verify
        verify(resourceRepository).findByNameContainingIgnoreCase("glo");
    }

    @Test
    void shouldCreateResource() {
        // Arrange
        Resource saved = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        saved.setId(1L);

        when(resourceRepository.save(any(Resource.class)))
                .thenReturn(saved);

        // Act
        Resource result = service.createResource(
                "Gloves",
                "Medical gloves",
                BigDecimal.valueOf(5)
        );

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Gloves", result.getName());

        // Verify
        verify(resourceRepository).save(any(Resource.class));
        verify(auditService).log(
                eq("CREATE_RESOURCE"),
                eq("Resource"),
                eq("1"),
                contains("Created resource")
        );
    }

    @Test
    void shouldUpdateResource() {
        // Arrange
        Resource existing = new Resource("Old", "Old description", BigDecimal.valueOf(3));
        existing.setId(1L);

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(resourceRepository.save(existing))
                .thenReturn(existing);

        // Act
        Resource result = service.updateResource(
                1L,
                "New",
                "New description",
                BigDecimal.valueOf(10)
        );

        // Assert
        assertEquals("New", result.getName());
        assertEquals("New description", result.getDescription());
        assertEquals(BigDecimal.valueOf(10), result.getPrice());

        // Verify
        verify(resourceRepository).findById(1L);
        verify(resourceRepository).save(existing);
        verify(auditService).log(
                eq("UPDATE_RESOURCE"),
                eq("Resource"),
                eq("1"),
                contains("Updated resource")
        );
    }

    @Test
    void shouldDeleteResource() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        // Act
        service.deleteResource(1L);

        // Assert + Verify
        verify(resourceRepository).findById(1L);
        verify(resourceRepository).delete(resource);
        verify(auditService).log(
                eq("DELETE_RESOURCE"),
                eq("Resource"),
                eq("1"),
                contains("Deleted resource")
        );
    }

    @Test
    void shouldReturnStorageInventory() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        Storage storage = new Storage();
        storage.setId(10L);
        storage.addResource(resource, 30);

        when(storageRepository.findById(10L))
                .thenReturn(Optional.of(storage));

        when(resourceRepository.findAll())
                .thenReturn(List.of(resource));

        // Act
        Map<Resource, Integer> result = service.getStorageInventory(10L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(30, result.get(resource));

        // Verify
        verify(storageRepository).findById(10L);
        verify(resourceRepository).findAll();
    }

    @Test
    void shouldReturnTotalInventoryAcrossStorages() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        Storage storage1 = new Storage();
        storage1.setId(10L);
        storage1.addResource(resource, 30);

        Storage storage2 = new Storage();
        storage2.setId(20L);
        storage2.addResource(resource, 40);

        when(resourceRepository.findAll())
                .thenReturn(List.of(resource));

        when(storageRepository.findAll())
                .thenReturn(List.of(storage1, storage2));

        // Act
        Map<Resource, Integer> result = service.getTotalInventory();

        // Assert
        assertEquals(1, result.size());
        assertEquals(70, result.get(resource));

        // Verify
        verify(resourceRepository).findAll();
        verify(storageRepository).findAll();
    }

    @Test
    void shouldReturnLowStockResources() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        Storage storage = new Storage();
        storage.setId(10L);
        storage.addResource(resource, 20);

        when(resourceRepository.findAll())
                .thenReturn(List.of(resource));

        when(storageRepository.findAll())
                .thenReturn(List.of(storage));

        // Act
        List<InventoryService.ResourceWithQuantity> result =
                service.getLowStockResources();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Gloves", result.get(0).getName());
        assertEquals(20, result.get(0).getQuantity());
        assertTrue(result.get(0).isLowStock());

        // Verify
        verify(resourceRepository).findAll();
        verify(storageRepository).findAll();
    }

    @Test
    void shouldAddResourceToStorage() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        Storage storage = new Storage();
        storage.setId(10L);

        when(storageRepository.findById(10L))
                .thenReturn(Optional.of(storage));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        // Act
        service.addResourceToStorage(10L, 1L, 25);

        // Assert
        assertEquals(25, storage.getResources().get(resource));

        // Verify
        verify(storageRepository).findById(10L);
        verify(resourceRepository).findById(1L);
        verify(storageRepository).save(storage);
        verify(auditService).log(
                eq("ADD_RESOURCE_TO_STORAGE"),
                eq("Storage"),
                eq("10"),
                contains("Added 25 units")
        );
    }

    @Test
    void shouldThrowWhenAddingNegativeQuantity() {
        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> service.addResourceToStorage(10L, 1L, -5)
        );

        // Verify
        verifyNoInteractions(storageRepository);
        verifyNoInteractions(resourceRepository);
    }

    @Test
    void shouldRemoveResourceFromStorage() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        Storage storage = new Storage();
        storage.setId(10L);
        storage.addResource(resource, 30);

        when(storageRepository.findById(10L))
                .thenReturn(Optional.of(storage));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        // Act
        service.removeResourceFromStorage(10L, 1L, 10);

        // Assert
        assertEquals(20, storage.getResources().get(resource));

        // Verify
        verify(storageRepository).findById(10L);
        verify(resourceRepository).findById(1L);
        verify(storageRepository).save(storage);
        verify(auditService).log(
                eq("REMOVE_RESOURCE_FROM_STORAGE"),
                eq("Storage"),
                eq("10"),
                contains("Removed 10 units")
        );
    }

    @Test
    void shouldThrowWhenRemovingTooMuchResource() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        Storage storage = new Storage();
        storage.setId(10L);
        storage.addResource(resource, 5);

        when(storageRepository.findById(10L))
                .thenReturn(Optional.of(storage));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.removeResourceFromStorage(10L, 1L, 10)
        );

        assertTrue(exception.getMessage().contains("Failed to remove resource"));

        // Verify
        verify(storageRepository).findById(10L);
        verify(resourceRepository).findById(1L);
        verify(storageRepository, never()).save(any());
    }

    @Test
    void shouldCheckIfResourceIsLowStock() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        Storage storage = new Storage();
        storage.setId(10L);
        storage.addResource(resource, 20);

        when(resourceRepository.findAll())
                .thenReturn(List.of(resource));

        when(storageRepository.findAll())
                .thenReturn(List.of(storage));

        // Act
        boolean result = service.isResourceLowStock(1L);

        // Assert
        assertTrue(result);

        // Verify
        verify(resourceRepository).findAll();
        verify(storageRepository).findAll();
    }

    @Test
    void shouldReturnTotalResourceQuantity() {
        // Arrange
        Resource resource = new Resource("Gloves", "Medical gloves", BigDecimal.valueOf(5));
        resource.setId(1L);

        Storage storage1 = new Storage();
        storage1.setId(10L);
        storage1.addResource(resource, 15);

        Storage storage2 = new Storage();
        storage2.setId(20L);
        storage2.addResource(resource, 35);

        when(resourceRepository.findAll())
                .thenReturn(List.of(resource));

        when(storageRepository.findAll())
                .thenReturn(List.of(storage1, storage2));

        // Act
        int result = service.getTotalResourceQuantity(1L);

        // Assert
        assertEquals(50, result);

        // Verify
        verify(resourceRepository).findAll();
        verify(storageRepository).findAll();
    }
}
