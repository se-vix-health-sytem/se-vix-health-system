package com.nvivx.vixhealthsystem.model.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
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
 * @see Patient
 * @see com.nvivx.vixhealthsystem.model.person.employee.Employee
 */
@MappedSuperclass
public abstract class Person {

    /** Person's first name. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Person's last name. */
    @Column(name = "surname", nullable = false)
    private String surname;

    /** Date of birth. */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /** Place of birth. */
    @Column(name = "birth_place")
    private String birthPlace;

    /** Gender (M, F, etc.). */
    @Column(name = "gender")
    private char gender;

    /** Email address. */
    @Column(name = "email")
    private String email;

    /** Phone number. */
    @Column(name = "phone")
    private String phoneNumber;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Person() {
    }

    /**
     * Creates a Person with the specified personal and contact information.
     *
     * @param name        the first name
     * @param surname     the last name
     * @param birthDate   the date of birth
     * @param birthPlace  the place of birth
     * @param gender      the gender character (e.g. 'M' or 'F')
     * @param email       the email address
     * @param phoneNumber the phone number
     */
    public Person(
            String name,
            String surname,
            LocalDate birthDate,
            String birthPlace,
            char gender,
            String email,
            String phoneNumber
    ) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the first name.
     *
     * @return the first name
     */
    public String getName() {
        return name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the first name.
     *
     * @param name the first name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the surname (last name).
     *
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the surname (last name).
     *
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the date of birth.
     *
     * @return the birth date
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the date of birth.
     *
     * @param birthDate the birth date to set
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the place of birth.
     *
     * @return the birth place
     */
    public String getBirthPlace() {
        return birthPlace;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the place of birth.
     *
     * @param birthPlace the birth place to set
     */
    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the gender.
     *
     * @return the gender character
     */
    public char getGender() {
        return gender;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the gender.
     *
     * @param gender the gender character to set
     */
    public void setGender(char gender) {
        this.gender = gender;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the phone number.
     *
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /// @endcond

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Calculates the current age based on the date of birth.
     *
     * @return age in years, or 0 if birth date is not set
     */
    @JsonIgnore
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}