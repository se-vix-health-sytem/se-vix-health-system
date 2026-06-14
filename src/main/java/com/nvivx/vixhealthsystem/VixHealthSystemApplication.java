package com.nvivx.vixhealthsystem;

import com.nvivx.vixhealthsystem.config.FirebaseConfig;
import com.nvivx.vixhealthsystem.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
