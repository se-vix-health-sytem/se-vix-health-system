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
 * @brief Manages hospital resource inventory — stock levels across all storage facilities,
 *        low-stock detection, and resource intake/consumption flows.  Covers FR5.5 (inventory
 *        visibility) and UC25 (employee resource consumption) and UC27 (buyer restocking).
 *
 * Annotated {@code @Transactional(readOnly=true)} at the class level; write methods override
 * with {@code @Transactional}.
 *
 * Two actor-aware overloads exist for stock mutations: the domain-method path (accepts an
 * {@link Employee} or {@link Buyer} actor) enforces business rules inside the model, while
 * the ID-only overloads are provided for internal/system use when no actor is in scope.
 *
 * @see ResourceTakeLogStore
 * @see com.nvivx.vixhealthsystem.model.resource.Storage
 * @see AuditService
 */
@Service
@Transactional(readOnly = true)
public class InventoryService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final ResourceRepository resourceRepository;
    private final StorageRepository storageRepository;
    private final AuditService auditService;
    private final ResourceTakeLogStore takeLogStore;

    /** Quantity below which a resource is flagged as low stock (FR5.5). */
    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 50;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with all required repositories and collaborators.
     *
     * @param resourceRepository  Persistence layer for {@link Resource} catalogue entries.
     * @param storageRepository   Persistence layer for {@link Storage} facilities and their stock maps.
     * @param auditService        Records every stock mutation for traceability (NFR02).
     * @param takeLogStore        JSON-backed log of every resource takeout event (FR5.5).
     */
    public InventoryService(ResourceRepository resourceRepository,
                            StorageRepository storageRepository,
                            AuditService auditService,
                            ResourceTakeLogStore takeLogStore) {
        this.resourceRepository = resourceRepository;
        this.storageRepository = storageRepository;
        this.auditService = auditService;
        this.takeLogStore = takeLogStore;
    }

    // =========================================================
    // READ OPERATIONS — RESOURCES
    // =========================================================

    /**
     * Returns all resource catalogue entries in the system.
     *
     * @return Non-null list; empty when no resources have been defined.
     */
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    /**
     * Looks up a resource by its catalogue ID, throwing when absent.
     *
     * @param id  Resource primary key.
     * @return    The matching {@link Resource}; never {@code null}.
     * @throws RuntimeException When no resource with the given ID exists.
     */
    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }

    /**
     * Finds resources whose name contains the given substring (case-insensitive).
     *
     * @param name  Search term.
     * @return      Non-null list of matching resources.
     */
    public List<Resource> searchResourcesByName(String name) {
        return resourceRepository.findByNameContainingIgnoreCase(name);
    }

    // =========================================================
    // WRITE OPERATIONS — RESOURCE CATALOGUE
    // =========================================================

    /**
     * Adds a new resource to the catalogue with zero initial stock.
     *
     * @param name         Display name of the resource.
     * @param description  Optional description; may be {@code null}.
     * @param price        Unit purchase price in euros.
     * @return             The persisted {@link Resource} with a generated ID.
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
     * Updates catalogue metadata for an existing resource; {@code null} parameters are ignored.
     *
     * @param id           ID of the resource to update.
     * @param name         New display name; {@code null} keeps the existing value.
     * @param description  New description; {@code null} keeps the existing value.
     * @param price        New unit price; {@code null} keeps the existing value.
     * @return             The updated and re-persisted {@link Resource}.
     * @throws RuntimeException When no resource with {@code id} exists.
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
     * Removes a resource from the catalogue.
     *
     * Storage-usage validation is not yet enforced; callers should check usage before
     * calling this method to avoid orphaned storage-resource references.
     *
     * @param id  ID of the resource to delete.
     * @throws RuntimeException When no resource with {@code id} exists.
     */
    @Transactional
    public void deleteResource(Long id) {
        Resource resource = getResourceById(id);

        boolean usedInStorage = storageRepository.findAll().stream()
                .anyMatch(storage -> storage.getResources().containsKey(resource));

        if (usedInStorage) {
            throw new IllegalStateException(
                    "Cannot delete resource '" + resource.getName() +
                            "' because it is still present in one or more storages."
            );
        }

        resourceRepository.delete(resource);

        auditService.log("DELETE_RESOURCE", "Resource", String.valueOf(id),
                "Deleted resource: " + resource.getName());
    }

    // =========================================================
    // READ OPERATIONS — STOCK
    // =========================================================

    /**
     * Returns the stock map for a specific storage facility.
     *
     * Re-fetches fully-loaded {@link Resource} objects from the repository so that the
     * returned map contains rich entities rather than JPA proxy stubs from the
     * {@code @ElementCollection}.
     *
     * @param storageId  ID of the storage facility.
     * @return           Map of {@link Resource} to quantity; never {@code null}.
     * @throws RuntimeException When no storage with {@code storageId} exists.
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
     * Aggregates stock quantities across all storage facilities (FR7.2).
     *
     * Iterates all {@link Storage} entities and merges their resource maps by summing
     * quantities for the same resource.  {@code @ElementCollection(EAGER)} means each
     * storage's map is loaded in one additional query.
     *
     * @return Map of {@link Resource} to total quantity across all storages; never {@code null}.
     */
    public Map<Resource, Integer> getTotalInventory() {
        Map<Resource, Integer> totalInventory = new HashMap<>();
        // Iterate all storages — @ElementCollection(EAGER) loads each map in one query per storage
        for (Storage storage : storageRepository.findAll()) {
            storage.getResources().forEach((resource, qty) ->
                    totalInventory.merge(resource, qty, Integer::sum));
        }
        return totalInventory;
    }

    /**
     * Returns all resources whose total stock across all facilities falls below
     *        {@link #DEFAULT_LOW_STOCK_THRESHOLD} (FR5.5 — inventory alerts).
     *
     * @return Non-null list of {@link ResourceWithQuantity} wrappers; empty when all resources
     *         are adequately stocked.
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
    public void addResourceToStorage(Buyer buyer, Long resourceId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Resource resource = getResourceById(resourceId);

        // Domain: buyer navigates department → facility → storage internally
        buyer.addResource(resource, quantity);

        Storage storage = buyer.getDepartment().getMedicalFacility().getStorage();
        storageRepository.save(storage);

        auditService.log("ADD_RESOURCE_TO_STORAGE", "Storage",
                String.valueOf(storage.getId()),
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
    public void removeResourceFromStorage(Employee employee, Long resourceId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Resource resource = getResourceById(resourceId);

        try {
            // Domain: employee navigates department → facility → storage internally
            employee.takeResource(resource, quantity);

            Storage storage = employee.getDepartment().getMedicalFacility().getStorage();
            storageRepository.save(storage);

            String role = employee.getClass().getSimpleName();
            takeLogStore.log(employee.getId(),
                    employee.getName() + " " + employee.getSurname(), role,
                    resource.getId(), resource.getName(), storage.getId(), quantity);

            auditService.log("REMOVE_RESOURCE_FROM_STORAGE", "Storage", String.valueOf(storage.getId()),
                    employee.getName() + " " + employee.getSurname() + " (" + role + ") took "
                    + quantity + " units of " + resource.getName());
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
     * Checks whether a specific resource is below the low-stock threshold across all facilities.
     *
     * @param resourceId  Catalogue ID of the resource to check.
     * @return            {@code true} when the aggregated quantity is below
     *                    {@link #DEFAULT_LOW_STOCK_THRESHOLD}.
     */
    public boolean isResourceLowStock(Long resourceId) {
        Map<Resource, Integer> totalInventory = getTotalInventory();

        return totalInventory.entrySet().stream()
                .filter(entry -> entry.getKey().getId().equals(resourceId))
                .anyMatch(entry -> entry.getValue() < DEFAULT_LOW_STOCK_THRESHOLD);
    }

    /**
     * Returns the total quantity of a resource summed across all storage facilities.
     *
     * @param resourceId  Catalogue ID of the resource.
     * @return            Total stock; {@code 0} when the resource is not stocked anywhere.
     */
    public int getTotalResourceQuantity(Long resourceId) {
        Map<Resource, Integer> totalInventory = getTotalInventory();

        return totalInventory.entrySet().stream()
                .filter(entry -> entry.getKey().getId().equals(resourceId))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    // =========================================================
    // INNER CLASSES
    // =========================================================

    /**
     * View-model wrapper that pairs a {@link Resource} with its current stock level
     *        and low-stock flag, used by Thymeleaf inventory templates.
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