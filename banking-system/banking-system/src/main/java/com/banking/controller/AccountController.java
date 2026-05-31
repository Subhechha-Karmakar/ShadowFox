package com.banking.controller;

import com.banking.dto.BankingDTOs.*;
import com.banking.service.BankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AccountController — CRUD operations for bank accounts.
 *
 * Security Pattern:
 *   @AuthenticationPrincipal UserDetails userDetails
 *   → Spring injects the authenticated user from the SecurityContext.
 *   This means we NEVER trust user-supplied usernames — we always use
 *   the identity from the validated JWT.
 *
 * REST Resource Design:
 *   GET    /api/accounts         → list all accounts for authenticated user
 *   POST   /api/accounts         → create a new account
 *   GET    /api/accounts/{id}    → get a specific account (ownership checked)
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final BankingService bankingService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        AccountResponse account = bankingService.createAccount(
                userDetails.getUsername(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Account created successfully", account));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<AccountResponse> accounts = bankingService.getUserAccounts(
                userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.ok("Accounts retrieved", accounts));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @PathVariable Long accountId,
            @AuthenticationPrincipal UserDetails userDetails) {

        AccountResponse account = bankingService.getAccount(
                accountId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok("Account retrieved", account));
    }
}
