package com.wealthtracker.app.dto;

import com.wealthtracker.app.entities.enums.AssetType;
import com.wealthtracker.app.entities.enums.TransactionType;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long id;
    private String assetName;
    private AssetType assetType;
    private TransactionType transactionType;
    private Double amount;
    private LocalDate transactionDate;
    private String notes;
    private LocalDateTime createdAt;

    private Double navAtPurchase;
    private Double units;
}