package com.wealthtracker.app.services.impl;

import com.wealthtracker.app.entities.Asset;
import com.wealthtracker.app.entities.enums.AssetType;
import com.wealthtracker.app.repository.AssetRepository;
import com.wealthtracker.app.services.PriceUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

// @Slf4j gives us a `log` object for logging — same as System.out.println but professional
@Slf4j
@Service
@RequiredArgsConstructor
public class PriceUpdateServiceImpl implements PriceUpdateService {

    private final AssetRepository assetRepository;
    private final Asset asset;

    // Injected from application.properties
    @Value("${alpha.vantage.api.key}")
    private String alphaVantageApiKey;

    // RestTemplate — Spring's HTTP client for calling external APIs
    private final RestTemplate restTemplate;

    // ── Scheduled job ────────────────────────────────────────
    // Runs every day at 6 PM IST (12:30 UTC) on weekdays
    // Cron format: second minute hour day month weekday
    // "0 30 12 * * MON-FRI" = 12:30 UTC every Monday to Friday
    @Scheduled(cron = "0 30 12 * * MON-FRI")
    @Override
    public void updateAllAssetPrices() {
        log.info("Starting scheduled price update...");
        List<Asset> allAssets = assetRepository.findAll();
        int updatedCount = 0;

        for (Asset asset : allAssets) {
            try {
                Double newPrice = null;

                if (asset.getAssetType() == AssetType.MUTUAL_FUND) {
                    // AMFI uses fund name
                    newPrice = fetchMutualFundNAV(asset.getName());
                } else {
                    // Alpha Vantage uses BSE symbol
                    String ticker = asset.getSymbol() != null
                            ? asset.getSymbol()
                            : asset.getName();  // fallback to name if symbol not set
                    newPrice = fetchStockOrETFPrice(ticker);
                }

                if (newPrice != null && newPrice > 0) {
                    asset.setCurrentPrice(newPrice);
                    asset.setIsProfitable(newPrice >= asset.getPurchasePrice());
                    assetRepository.save(asset);
                    updatedCount++;
                    log.info("Updated {} → ₹{}", asset.getName(), newPrice);
                }
            } catch (Exception e) {
                log.error("Failed to update price for {}: {}", asset.getName(), e.getMessage());
            }
        }
        log.info("Price update complete. {}/{} assets updated.", updatedCount, allAssets.size());
    }

    // ── AMFI API — Mutual Fund NAV ───────────────────────────
    // AMFI (Association of Mutual Funds in India) publishes daily NAV
    // for ALL mutual funds as a plain text file — completely free, no API key needed
    // URL: https://www.amfiindia.com/spages/NAVAll.txt
    // Format: SchemeCode;ISINDivPayoutISINGrowth;ISINDivReinvestment;SchemeName;NetAssetValue;Date
    @Override
    public Double fetchMutualFundNAV(String fundName) {
        try {
            String url = "https://www.amfiindia.com/spages/NAVAll.txt";
            String response = restTemplate.getForObject(url, String.class);

            if (response == null) return null;

            // Search for the fund by name (case-insensitive partial match)
            // Each line looks like: 120503;INF879O01019;INF879O01019;Parag Parikh Flexi Cap Fund;78.4520;21-Mar-2026
            String[] lines = response.split("\n");
            String searchName = fundName.toLowerCase();

            for (String line : lines) {
                String[] parts = line.split(";");
                if (parts.length >= 5) {
                    String schemeName = parts[3].toLowerCase();
                    if (schemeName.contains(searchName) ||
                            searchName.contains(schemeName.substring(0, Math.min(schemeName.length(), 10)))) {
                        String navStr = parts[4].trim();
                        Double nav = Double.parseDouble(navStr);
                        Double units = asset.getPurchasePrice() / nav;
                        return units * nav;
                    }
                }
            }

            log.warn("No NAV found for fund: {}", fundName);
            return null;

        } catch (Exception e) {
            log.error("AMFI API error for {}: {}", fundName, e.getMessage());
            return null;
        }
    }

    // ── Alpha Vantage API — Stocks + ETFs ────────────────────
    // Alpha Vantage provides real-time and historical stock prices
    // Free tier: 25 requests/day
    // We use GLOBAL_QUOTE endpoint which returns the latest price
    @Override
    @SuppressWarnings("unchecked")
    public Double fetchStockOrETFPrice(String symbol) {
        try {
            // Alpha Vantage expects a stock symbol like "HDFCBANK.BSE" or "NIFTYBEES.BSE"
            // We append ".BSE" for Indian stocks on BSE exchange
            String formattedSymbol = symbol.toUpperCase()
                    .replace(" ", "")
                    .replace("-", "");

            // Try with BSE suffix first (Indian market)
            String url = String.format(
                    "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s.BSE&apikey=%s",
                    formattedSymbol, alphaVantageApiKey
            );

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("Global Quote")) {
                Map<String, String> quote = (Map<String, String>) response.get("Global Quote");
                String price = quote.get("05. price");
                if (price != null && !price.isEmpty()) {
                    return Double.parseDouble(price);
                }
            }

            log.warn("No price found for symbol: {}", symbol);
            return null;

        } catch (Exception e) {
            log.error("Alpha Vantage error for {}: {}", symbol, e.getMessage());
            return null;
        }
    }
}