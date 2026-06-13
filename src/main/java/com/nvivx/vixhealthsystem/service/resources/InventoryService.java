package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.resource.Storage;
import com.nvivx.vixhealthsystem.repository.ResourceRepository;
import com.nvivx.vixhealthsystem.repository.StorageRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for inventory management (FR5.5)
 * Used by Purchasing Department / Buyer to manage hospital resources
 */
@Service
@Transactional(readOnly = true)
public class InventoryService {

    private final ResourceRepository resourceRepository;
    private final StorageRepository storageRepository;
    private final AuditService auditService;

    // Low stock threshold (can be configured per resource in the future)
    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 50;

    public InventoryService(ResourceRepository resourceRepository,
                            StorageRepository storageRepository,
                            AuditService auditService) {
        this.resourceRepository = resourceRepository;
        this.storageRepository = storageRepository;
        this.auditService = auditService;
    }

    /**
     * Get all resources in the system
     */
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    /**
     * Get resource by ID
     */
    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }

    /**
     * Search resources by name
     */
    public List<Resource> searchResourcesByName(String name) {
        return resourceRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Create a new resource
     */
    @Transactional
    public Resource createResource(String name, String description, BigDecimal price) {
        Resource resource = new Resource(name, description, price);
        Resource saved = resourceRepository.save(resource);

        auditService.log("CREATE_RESOURCE", "Resource", String.valueOf(saved.getId()),
                "Created resource: " + name + " (Price: €" + price + ")");

        return saved;
    }

    /**
     * Update resource details
     */
    @Transactional
    public Resource updateResource(Long id, String name, String description, BigDecimal price) {
        Resource resource = getResourceById(id);

        if (name != null) resource.setName(name);
        if (description != null) resource.setDescription(description);
        if (price != null) resource.setPrice(price);

        Resource saved = resourceRepository.save(resource);

        auditService.log("UPDATE_RESOURCE", "Resource", String.valueOf(id),
                "Updated resource: " + name);

        return saved;
    }

    /**
     * Delete a resource (only if not used in any storage)
     */
    @Transactional
    public void deleteResource(Long id) {
        Resource resource = getResourceById(id);

        // Check if resource is used in any storage
        // This would require checking StorageResources table
        // For now, we'll just delete and log

        resourceRepository.delete(resource);

        auditService.log("DELETE_RESOURCE", "Resource", String.valueOf(id),
                "Deleted resource: " + resource.getName());
    }

    /**
     * Get inventory for a specific storage facility
     */
    public Map<Resource, Integer> getStorageInventory(Long storageId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found with id: " + storageId));

        Map<Long, Resource> resourceById = resourceRepository.findAll().stream()
                .collect(Collectors.toMap(Resource::getId, r -> r));

        Map<Resource, Integer> result = new HashMap<>();
        for (Map.Entry<Resource, Integer> entry : storage.getResources().entrySet()) {
            Resource fullyLoaded = resourceById.get(entry.getKey().getId());
            if (fullyLoaded != null) {
                result.put(fullyLoaded, entry.getValue());
            }
        }
        return result;
    }

    /**
     * Get all resources with their quantities across all storages
     * Used for FR7.2 - Compound resource availability
     */
    public Map<Resource, Integer> getTotalInventory() {
        // Load all Resource entities fully within this transaction so they are not
        // Hibernate proxies when accessed by the controller after the session closes.
        Map<Long, Resource> resourceById = resourceRepository.findAll().stream()
                .collect(Collectors.toMap(Resource::getId, r -> r));

        Map<Resource, Integer> totalInventory = new HashMap<>();

        for (Storage storage : storageRepository.findAll()) {
            for (Map.Entry<Resource, Integer> entry : storage.getResources().entrySet()) {
                // entry.getKey() may be a proxy; get its ID (safe without initialization)
                Long id = entry.getKey().getId();
                Resource fullyLoaded = resourceById.get(id);
                if (fullyLoaded != null) {
                    totalInventory.merge(fullyLoaded, entry.getValue(), Integer::sum);
                }
            }
        }

        return totalInventory;
    }

    /**
     * Get low stock resources (FR5.5 - Inventory visibility and alerts)
     * Resources with quantity below threshold are considered low stock
     */
    public List<ResourceWithQuantity> getLowStockResources() {
        Map<Resource, Integer> totalInventory = getTotalInventory();

        return totalInventory.entrySet().stream()
                .filter(entry -> entry.getValue() < DEFAULT_LOW_STOCK_THRESHOLD)
                .map(entry -> new ResourceWithQuantity(
                        entry.getKey(),
                        entry.getValue(),
                        DEFAULT_LOW_STOCK_THRESHOLD,
                        entry.getValue() < DEFAULT_LOW_STOCK_THRESHOLD
                ))
                .collect(Collectors.toList());
    }

    /**
     * Add quantity to a resource in storage via a buyer (UC27).
     * Uses the Buyer domain method, which delegates to Storage domain logic.
     */
    @Transactional
    public void addResourceToStorage(Buyer buyer, Long storageId, Long resourceId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found: " + storageId));

        Resource resource = getResourceById(resourceId);

        // Domain: buyer adds resource to storage via model method
        buyer.addResource(storage, resource, quantity);
        storageRepository.save(storage);

        auditService.log("ADD_RESOURCE_TO_STORAGE", "Storage", String.valueOf(storageId),
                "Added " + quantity + " units of " + resource.getName() + " to storage");
    }

    /**
     * Overload kept for internal/system use where no specific buyer actor is present.
     */
    @Transactional
    public void addResourceToStorage(Long storageId, Long resourceId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found: " + storageId));
        Resource resource = getResourceById(resourceId);
        storage.addResource(resource, quantity);
        storageRepository.save(storage);
        auditService.log("ADD_RESOURCE_TO_STORAGE", "Storage", String.valueOf(storageId),
                "Added " + quantity + " units of " + resource.getName() + " to storage");
    }

    /**
     * Remove quantity from a resource in storage via an employee (UC25).
     * Uses the Employee domain method, which delegates to Storage domain logic.
     */
    @Transactional
    public void removeResourceFromStorage(Employee employee, Long storageId, Long resourceId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found: " + storageId));

        Resource resource = getResourceById(resourceId);

        try {
            // Domain: employee takes resource from storage via model method
            employee.takeResource(storage, resource, quantity);
            storageRepository.save(storage);

            auditService.log("REMOVE_RESOURCE_FROM_STORAGE", "Storage", String.valueOf(storageId),
                    "Removed " + quantity + " units of " + resource.getName() + " from storage");
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove resource: " + e.getMessage(), e);
        }
    }

    /**
     * Overload kept for internal/system use where no specific employee actor is present.
     */
    @Transactional
    public void removeResourceFromStorage(Long storageId, Long resourceId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found: " + storageId));
        Resource resource = getResourceById(resourceId);
        try {
            storage.removeResource(resource, quantity);
            storageRepository.save(storage);
            auditService.log("REMOVE_RESOURCE_FROM_STORAGE", "Storage", String.valueOf(storageId),
                    "Removed " + quantity + " units of " + resource.getName() + " from storage");
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove resource: " + e.getMessage(), e);
        }
    }

    /**
     * Check if a resource is low stock across all storages
     */
    public boolean isResourceLowStock(Long resourceId) {
        Map<Resource, Integer> totalInventory = getTotalInventory();

        return totalInventory.entrySet().stream()
                .filter(entry -> entry.getKey().getId().equals(resourceId))
                .anyMatch(entry -> entry.getValue() < DEFAULT_LOW_STOCK_THRESHOLD);
    }

    /**
     * Get total quantity of a specific resource across all storages
     */
    public int getTotalResourceQuantity(Long resourceId) {
        Map<Resource, Integer> totalInventory = getTotalInventory();

        return totalInventory.entrySet().stream()
                .filter(entry -> entry.getKey().getId().equals(resourceId))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    /**
     * Helper class for resource with quantity info
     */
    public static class ResourceWithQuantity {
        private final Resource resource;
        private final int quantity;
        private final int threshold;
        private final boolean lowStock;

        public ResourceWithQuantity(Resource resource, int quantity, int threshold, boolean lowStock) {
            this.resource = resource;
            this.quantity = quantity;
            this.threshold = threshold;
            this.lowStock = lowStock;
        }

        public Resource getResource() { return resource; }
        public int getQuantity() { return quantity; }
        public int getThreshold() { return threshold; }
        public boolean isLowStock() { return lowStock; }

        // Convenience getters for Thymeleaf templates
        public Long getId() { return resource.getId(); }
        public String getName() { return resource.getName(); }
        public String getDescription() { return resource.getDescription(); }
        public BigDecimal getPrice() { return resource.getPrice(); }
    }
}