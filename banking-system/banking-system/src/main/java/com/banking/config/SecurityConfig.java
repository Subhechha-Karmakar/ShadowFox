package com.banking.config;

import com.banking.repository.UserRepository;
import com.banking.security.JwtAuthFilter;
import com.banking.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig — TIER 2: Spring Security + JWT Configuration
 *
 * Key concepts:
 *
 * SecurityFilterChain: defines which endpoints are public vs. protected.
 *   Public:  /api/auth/** (login, register)
 *   Private: everything else requires a valid JWT
 *
 * SessionCreationPolicy.STATELESS: disables HTTP sessions entirely.
 *   JWT is self-contained — no server-side session needed.
 *
 * BCryptPasswordEncoder: one-way hashing with salt.
 *   Never store plaintext passwords. BCrypt auto-salts and is slow by design.
 *
 * DaoAuthenticationProvider: wires UserDetailsService + PasswordEncoder
 *   so Spring knows how to look up users and verify passwords.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize on controller methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    /**
     * UserDetailsService: Spring Security's contract for loading users.
     * Our implementation loads from the database.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.banking.entity.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User not found: " + username));

            // Spring Security's built-in User implements UserDetails
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt strength 12: ~250ms per hash — slow enough to thwart brute force
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtils, userDetailsService());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — not needed for stateless JWT APIs
            .csrf(AbstractHttpConfigurer::disable)

            // No sessions — every request must carry a JWT
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Route-level security rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/*.css", "/*.js", "/assets/**").permitAll() // Web Frontend
                .requestMatchers("/api/auth/**").permitAll()     // Login/Register: public
                .requestMatchers("/h2-console/**").permitAll()   // H2 console: dev only
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()                    // Everything else: needs JWT
            )

            // Allow H2 console frames (dev only)
            .headers(headers -> headers.frameOptions(f -> f.disable()))

            .authenticationProvider(authenticationProvider())

            // Our JWT filter runs before the built-in username/password filter
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
