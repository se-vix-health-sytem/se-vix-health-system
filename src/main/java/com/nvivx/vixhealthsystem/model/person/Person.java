package com.nvivx.vixhealthsystem.model.person;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDate;
import java.time.Period;

/**
 * Base class for all people in the system.
 * Contains personal and contact information shared by both patients and employees.
 *
 * This class is marked as a MappedSuperclass, meaning its fields are inherited
 * by subclasses and mapped directly into their tables, but Person itself
 * does not have a dedicated database table.
 *
 * @see com.nvivx.vixhealthsystem.model.person.patient.Patient
 * @see com.nvivx.vixhealthsystem.model.person.employee.Employee
 */
@MappedSuperclass
public abstract class Person {

    /**
     * Person's first name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Person's last name.
     */
    @Column(name = "surname", nullable = false)
    private String surname;

    /**
     * Date of birth.
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Place of birth.
     */
    @Column(name = "birth_place")
    private String birthPlace;

    /**
     * Gender.
     * Stored as a single character (M, F, etc.).
     */
    @Column(name = "gender")
    private char gender;

    /**
     * Email address.
     */
    @Column(name = "email")
    private String email;

    /**
     * Phone number.
     */
    @Column(name = "phone")
    private String phoneNumber;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the first name.
     *
     * @return person's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the first name.
     *
     * @param name first name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the surname.
     *
     * @return person's surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname.
     *
     * @param surname last name
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Returns the date of birth.
     *
     * @return birth date
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the date of birth.
     *
     * @param birthDate date of birth
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Returns the place of birth.
     *
     * @return birth place
     */
    public String getBirthPlace() {
        return birthPlace;
    }

    /**
     * Sets the place of birth.
     *
     * @param birthPlace place of birth
     */
    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    /**
     * Returns the gender.
     *
     * @return gender character
     */
    public char getGender() {
        return gender;
    }

    /**
     * Sets the gender.
     *
     * @param gender gender character
     */
    public void setGender(char gender) {
        this.gender = gender;
    }

    /**
     * Returns the email address.
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the phone number.
     *
     * @return phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     *
     * @param phoneNumber phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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