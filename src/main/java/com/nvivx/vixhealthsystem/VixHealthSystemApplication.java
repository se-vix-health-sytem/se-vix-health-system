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
 *
 * \dot
 * digraph ProjectStructure {
 *   rankdir=LR;
 *   bgcolor=transparent;
 *   node [shape=box, style="filled,rounded", fontname="Helvetica", fontsize=11, margin="0.3,0.15"];
 *   edge [color="#888888", arrowhead=open, arrowsize=0.8];
 *
 *   root [label="VIX Health System", fillcolor="#1e3a5f", fontcolor=white, penwidth=2];
 *   ctrl [label="controllers\n(MVC — one per role)",      fillcolor="#1e5f3a", fontcolor=white];
 *   model[label="model\n(JPA entities)",                  fillcolor="#1e3a5f", fontcolor="#a8d4ff"];
 *   svc  [label="service\n(business logic)",              fillcolor="#3a1e5f", fontcolor=white];
 *   repo [label="repository\n(data access)",              fillcolor="#5f3a1e", fontcolor=white];
 *   cfg  [label="config\n(security & Firebase)",          fillcolor="#5f1e3a", fontcolor=white];
 *   dto  [label="dto\n(request/response objects)",        fillcolor="#3a5f1e", fontcolor=white];
 *
 *   root -> ctrl;
 *   root -> model;
 *   root -> svc;
 *   root -> repo;
 *   root -> cfg;
 *   root -> dto;
 * }
 * \enddot
 *
 * @section technologies Technologies Used
 * - **Backend:** Spring Boot (Java 17)
 * - **Database:** H2 (development), PostgreSQL (production-ready)
 * - **Security:** Spring Security, Firebase Admin SDK
 * - **Frontend:** Thymeleaf, Bootstrap 5, Leaflet.js
 *
 * @section authors Authors
 * - Viviana Fraccaroli
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
