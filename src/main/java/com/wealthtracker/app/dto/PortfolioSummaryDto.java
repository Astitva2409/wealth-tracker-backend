package com.wealthtracker.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioSummaryDto {
    // Returned by GET /api/portfolio/summary
    // Powers the 3 stat cards on the React Dashboard
    private Double totalInvested;
    private Double totalCurrentValue;
    private Double totalGainLoss;
    private Double gainLossPercent;
    private Integer totalAssets;
}
