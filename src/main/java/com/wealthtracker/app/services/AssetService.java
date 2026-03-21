package com.wealthtracker.app.services;

import com.wealthtracker.app.dto.AssetDto;
import com.wealthtracker.app.dto.AssetRequestDto;
import com.wealthtracker.app.dto.PortfolioSummaryDto;
import com.wealthtracker.app.entities.User;
import java.util.List;

public interface AssetService {

    AssetDto addAsset(AssetRequestDto assetRequestDto, User currentUser);

    List<AssetDto> getAllAssets(User currentUser);

    AssetDto updateAsset(Long assetId, AssetRequestDto assetRequestDto, User currentUser);

    void deleteAsset(Long assetId, User currentUser);

    PortfolioSummaryDto getPortfolioSummary(User currentUser);
}
