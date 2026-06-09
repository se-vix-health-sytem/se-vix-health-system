package com.nvivx.vixhealthsystem.model.facility;

import jakarta.persistence.Embeddable;

/**
 * Represents the geographic coordinates of a medical facility.
 *
 * This class is embedded inside MedicalFacility and does not have
 * its own database table.
 *
 * @see MedicalFacility
 */
@Embeddable
public class Location {

    private Double latitude;

    private Double longitude;

    public Location() {
    }

    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}