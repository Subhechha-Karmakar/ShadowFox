package com.banking.service;

import com.banking.dto.BankingDTOs.*;
import com.banking.entity.BankAccount;
import com.banking.entity.TransactionHistory;
import com.banking.entity.TransactionHistory.TransactionType;
import com.banking.entity.User;
import com.banking.repository.BankAccountRepository;
import com.banking.repository.TransactionHistoryRepository;
import com.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BankingService — core business logic layer.
 *
 * Layered Architecture:
 *   Controller → Service → Repository → Database
 *
 *   Controllers:  handle HTTP, parse requests, return responses
 *   Services:     contain business rules, validation, transactions
 *   Repositories: database access only — no business logic
 *
 * @Transactional on methods: guarantees atomicity.
 *   Transfer example: debit source + credit target must BOTH succeed,
 *   or NEITHER is committed (prevents money creation/destruction bugs).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BankingService {

    private final UserRepository userRepository;
    private final BankAccountRepository accountRepository;
    private final TransactionHistoryRepository transactionRepo;

    // =========================================================
    // ACCOUNT OPERATIONS
    // =========================================================

    @Transactional
    public AccountResponse createAccount(String username, CreateAccountRequest request) {
        User owner = findUserByUsername(username);

        BankAccount account = BankAccount.builder()
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit())
                .owner(owner)
                .build();

        account = accountRepository.save(account);

        // Audit: record the initial deposit if > 0
        if (request.getInitialDeposit().compareTo(BigDecimal.ZERO) > 0) {
            saveTransaction(account, TransactionType.DEPOSIT,
                    request.getInitialDeposit(),
                    BigDecimal.ZERO,
                    account.getBalance(),
                    "Initial deposit", null);
        }

        log.info("Created {} account {} for user {}",
                account.getAccountType(), account.getAccountNumber(), username);

        return toAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getUserAccounts(String username) {
        User user = findUserByUsername(username);
        return accountRepository.findByOwnerId(user.getId())
                .stream()
                .map(this::toAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long accountId, String username) {
        User user = findUserByUsername(username);
        BankAccount account = accountRepository
                .findByIdAndOwnerId(accountId, user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Account not found or access denied"));
        return toAccountResponse(account);
    }

    // =========================================================
    // TRANSACTION OPERATIONS
    // =========================================================

    @Transactional
    public TransactionResponse deposit(Long accountId, String username, DepositRequest request) {
        BankAccount account = getOwnedAccount(accountId, username);

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(request.getAmount());

        account.setBalance(balanceAfter);
        accountRepository.save(account);

        TransactionHistory txn = saveTransaction(account, TransactionType.DEPOSIT,
                request.getAmount(), balanceBefore, balanceAfter,
                request.getDescription(), null);

        log.info("Deposit: {} to account {} | Balance: {} → {}",
                request.getAmount(), account.getAccountNumber(), balanceBefore, balanceAfter);

        return toTransactionResponse(txn);
    }

    @Transactional
    public TransactionResponse withdraw(Long accountId, String username, WithdrawalRequest request) {
        BankAccount account = getOwnedAccount(accountId, username);

        // Business rule: cannot overdraw
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException(
                    "Insufficient funds. Available: " + account.getBalance() +
                    ", Requested: " + request.getAmount());
        }

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmount());

        account.setBalance(balanceAfter);
        accountRepository.save(account);

        TransactionHistory txn = saveTransaction(account, TransactionType.WITHDRAWAL,
                request.getAmount(), balanceBefore, balanceAfter,
                request.getDescription(), null);

        return toTransactionResponse(txn);
    }

    /**
     * Fund transfer — the most complex operation.
     *
     * @Transactional ensures BOTH account updates happen atomically.
     * If the credit fails after the debit, the whole transaction rolls back.
     * This is the core guarantee that prevents money from "disappearing".
     */
    @Transactional
    public TransactionResponse transfer(Long sourceAccountId, String username, TransferRequest request) {
        BankAccount source = getOwnedAccount(sourceAccountId, username);
        BankAccount target = accountRepository
                .findByAccountNumber(request.getTargetAccountNumber())
                .orElseThrow(() -> new RuntimeException(
                        "Target account not found: " + request.getTargetAccountNumber()));

        if (source.getAccountNumber().equals(target.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException(
                    "Insufficient funds. Available: " + source.getBalance());
        }

        // Debit source
        BigDecimal sourceBefore = source.getBalance();
        BigDecimal sourceAfter = sourceBefore.subtract(request.getAmount());
        source.setBalance(sourceAfter);
        accountRepository.save(source);

        // Credit target
        BigDecimal targetBefore = target.getBalance();
        BigDecimal targetAfter = targetBefore.add(request.getAmount());
        target.setBalance(targetAfter);
        accountRepository.save(target);

        // Audit both sides
        TransactionHistory debitTxn = saveTransaction(source, TransactionType.TRANSFER_OUT,
                request.getAmount(), sourceBefore, sourceAfter,
                request.getDescription(), target.getAccountNumber());

        saveTransaction(target, TransactionType.TRANSFER_IN,
                request.getAmount(), targetBefore, targetAfter,
                request.getDescription(), source.getAccountNumber());

        log.info("Transfer: {} from {} to {}",
                request.getAmount(), source.getAccountNumber(), target.getAccountNumber());

        return toTransactionResponse(debitTxn);
    }

    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getTransactionHistory(
            Long accountId, String username, int page, int size) {

        getOwnedAccount(accountId, username); // Verify ownership

        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionHistory> txnPage = transactionRepo
                .findByAccountIdOrderByTimestampDesc(accountId, pageable);

        List<TransactionResponse> content = txnPage.getContent()
                .stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());

        return PagedResponse.<TransactionResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(txnPage.getTotalElements())
                .totalPages(txnPage.getTotalPages())
                .build();
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private TransactionHistory saveTransaction(
            BankAccount account, TransactionType type,
            BigDecimal amount, BigDecimal before, BigDecimal after,
            String description, String relatedAccountNumber) {

        TransactionHistory txn = TransactionHistory.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceBefore(before)
                .balanceAfter(after)
                .description(description)
                .relatedAccountNumber(relatedAccountNumber)
                .build();

        return transactionRepo.save(txn);
    }

    private BankAccount getOwnedAccount(Long accountId, String username) {
        User user = findUserByUsername(username);
        return accountRepository
                .findByIdAndOwnerId(accountId, user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Account not found or access denied"));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private AccountResponse toAccountResponse(BankAccount account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .ownerUsername(account.getOwner().getUsername())
                .build();
    }

    private TransactionResponse toTransactionResponse(TransactionHistory txn) {
        return TransactionResponse.builder()
                .id(txn.getId())
                .type(txn.getType())
                .amount(txn.getAmount())
                .balanceBefore(txn.getBalanceBefore())
                .balanceAfter(txn.getBalanceAfter())
                .description(txn.getDescription())
                .relatedAccountNumber(txn.getRelatedAccountNumber())
                .timestamp(txn.getTimestamp())
                .build();
    }
}
