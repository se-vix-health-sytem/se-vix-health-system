package com.nvivx.vixhealthsystem.model.enums;

/**
 * @brief Spring Security authority roles assigned to every authenticated user in the system.
 *
 * The {@code ROLE_} prefix is required by Spring Security's default role-naming
 * convention and maps directly to the {@code hasRole()} expressions used in
 * {@link com.nvivx.vixhealthsystem.config.SecurityConfig}.
 */
public enum Role {
    /** Authenticated patient accessing their own appointments and medical record. */
    ROLE_PATIENT,
    /** Doctor or specialist with access to patient records, prescriptions, and surgeries. */
    ROLE_MEDICAL_SPECIALIST,
    /** Administrative secretary managing appointments and patient admissions. */
    ROLE_SECRETARY,
    /** Technician responsible for maintaining and monitoring medical equipment. */
    ROLE_TECHNICIAN,
    /** HR/administrative manager who creates and manages employee accounts. */
    ROLE_STAFF_MANAGER,
    /** Procurement buyer who manages inventory and resource purchasing. */
    ROLE_BUYER,
}
