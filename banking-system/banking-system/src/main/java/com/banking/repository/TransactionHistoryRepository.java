package com.banking.repository;

import com.banking.entity.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    // Paginated results — important for accounts with thousands of transactions
    Page<TransactionHistory> findByAccountIdOrderByTimestampDesc(Long accountId, Pageable pageable);

    // Date-range filter for statements
    List<TransactionHistory> findByAccountIdAndTimestampBetweenOrderByTimestampDesc(
            Long accountId,
            LocalDateTime from,
            LocalDateTime to);
}
