package com.src.categoryservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Disable CSRF because this is a REST API
                .csrf(csrf -> csrf.disable())

                // Stateless authentication because we use JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // ==============================
                        // CATEGORY - ADMIN ONLY
                        // ==============================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/categories"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/categories/**"
                        ).hasRole("ADMIN")


                        // ==============================
                        // CATEGORY - AUTHENTICATED USERS
                        // ==============================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/categories/**"
                        ).authenticated()


                        // ==============================
                        // ALL OTHER REQUESTS
                        // ==============================

                        .anyRequest().authenticated()
                )

                // JWT filter
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}