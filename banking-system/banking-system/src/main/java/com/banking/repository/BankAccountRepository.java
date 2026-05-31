package com.banking.repository;

import com.banking.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    // Spring Data parses method name → WHERE owner_id = ?
    List<BankAccount> findByOwnerId(Long ownerId);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    // JPQL query — uses entity class names, not table names
    @Query("SELECT a FROM BankAccount a WHERE a.owner.id = :ownerId AND a.id = :accountId")
    Optional<BankAccount> findByIdAndOwnerId(
            @Param("accountId") Long accountId,
            @Param("ownerId") Long ownerId);

    boolean existsByAccountNumber(String accountNumber);
}
