package com.banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TransactionHistory Entity — TIER 1: Transaction Audit Trail
 *
 * Every balance mutation (deposit, withdrawal, transfer) writes
 * an immutable record here with:
 *   - What type of transaction it was
 *   - The amount involved
 *   - Balance before and after (for audit completeness)
 *   - Related account (counterparty for transfers)
 *   - Timestamp
 *
 * Immutability: @Column(updatable = false) on all fields ensures
 * audit records cannot be modified after creation.
 */
@Entity
@Table(name = "transaction_history",
       indexes = {
           @Index(name = "idx_txn_account", columnList = "account_id"),
           @Index(name = "idx_txn_timestamp", columnList = "timestamp")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal balanceBefore;

    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    @Column(length = 255, updatable = false)
    private String description;

    /**
     * For transfers: stores the other account number involved.
     * NULL for deposits and withdrawals.
     */
    @Column(length = 36, updatable = false)
    private String relatedAccountNumber;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    /**
     * The account this transaction belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private BankAccount account;

    @PrePersist
    protected void onPersist() {
        this.timestamp = LocalDateTime.now();
    }

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_OUT,   // Debit side of a transfer
        TRANSFER_IN     // Credit side of a transfer
    }
}
