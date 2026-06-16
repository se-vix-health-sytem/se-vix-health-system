package com.nvivx.vixhealthsystem.model.facility;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Location.
 *
 * Verifies that geographic coordinates can be set via both constructors and
 * individual setters, and that null coordinates are accepted (representing an
 * unlocated facility). Plain JUnit : no Spring context loaded.
 *
 * @see Location
 */
class LocationTest {

    /**
     * Verifies that a default-constructed Location starts with null
     *        coordinates, indicating the position has not yet been set.
     */
    @Test
    void defaultConstructor_ShouldCreateEmptyLocation() {
        Location location = new Location();
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
    }

    /**
     * Verifies that the two-argument constructor stores both latitude
     *        and longitude so callers can create a located facility in one step.
     */
    @Test
    void parameterizedConstructor_ShouldSetCoordinates() {
        Location location = new Location(45.4642, 9.1900);
        assertEquals(45.4642, location.getLatitude());
        assertEquals(9.1900, location.getLongitude());
    }

    /**
     * Verifies that individual coordinate setters update the values
     *        independently of the constructor path.
     */
    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Location location = new Location();
        location.setLatitude(46.066422);
        location.setLongitude(11.119928);

        assertEquals(46.066422, location.getLatitude());
        assertEquals(11.119928, location.getLongitude());
    }

    /**
     * Verifies that coordinates may be explicitly set to null, which
     *        is a valid state for a facility whose position is not yet known.
     */
    @Test
    void coordinates_ShouldAllowNull() {
        Location location = new Location();
        location.setLatitude(null);
        location.setLongitude(null);

        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
    }
}