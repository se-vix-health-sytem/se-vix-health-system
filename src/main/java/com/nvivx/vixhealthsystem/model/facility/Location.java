package com.nvivx.vixhealthsystem.model.facility;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the geographic coordinates of a medical facility.
 * <p>
 * This class is embedded inside MedicalFacility and does not have
 * its own database table.
 *
 * @see MedicalFacility
 */
@Getter
@Setter
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

}