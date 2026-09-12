package com.src.commentservice.config;

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

    private final JwtFilter jwtFilter;

    public SecurityConfig(
            JwtFilter jwtFilter) {

        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain
    securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                .csrf(csrf ->
                        csrf.disable()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // -------------------------------------
                        // ADMIN
                        // -------------------------------------

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/comments/*/block"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/comments/*/unblock"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/comments/**"
                        ).hasRole("ADMIN")

                        // -------------------------------------
                        // AUTHENTICATED USERS
                        // -------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/comments"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/comments/**"
                        ).authenticated()

                        // -------------------------------------
                        // PUBLIC READ
                        // -------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/comments/article/**"
                        ).permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}