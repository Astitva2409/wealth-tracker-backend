package com.wealthtracker.app.controllers;

import com.wealthtracker.app.services.PriceUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@Tag(name = "Price Updates", description = "Trigger live price updates from AMFI and Alpha Vantage APIs")
public class PriceUpdateController {

    private final PriceUpdateService priceUpdateService;

    // POST /api/prices/update
    @PostMapping("/update")
    @Operation(
            summary = "Trigger price update for all assets",
            description = "Manually triggers the scheduled job that fetches latest NAV " +
                    "from AMFI (Mutual Funds) and Alpha Vantage (Stocks/ETFs) " +
                    "and updates currentPrice for all assets in the database."
    )
    public ResponseEntity<String> triggerPriceUpdate() {
        priceUpdateService.updateAllAssetPrices();
        return ResponseEntity.ok("Price update triggered successfully");
    }

    // GET /api/prices/mutual-fund?name=Parag Parikh
    @GetMapping("/mutual-fund")
    @Operation(
            summary = "Fetch latest NAV for a mutual fund",
            description = "Fetches the latest Net Asset Value (NAV) from AMFI India " +
                    "for the given mutual fund name. Uses partial name matching."
    )
    public ResponseEntity<Double> getMutualFundPrice(
            @Parameter(description = "Mutual fund name or partial name e.g. 'Parag Parikh'")
            @RequestParam String name) {
        Double nav = priceUpdateService.fetchMutualFundNAV(name);
        return ResponseEntity.ok(nav);
    }

    // GET /api/prices/stock?symbol=HDFCBANK
    @GetMapping("/stock")
    @Operation(
            summary = "Fetch latest price for a stock or ETF",
            description = "Fetches the latest market price from Alpha Vantage API " +
                    "for the given stock/ETF symbol. Appends .BSE suffix for Indian stocks."
    )
    public ResponseEntity<Double> getStockPrice(
            @Parameter(description = "Stock or ETF symbol e.g. 'HDFCBANK' or 'NIFTYBEES'")
            @RequestParam String symbol) {
        Double price = priceUpdateService.fetchStockOrETFPrice(symbol);
        return ResponseEntity.ok(price);
    }
}