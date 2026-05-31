package com.banking.dto;

import com.banking.entity.BankAccount.AccountType;
import com.banking.entity.TransactionHistory.TransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Objects (DTOs)
 *
 * WHY DTOs instead of exposing Entities directly?
 *
 * Security: Entities may contain sensitive fields (hashed passwords,
 *   internal IDs). DTOs let you control exactly what leaves the API.
 *
 * Decoupling: Your API contract stays stable even if the DB schema changes.
 *
 * Validation: Input DTOs carry @Valid constraints. Entities should reflect
 *   DB state, not validate user input.
 *
 * Pattern: Request DTOs (user → API) and Response DTOs (API → user).
 */
public class BankingDTOs {

    // =========================================================
    // AUTH DTOs
    // =========================================================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be 3–50 characters")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;         // JWT
        private String type = "Bearer";
        private Long userId;
        private String username;
        private String email;
    }

    // =========================================================
    // ACCOUNT DTOs
    // =========================================================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateAccountRequest {
        @NotNull(message = "Account type is required")
        private AccountType accountType;

        /**
         * VALIDATION: @Min(0) prevents negative initial balance.
         * BigDecimal allows precise monetary values.
         */
        @NotNull(message = "Initial deposit is required")
        @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative")
        private BigDecimal initialDeposit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountResponse {
        private Long id;
        private String accountNumber;
        private AccountType accountType;
        private BigDecimal balance;
        private LocalDateTime createdAt;
        private String ownerUsername;
    }

    // =========================================================
    // TRANSACTION DTOs
    // =========================================================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepositRequest {
        @NotNull
        @DecimalMin(value = "0.01", message = "Deposit amount must be positive")
        private BigDecimal amount;

        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawalRequest {
        @NotNull
        @DecimalMin(value = "0.01", message = "Withdrawal amount must be positive")
        private BigDecimal amount;

        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequest {
        @NotBlank(message = "Target account number is required")
        private String targetAccountNumber;

        @NotNull
        @DecimalMin(value = "0.01", message = "Transfer amount must be positive")
        private BigDecimal amount;

        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionResponse {
        private Long id;
        private TransactionType type;
        private BigDecimal amount;
        private BigDecimal balanceBefore;
        private BigDecimal balanceAfter;
        private String description;
        private String relatedAccountNumber;
        private LocalDateTime timestamp;
    }

    // =========================================================
    // GENERIC RESPONSE WRAPPERS
    // =========================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> ApiResponse<T> ok(String message, T data) {
            return ApiResponse.<T>builder()
                    .success(true).message(message).data(data).build();
        }

        public static <T> ApiResponse<T> error(String message) {
            return ApiResponse.<T>builder()
                    .success(false).message(message).build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}
