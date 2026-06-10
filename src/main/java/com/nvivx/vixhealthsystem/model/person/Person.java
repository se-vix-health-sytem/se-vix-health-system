package com.nvivx.vixhealthsystem.model.person;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

/**
 * Base class for all people in the system.
 * Contains personal and contact information shared by both patients and employees.
 * <p>
 * This class is marked as a MappedSuperclass, meaning its fields are inherited
 * by subclasses and mapped directly into their tables, but Person itself
 * does not have a dedicated database table.
 *
 * @see com.nvivx.vixhealthsystem.model.person.Patient
 * @see com.nvivx.vixhealthsystem.model.person.employee.Employee
 */
@Setter
@Getter
@MappedSuperclass
public abstract class Person {

    /**
     * Person's first name.
     * -- GETTER --
     *  Returns the first name.
     * <p>
     * -- SETTER --
     *  Sets the first name.
     *


     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Person's last name.
     * -- GETTER --
     *  Returns the surname.
     * <p>
     * -- SETTER --
     *  Sets the surname.
     *


     */
    @Column(name = "surname", nullable = false)
    private String surname;

    /**
     * Date of birth.
     * -- GETTER --
     *  Returns the date of birth.
     * <p>
     * -- SETTER --
     *  Sets the date of birth.
     *


     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Place of birth.
     * -- GETTER --
     *  Returns the place of birth.
     * <p>
     * -- SETTER --
     *  Sets the place of birth.
     *


     */
    @Column(name = "birth_place")
    private String birthPlace;

    /**
     * Gender.
     * Stored as a single character (M, F, etc.).
     * -- GETTER --
     *  Returns the gender.
     * <p>
     * -- SETTER --
     *  Sets the gender.
     *


     */
    @Column(name = "gender")
    private char gender;

    /**
     * Email address.
     * -- GETTER --
     *  Returns the email address.
     * <p>
     * -- SETTER --
     *  Sets the email address.
     *


     */
    @Column(name = "email")
    private String email;

    /**
     * Phone number.
     * -- GETTER --
     *  Returns the phone number.
     * <p>
     * -- SETTER --
     *  Sets the phone number.
     *


     */
    @Column(name = "phone")
    private String phoneNumber;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Calculates the current age based on the date of birth.
     *
     * @return age in years
     */
    public int getAge() {

        if (birthDate == null) {
            return 0;
        }

        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}