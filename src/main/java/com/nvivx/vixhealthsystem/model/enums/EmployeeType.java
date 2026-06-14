package com.nvivx.vixhealthsystem.model.enums;

/**
 * @brief Categorises the concrete sub-type of an {@link com.nvivx.vixhealthsystem.model.person.employee.Employee}.
 *
 * Mirrors the JPA discriminator values stored in the {@code Employees} table and is
 * used in service code where a type check is needed without resorting to {@code instanceof}.
 */
public enum EmployeeType {
    /** A doctor, surgeon, or other licensed medical professional. */
    MEDICAL_SPECIALIST,
    /** An administrative secretary handling appointments and admissions. */
    SECRETARY,
    /** A technician responsible for equipment maintenance and monitoring. */
    TECHNICIAN,
    /** A procurement buyer managing supplies and inventory. */
    BUYER,
    /** An HR/administrative manager overseeing employee account management. */
    STAFF_MANAGER
}
