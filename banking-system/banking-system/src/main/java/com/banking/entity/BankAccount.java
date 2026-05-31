package com.banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BankAccount Entity — mapped to BANK_ACCOUNTS table.
 *
 * Design Decisions:
 *   - BigDecimal for money: NEVER use float/double for currency (precision loss).
 *   - accountNumber: auto-generated UUID — globally unique, not guessable.
 *   - AccountType enum: stored as STRING so DB values are human-readable.
 *   - @ManyToOne: many accounts can belong to one user.
 */
@Entity
@Table(name = "bank_accounts",
       uniqueConstraints = @UniqueConstraint(columnNames = "accountNumber"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String accountNumber; // UUID format: "550e8400-e29b-41d4-a716-446655440000"

    @Enumerated(EnumType.STRING) // Store "SAVINGS" / "CHECKING" not 0/1
    @Column(nullable = false, length = 20)
    private AccountType accountType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Many-to-One: many accounts → one user.
     * @JoinColumn defines the FK column name in BANK_ACCOUNTS table.
     * FetchType.LAZY: don't load the User object until explicitly accessed (performance).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<TransactionHistory> transactions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.accountNumber == null) {
            this.accountNumber = UUID.randomUUID().toString();
        }
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
    }

    public enum AccountType {
        SAVINGS, CHECKING
    }
}
