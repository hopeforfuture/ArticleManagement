package com.src.articleservice.config;

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

                        /*
                         * Anyone authenticated can create.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/articles"
                        )
                        .authenticated()


                        /*
                         * Anyone authenticated can edit/delete
                         * through the endpoint.
                         *
                         * Actual ownership is checked inside
                         * ArticleService.
                         */
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/articles/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/articles/**"
                        )
                        .authenticated()


                        /*
                         * Publish own article.
                         * Ownership is checked in service.
                         */
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/articles/*/publish"
                        )
                        .hasRole("ADMIN")


                        /*
                         * ONLY ADMIN can unpublish.
                         */
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/articles/*/unpublish"
                        )
                        .hasRole("ADMIN")


                        /*
                         * Read operations.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/articles/**"
                        )
                        .permitAll()


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