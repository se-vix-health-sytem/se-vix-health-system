package com.nvivx.vixhealthsystem.model.resource;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Represents a resource stored and managed by a medical facility.
 *
 * Resources may include medical supplies, medications,
 * disposable equipment and other inventory items.
 *
 * Quantities are managed through the StorageResources table.
 *
 * @see Storage
 */
@Entity
@Table(name = "Resources")
public class Resource {

    /**
     * Unique resource identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Resource name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Resource description.
     */
    @Column(name = "description")
    private String description;

    /**
     * Unit price of the resource.
     */
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Resource() {
    }

    /**
     * Creates a resource.
     *
     * @param name resource name
     * @param description resource description
     * @param price unit price
     */
    public Resource(
            String name,
            String description,
            BigDecimal price
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // =====================================================
    // EQUALS & HASHCODE
    // =====================================================

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Resource other)) {
            return false;
        }

        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : 0;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}