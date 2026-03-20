package com.wealthtracker.app.entities;

import com.wealthtracker.app.entities.enums.AssetType;
import com.wealthtracker.app.entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions",
        indexes = {
                @Index(name = "idx_transaction_user",  columnList = "user_id"),
                @Index(name = "idx_transaction_type",  columnList = "transactionType"),
                @Index(name = "idx_transaction_date",  columnList = "transactionDate")
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Double amount;

    // User-provided date of investment — not auto-generated
    // Using LocalDate (no time component needed for a transaction date)
    @Column(nullable = false)
    private LocalDate transactionDate;

    // Optional free-text note — no @Column constraints, can be null
    private String notes;

    // Every transaction belongs to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Auto-set when record is created in DB
    @CreationTimestamp
    private LocalDateTime createdAt;
}