package com.banking.service;

import com.banking.dto.BankingDTOs.*;
import com.banking.entity.User;
import com.banking.exception.GlobalExceptionHandler;
import com.banking.repository.UserRepository;
import com.banking.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService — handles registration and login.
 *
 * @Service: marks this as a Spring-managed service Bean.
 *   Spring creates one instance and injects it wherever needed (IoC/DI).
 *
 * @Transactional: wraps the method in a DB transaction.
 *   If any exception is thrown, the entire operation rolls back.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicates before creating
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Build and save the user with a BCrypt-hashed password
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // HASH, never plaintext
                .build();

        userRepository.save(user);
        log.info("Registered new user: {}", user.getUsername());

        // Auto-login after registration
        String token = jwtUtils.generateToken(request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager validates username/password against DB
        // Throws BadCredentialsException if invalid — caught by GlobalExceptionHandler
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateToken(authentication);
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        log.info("User logged in: {}", request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
