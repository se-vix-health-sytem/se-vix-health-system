package com.nvivx.vixhealthsystem.config;

// src/main/java/com/yourpackage/config/SecurityConfig.java

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
                        // Public pages — no login needed
                        .requestMatchers("/", "/departments/**", "/doctors/**",
                                "/hospitals", "/questionnaire",
                                "/about", "/contact",
                                "/css/**", "/js/**", "/images/**",
                                "/webjars/**").permitAll()

                        // Patient login
                        .requestMatchers("/login").permitAll()

                        // Staff login
                        .requestMatchers("/staff/login").permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}
