package com.wealthtracker.app.services;

public interface PriceUpdateService {
    void updateAllAssetPrices();
    Double fetchMutualFundNAV(String fundName);
    Double fetchStockOrETFPrice(String symbol);
}