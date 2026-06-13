package com.nvivx.vixhealthsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ── Public pages ─────────────────────────────────────────
                .requestMatchers(
                    "/", "/login", "/logout",
                    "/patient/login", "/patient/authenticate", "/patient/logout",
                    "/patient/spid-callback", "/patient/cie-callback",
                    "/mock-spid/login", "/mock-spid/authenticate",
                    "/mock-cie/login", "/mock-cie/authenticate",
                    "/authenticate", "/select-role",
                    "/departments/**", "/questionnaire/**",
                    "/specialists/**", "/doctors",
                    "/about", "/contact", "/map/**",
                    "/images/**", "/css/**", "/js/**",
                    "/webjars/**", "/error"
                ).permitAll()
                // ── Patient ──────────────────────────────────────────────
                .requestMatchers("/patient/**").hasRole("PATIENT")
                // ── Staff ────────────────────────────────────────────────
                .requestMatchers("/medical-specialist/**").hasRole("MEDICALSPECIALIST")
                .requestMatchers("/secretary/**").hasRole("SECRETARY")
                .requestMatchers("/buyer/**").hasRole("BUYER")
                .requestMatchers("/technician/**").hasRole("TECHNICIAN")
                .requestMatchers("/staff-manager/**").hasRole("STAFFMANAGER")
                .requestMatchers("/payment/**").hasAnyRole("PATIENT", "SECRETARY")
                .requestMatchers("/employee/resources/**").hasAnyRole(
                        "MEDICALSPECIALIST", "SECRETARY", "BUYER", "TECHNICIAN", "STAFFMANAGER")
                // ── Anything else requires authentication ─────────────────
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .exceptionHandling(ex -> ex
                // Redirect unauthenticated requests to the correct login page
                .authenticationEntryPoint((request, response, authException) -> {
                    String path = request.getRequestURI();
                    String base = request.getContextPath();
                    if (path.startsWith(base + "/patient/")) {
                        response.sendRedirect(base + "/patient/login");
                    } else {
                        response.sendRedirect(base + "/login");
                    }
                })
                // Wrong role → back to appropriate login
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    String path = request.getRequestURI();
                    String base = request.getContextPath();
                    if (path.startsWith(base + "/patient/")) {
                        response.sendRedirect(base + "/patient/login");
                    } else {
                        response.sendRedirect(base + "/login");
                    }
                })
            );

        return http.build();
    }
}
