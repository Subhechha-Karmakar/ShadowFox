package com.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User Entity — mapped to the USERS table via JPA/Hibernate.
 *
 * JPA Annotation Guide:
 *   @Entity        → tells Hibernate "this class is a database table"
 *   @Table         → customises the table name
 *   @Id            → marks the primary key field
 *   @GeneratedValue → auto-increment the PK (IDENTITY = DB-managed)
 *   @Column        → maps field to a specific column with constraints
 *   @OneToMany     → one User can have many BankAccounts
 */
@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email"),
           @UniqueConstraint(columnNames = "username")
       })
@Data                 // Lombok: generates getters, setters, equals, hashCode, toString
@Builder              // Lombok: enables builder pattern  User.builder().name("x").build()
@NoArgsConstructor    // Lombok: generates no-args constructor (required by JPA)
@AllArgsConstructor   // Lombok: generates all-args constructor (used by @Builder)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false, length = 100)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password; // BCrypt-hashed, never plaintext

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Bidirectional one-to-many: one user → many accounts.
     * mappedBy = "owner" means the BankAccount.owner field owns the FK.
     * CascadeType.ALL: operations on User cascade to their accounts.
     * orphanRemoval: deleting a User deletes their orphaned accounts.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankAccount> accounts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
