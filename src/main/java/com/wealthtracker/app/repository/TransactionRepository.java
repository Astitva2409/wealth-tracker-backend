package com.wealthtracker.app.repository;

import com.wealthtracker.app.entities.Transaction;
import com.wealthtracker.app.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Get all transactions for a user — no pagination
    List<Transaction> findByUser(User user);

    // Paginated version — same Page<T> + PageRequest pattern as Uber project
    // Useful when a user has hundreds of transactions
    Page<Transaction> findByUser(User user, PageRequest pageRequest);

    // Find one transaction only if it belongs to the requesting user
    Optional<Transaction> findByIdAndUser(Long id, User user);
}