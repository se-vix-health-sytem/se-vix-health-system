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
                                        // Allow ALL requests without authentication FOR now
                                        .anyRequest().permitAll()
                                )
                                // Disable CSRF for development
                                .csrf(csrf -> csrf.disable())

                                // Disable form login
                                .formLogin(form -> form.disable())

                                // Disable HTTP Basic
                                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
