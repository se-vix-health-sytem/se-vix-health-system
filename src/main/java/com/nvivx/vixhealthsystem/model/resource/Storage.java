package com.nvivx.vixhealthsystem.model.resource;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private int totalQuantity;
    private Map<Resource, Integer> resources;

    public Storage() {
        totalQuantity = 0;
        resources = new HashMap<>();
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    private void updateQuantity(int q) {
        totalQuantity += q;
    }

    public void addResource(Resource r, int q) {
        if (resources.containsKey(r)) {
            int current = resources.get(r);
            resources.put(r, current + q);
        } else {
            resources.put(r, q);
        }

        updateQuantity(q);
    }

    public void removeResource(Resource r, int q) throws Exception {
        if (resources.containsKey(r)) {
            int current = resources.get(r);
            resources.put(r, current - q);
        } else {
            throw new Exception("Resource not present");
        }

        updateQuantity(q);
    }

    public Map<Resource, Integer> getResources() {
        return resources;
    }
}
