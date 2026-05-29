package com.bezkoder.spring.jpa.h2.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Enterprise-grade Spring Security configuration.
 *
 * <p>Authentication strategy: HTTP Basic Auth backed by {@link UserDetailsServiceImpl}
 * which loads credentials from PostgreSQL via BCrypt password matching.</p>
 *
 * <p>CORS: Explicit allow-list for the Angular dev client (localhost:4200).
 * withCredentials=true requires a specific origin — wildcard is intentionally
 * NOT used here to avoid browser rejection of credentialed cross-origin requests.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // ── Password Encoder ────────────────────────────────────────────────────

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── DAO Authentication Provider ─────────────────────────────────────────
    // Wires the UserDetailsService + PasswordEncoder into Spring Security so
    // HTTP Basic Auth validates against the DB, not an in-memory store.

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── Authentication Manager ──────────────────────────────────────────────

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // ── CORS Configuration ──────────────────────────────────────────────────
    // Centralised here — controllers must NOT use @CrossOrigin to avoid
    // conflicts when allowCredentials=true is set.

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Allow Angular dev server; add production URL as needed via env var
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:4201"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));
        config.setExposedHeaders(List.of("Authorization"));
        // Required for Basic Auth credentials to be forwarded by the browser
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    // ── Security Filter Chain ────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Use the centralised CORS bean above
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Disable CSRF — stateless REST API using Basic Auth per request
            .csrf(csrf -> csrf.disable())

            // Stateless — no HTTP session is created or used
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Wire the DAO provider
            .authenticationProvider(authenticationProvider())

            // Route-level authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints — login + register never require a token
                .requestMatchers("/api/auth/**").permitAll()
                // All other API endpoints require a valid authenticated user
                .anyRequest().authenticated()
            )

            // Enable HTTP Basic Auth — credentials are validated against the DB
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
