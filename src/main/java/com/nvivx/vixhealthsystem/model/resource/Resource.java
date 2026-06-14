package com.nvivx.vixhealthsystem.model.resource;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Represents a resource stored and managed by a medical facility.
 * <p>
 * Resources may include medical supplies, medications,
 * disposable equipment and other inventory items.
 * <p>
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
     * Creates a resource with the specified details.
     *
     * @param name        the resource name
     * @param description the resource description
     * @param price       the unit price
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

    /// @cond INTERNAL
    /**
     * Returns the unique resource identifier.
     *
     * @return the resource ID
     */
    public Long getId() {
        return id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the unique resource identifier.
     *
     * @param id the resource ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the resource name.
     *
     * @return the resource name
     */
    public String getName() {
        return name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the resource name.
     *
     * @param name the resource name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the resource description.
     *
     * @return the resource description
     */
    public String getDescription() {
        return description;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the resource description.
     *
     * @param description the resource description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the unit price of the resource.
     *
     * @return the unit price
     */
    public BigDecimal getPrice() {
        return price;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the unit price of the resource.
     *
     * @param price the unit price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    /// @endcond
}
