package com.nvivx.vixhealthsystem.model.person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// Concrete subclass for testing abstract Person
class TestPerson extends Person {}

/**
 * @brief Unit tests for the abstract Person base class.
 *
 * Uses a minimal TestPerson subclass to exercise shared demographic fields and
 * the age-calculation helper. Plain JUnit : no Spring context loaded.
 *
 * @see Person
 */
class PersonTest {
    private TestPerson person;

    /** @brief Builds the fixture shared by all tests in this class. */
    @BeforeEach
    void setUp() {
        person = new TestPerson();
        person.setName("Mario");
        person.setSurname("Rossi");
        person.setBirthDate(LocalDate.of(1980, 1, 15));
        person.setBirthPlace("Milano");
        person.setGender('M');
        person.setEmail("mario.rossi@example.com");
        person.setPhoneNumber("+39 333 1234567");
    }

    /**
     * Verifies that all demographic fields round-trip through their
     *        setters and getters correctly.
     */
    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals("Mario", person.getName());
        assertEquals("Rossi", person.getSurname());
        assertEquals(LocalDate.of(1980, 1, 15), person.getBirthDate());
        assertEquals("Milano", person.getBirthPlace());
        assertEquals('M', person.getGender());
        assertEquals("mario.rossi@example.com", person.getEmail());
        assertEquals("+39 333 1234567", person.getPhoneNumber());
    }

    /**
     * Verifies that getAge returns the correct number of complete years
     *        from the birth date to today.
     */
    @Test
    void getAge_ShouldCalculateCorrectAge() {
        // Age calculation is based on current date, so we test with a known date
        LocalDate birthDate = LocalDate.now().minusYears(30);
        person.setBirthDate(birthDate);

        assertEquals(30, person.getAge());
    }

    /**
     * Verifies that getAge returns zero when no birth date is set,
     *        preventing a NullPointerException in age display views.
     */
    @Test
    void getAge_ShouldReturnZeroWhenBirthDateNull() {
        person.setBirthDate(null);
        assertEquals(0, person.getAge());
    }

    /**
     * Verifies that name and surname accept null values without throwing,
     *        supporting partial registration scenarios.
     */
    @Test
    void nameAndSurname_ShouldAllowNull() {
        person.setName(null);
        person.setSurname(null);

        assertNull(person.getName());
        assertNull(person.getSurname());
    }
}