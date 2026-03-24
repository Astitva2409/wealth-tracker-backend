package com.wealthtracker.app.dto;

import com.wealthtracker.app.entities.enums.AssetType;
import com.wealthtracker.app.entities.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionRequestDto {

    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotNull(message = "Asset type is required")
    private AssetType assetType;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    // Optional — no validation annotation needed
    private String notes;

    private Double navAtPurchase;
}