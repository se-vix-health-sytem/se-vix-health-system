package com.nvivx.vixhealthsystem;

import com.nvivx.vixhealthsystem.config.FirebaseConfig;
import com.nvivx.vixhealthsystem.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @mainpage VIX Health System - Software Engineering Project
 *
 * @section intro Introduction
 * This document contains the automatically generated technical documentation for the
 * **VIX Health System**, a hospital management platform developed as a university
 * software engineering project.
 *
 * The system covers patient registration and authentication, appointment booking,
 * staff management, medical records, inventory tracking, and basic payment simulation.
 *
 * @section structure Project Structure
 * - @ref com.nvivx.vixhealthsystem.controllers "Controllers" - MVC controllers for all user roles.
 * - @ref com.nvivx.vixhealthsystem.model "Model" - JPA entities and the core domain logic.
 * - @ref com.nvivx.vixhealthsystem.service "Service" - Business logic, grouped by domain area.
 * - @ref com.nvivx.vixhealthsystem.repository "Repository" - Data access layer (JPA & JSON).
 * - @ref com.nvivx.vixhealthsystem.config "Config" - Spring Security and Firebase setup.
 *
 * @section technologies Technologies Used
 * - **Backend:** Spring Boot (Java 17)
 * - **Database:** H2 (development), PostgreSQL (production-ready)
 * - **Security:** Spring Security, Firebase Admin SDK
 * - **Frontend:** Thymeleaf, Bootstrap 5, Leaflet.js
 *
 * @section authors Authors
 * - Viviana Fraccarolli
 * - Lorena Valentina Buitrón Zambrano
 * - Navjot Kaur
 * - Alexandrina Harti
 */


/**
 * @brief Starts the VIX Health System Spring Boot application.
 *
 * Triggers component scanning from com.nvivx.vixhealthsystem and launches
 * the embedded Tomcat server on the port defined in application.properties
 * (default 8080).
 *
 * @see SecurityConfig
 * @see FirebaseConfig
 */
@SpringBootApplication
public class VixHealthSystemApplication {

    /**
     * Application entry point: delegates directly to {@link SpringApplication}.
     *
     * @param args command-line arguments passed through to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(VixHealthSystemApplication.class, args);
    }

}
