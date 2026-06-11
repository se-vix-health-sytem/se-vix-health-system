package com.nvivx.vixhealthsystem.model.facility;

import jakarta.persistence.Embeddable;

/**
 * Represents the geographic coordinates of a medical facility.
 * <p>
 * This class is embedded inside MedicalFacility and does not have
 * its own database table.
 *
 * @see MedicalFacility
 */
@Embeddable
public class Location {

    /** Geographic latitude coordinate. */
    private Double latitude;

    /** Geographic longitude coordinate. */
    private Double longitude;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Location() {
    }

    /**
     * Creates a Location with the specified coordinates.
     *
     * @param latitude the geographic latitude
     * @param longitude the geographic longitude
     */
    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the geographic latitude.
     *
     * @return the latitude coordinate
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the geographic latitude.
     *
     * @param latitude the latitude coordinate to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the geographic longitude.
     *
     * @return the longitude coordinate
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the geographic longitude.
     *
     * @param longitude the longitude coordinate to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}