package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.mock.MockDatabase;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyerService {

    private final MockDatabase mockDatabase;

    public BuyerService(MockDatabase mockDatabase) {
        this.mockDatabase = mockDatabase;
    }

    // UC26: View all resources in inventory
    public List<Resource> getAllResources() {
        return mockDatabase.findAllResources();
    }

    // UC26: View only low stock resources
    public List<Resource> getLowStockResources() {
        return mockDatabase.findLowStockResources();
    }

    // UC27: Add new resource to inventory
    public Resource addResource(String name, String description, int quantity, float price, String unit) {
        // Check if resource already exists (by name)
        Resource existing = mockDatabase.findAllResources().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            // If exists, increase quantity
            existing.setQuantity(existing.getQuantity() + quantity);
            return mockDatabase.saveResource(existing);
        } else {
            // Create new resource
            Resource newResource = new Resource(name, description, quantity, price, unit);
            return mockDatabase.saveResource(newResource);
        }
    }

    // Update resource quantity (when items are used or restocked)
    public void updateQuantity(int resourceId, int newQuantity) {
        Resource resource = mockDatabase.findResourceById(resourceId);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found with id: " + resourceId);
        }
        resource.setQuantity(newQuantity);
        mockDatabase.saveResource(resource);
    }

    // Get a single resource by ID
    public Resource getResourceById(int resourceId) {
        return mockDatabase.findResourceById(resourceId);
    }
}