package com.nvivx.vixhealthsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @brief Configures Spring Security for role-based URL access control.
 *
 * Authentication is handled entirely by Firebase (see {@link FirebaseConfig}).
 * Spring Security's job here is authorisation only — it checks the role stored in the
 * HTTP session and either lets the request through or redirects to the appropriate
 * login page.
 *
 * CSRF, form-login, and HTTP-Basic are all disabled because the app uses Firebase ID
 * tokens and session attributes for auth state rather than Spring's built-in mechanisms.
 *
 * @see FirebaseConfig
 * @see com.nvivx.vixhealthsystem.service.integration.FirebaseAuthService
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // =========================================================
    // FILTER CHAIN
    // =========================================================

    /**
     * Declares the security filter chain that governs every HTTP request.
     *
     * Access rules summary:
     * - Public pages (site, assets, login endpoints) — {@code permitAll()}
     * - {@code /patient/**}             — {@code ROLE_PATIENT} only
     * - {@code /medical-specialist/**}  — {@code ROLE_MEDICALSPECIALIST} only
     * - {@code /secretary/**}           — {@code ROLE_SECRETARY} only
     * - {@code /buyer/**}               — {@code ROLE_BUYER} only
     * - {@code /technician/**}          — {@code ROLE_TECHNICIAN} only
     * - {@code /staff-manager/**}       — {@code ROLE_STAFFMANAGER} only
     * - {@code /payment/**}             — patient or secretary
     * - {@code /employee/resources/**}  — any staff role
     *
     * On auth failure: patient paths redirect to {@code /patient/login}, all others to {@code /login}.
     *
     * @param http  Spring Security's builder for the filter chain.
     * @return      The configured {@link SecurityFilterChain}.
     * @throws Exception When any security builder call fails.
     */
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
                    "/privacy", "/gdpr", "/terms", "/accessibility",
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
