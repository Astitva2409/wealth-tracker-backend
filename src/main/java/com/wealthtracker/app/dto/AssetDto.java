package com.wealthtracker.app.dto;

import com.wealthtracker.app.entities.enums.AssetType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssetDto {
    private Long id;
    private String name;
    private AssetType assetType;
    private Double purchasePrice;
    private Double currentPrice;

    // These two are computed in the service layer — not stored in DB
    // Same "derived data" principle as the frontend
    private Boolean isProfitable;
    private Double gainLoss;

    private LocalDateTime createdAt;
}