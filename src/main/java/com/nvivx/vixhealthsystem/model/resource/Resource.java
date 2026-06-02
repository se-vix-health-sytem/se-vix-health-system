package com.nvivx.vixhealthsystem.model.resource;

public class Resource {
    private int id;
    private String name;
    private String description;
    private float price;

    public Resource(int id, String name, String description, int quantity, float price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public boolean equals(Object r) {
        if (r instanceof Resource other ) {
            return this.id ==other.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
