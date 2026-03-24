package com.pcplus.config;

import com.pcplus.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration:
 *   - Stateless JWT sessions (no server-side session state)
 *   - CORS allows the frontend origin (localhost:3000) AND file:// (null origin)
 *   - Public endpoints: GET /api/games, POST /api/auth/**
 *   - Protected: everything else requires a valid JWT
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${pcplus.cors.allowed-origin}")
    private String allowedOrigin;

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsSource()))
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public – auth endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // Public – read games catalog
                .requestMatchers(HttpMethod.GET, "/api/games/**").permitAll()
                // Publisher-only endpoints
                .requestMatchers("/api/publisher/**")
                    .hasRole("PUBLISHER")
                // All other /api/** require any authenticated user
                .requestMatchers("/api/**").authenticated()
                // Static frontend files (if served by Spring, else ignore)
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Allow the configured origin (localhost:3000 by default) plus common dev origins.
        // "null" covers pages opened directly from the filesystem (file:// origin).
        cfg.setAllowedOrigins(Arrays.asList(
            allowedOrigin,
            "http://localhost:3000",
            "http://localhost:8080",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5500",   // VS Code Live Server
            "http://localhost:5500",    // VS Code Live Server
            "null"                      // file:// pages send Origin: null
        ));

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/api/**", cfg);
        return src;
    }
}
