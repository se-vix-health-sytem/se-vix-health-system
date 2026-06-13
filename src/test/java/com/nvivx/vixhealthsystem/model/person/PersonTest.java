package com.nvivx.vixhealthsystem.model.person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// Concrete subclass for testing abstract Person
class TestPerson extends Person {}

class PersonTest {
    private TestPerson person;

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

    @Test
    void getAge_ShouldCalculateCorrectAge() {
        // Age calculation is based on current date, so we test with a known date
        LocalDate birthDate = LocalDate.now().minusYears(30);
        person.setBirthDate(birthDate);

        assertEquals(30, person.getAge());
    }

    @Test
    void getAge_ShouldReturnZeroWhenBirthDateNull() {
        person.setBirthDate(null);
        assertEquals(0, person.getAge());
    }

    @Test
    void nameAndSurname_ShouldAllowNull() {
        person.setName(null);
        person.setSurname(null);

        assertNull(person.getName());
        assertNull(person.getSurname());
    }
}