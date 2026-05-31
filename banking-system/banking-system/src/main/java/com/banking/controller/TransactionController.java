package com.banking.controller;

import com.banking.dto.BankingDTOs.*;
import com.banking.service.BankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * TransactionController — deposit, withdraw, transfer, and history.
 *
 * REST Design:
 *   POST /api/accounts/{id}/deposit      → add funds
 *   POST /api/accounts/{id}/withdraw     → remove funds
 *   POST /api/accounts/{id}/transfer     → move funds to another account
 *   GET  /api/accounts/{id}/transactions → paginated history
 *
 * All operations return the resulting TransactionHistory record,
 * giving clients immediate confirmation with before/after balances.
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class TransactionController {

    private final BankingService bankingService;

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody DepositRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        TransactionResponse txn = bankingService.deposit(
                accountId, userDetails.getUsername(), request);

        return ResponseEntity.ok(ApiResponse.ok("Deposit successful", txn));
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody WithdrawalRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        TransactionResponse txn = bankingService.withdraw(
                accountId, userDetails.getUsername(), request);

        return ResponseEntity.ok(ApiResponse.ok("Withdrawal successful", txn));
    }

    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @PathVariable Long accountId,
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        TransactionResponse txn = bankingService.transfer(
                accountId, userDetails.getUsername(), request);

        return ResponseEntity.ok(ApiResponse.ok("Transfer successful", txn));
    }

    /**
     * GET /api/accounts/{id}/transactions?page=0&size=20
     *
     * Pagination prevents returning thousands of rows at once.
     * Page 0 = first page. Default size = 20 transactions per page.
     */
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> getTransactions(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        PagedResponse<TransactionResponse> history = bankingService.getTransactionHistory(
                accountId, userDetails.getUsername(), page, size);

        return ResponseEntity.ok(ApiResponse.ok("Transaction history retrieved", history));
    }
}
