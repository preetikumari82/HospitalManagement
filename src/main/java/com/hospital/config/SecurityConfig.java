package com.hospital.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // FR1.2: makes @PreAuthorize actually enforce RBAC
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    // Comma separated list, e.g. "http://localhost:5173,https://your-frontend.onrender.com"
    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                	    .requestMatchers(
                	            "/api/auth/login",
                	            "/api/auth/forgot-password",
                	            "/api/auth/reset-password",
                	            "/v3/api-docs/**",
                	            "/swagger-ui/**",
                	            "/swagger-ui.html",
                	            "/swagger-ui/index.html"
                	    ).permitAll()

                	    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                	    .requestMatchers("/api/patients/register").permitAll()
                	    .requestMatchers("/api/patients/me").hasRole("PATIENT")
                	    .requestMatchers("/api/patients/**").hasAnyRole("ADMIN","DOCTOR","NURSE","RECEPTIONIST","PHARMACIST","LAB_TECH")
                	    .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
                	    .requestMatchers("/api/nurse/**").hasRole("NURSE")
                	    .requestMatchers("/api/medical/**")
                	    .hasAnyRole("ADMIN", "MEDICAL", "PHARMACIST")

                	    .anyRequest().authenticated()
                	)
				/*
				 * .authorizeHttpRequests(auth -> auth
				 * .requestMatchers("/api/auth/login").permitAll() // Admin APIs
				 * .requestMatchers("/api/admin/**").hasRole("ADMIN")
				 * 
				 * // .anyRequest().authenticated() )
				 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }
}
