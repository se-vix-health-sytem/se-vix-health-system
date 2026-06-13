package com.nvivx.vixhealthsystem.model.facility;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @Test
    void defaultConstructor_ShouldCreateEmptyLocation() {
        Location location = new Location();
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
    }

    @Test
    void parameterizedConstructor_ShouldSetCoordinates() {
        Location location = new Location(45.4642, 9.1900);
        assertEquals(45.4642, location.getLatitude());
        assertEquals(9.1900, location.getLongitude());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Location location = new Location();
        location.setLatitude(46.066422);
        location.setLongitude(11.119928);

        assertEquals(46.066422, location.getLatitude());
        assertEquals(11.119928, location.getLongitude());
    }

    @Test
    void coordinates_ShouldAllowNull() {
        Location location = new Location();
        location.setLatitude(null);
        location.setLongitude(null);

        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
    }
}