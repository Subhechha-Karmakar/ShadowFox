package com.banking.controller;

import com.banking.dto.BankingDTOs.*;
import com.banking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — handles user registration and login.
 *
 * @RestController = @Controller + @ResponseBody
 *   Automatically serializes return values to JSON.
 *
 * REST Status Code Guide:
 *   201 Created  → resource successfully created (POST that creates)
 *   200 OK       → successful read or action without creation
 *   400 Bad Request → validation failed
 *   401 Unauthorized → bad credentials
 *   409 Conflict → duplicate resource
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     *
     * @Valid triggers bean validation on RegisterRequest fields.
     * Returns 201 Created with JWT token — ready to call protected endpoints.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)     // 201 — resource was created
                .body(ApiResponse.ok("User registered successfully", response));
    }

    /**
     * POST /api/auth/login
     *
     * Returns 200 OK with JWT on success.
     * Returns 401 Unauthorized if credentials are wrong.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }
}
