/**
 * Root package of the VIX Health System.
 *
 * VIX is a hospital management platform developed as a university software
 * engineering project. It covers patient registration and authentication,
 * appointment booking, staff management, medical records, inventory tracking,
 * and basic payment simulation.
 *
 * The codebase is split into the following sub-packages:
 * <ul>
 *   <li>{@code config}      — Spring Security and Firebase bootstrap</li>
 *   <li>{@code controllers} — MVC controllers, one sub-package per user role</li>
 *   <li>{@code database}    — low-level JDBC utilities (used outside JPA)</li>
 *   <li>{@code dto}         — lightweight objects that cross controller/service boundaries</li>
 *   <li>{@code exception}   — custom exceptions and the global error handler</li>
 *   <li>{@code model}       — JPA entities and enums; the core domain</li>
 *   <li>{@code repository}  — Spring Data JPA interfaces and JSON-backed stores</li>
 *   <li>{@code service}     — all business logic, grouped by domain area</li>
 * </ul>
 *
 * Entry point: {@link com.nvivx.vixhealthsystem.VixHealthSystemApplication}
 */
package com.nvivx.vixhealthsystem;
