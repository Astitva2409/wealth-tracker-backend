package com.wealthtracker.app.services.impl;

import com.wealthtracker.app.dto.AssetDto;
import com.wealthtracker.app.dto.AssetRequestDto;
import com.wealthtracker.app.dto.PortfolioSummaryDto;
import com.wealthtracker.app.entities.Asset;
import com.wealthtracker.app.entities.User;
import com.wealthtracker.app.exception.ResourceNotFoundException;
import com.wealthtracker.app.repository.AssetRepository;
import com.wealthtracker.app.services.AssetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final ModelMapper modelMapper;

    @Override
    public AssetDto addAsset(AssetRequestDto assetRequestDto, User currentUser) {

        // Builder pattern — same as Uber project style
        Asset asset = Asset.builder()
                .name(assetRequestDto.getName())
                .symbol(assetRequestDto.getSymbol())
                .assetType(assetRequestDto.getAssetType())
                .purchasePrice(assetRequestDto.getPurchasePrice())
                .currentPrice(assetRequestDto.getCurrentPrice())
                .user(currentUser)
                .units(assetRequestDto.getUnits())
                .build();

        Asset savedAsset = assetRepository.save(asset);
        return mapToDto(savedAsset);
    }

    @Override
    public List<AssetDto> getAllAssets(User currentUser) {
        return assetRepository.findByUser(currentUser)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public AssetDto updateAsset(Long assetId, AssetRequestDto assetRequestDto, User currentUser) {

        // findByIdAndUser ensures user can only update their own assets
        Asset asset = assetRepository.findByIdAndUser(assetId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset not found with id: " + assetId));

        // Update only the fields that can change
        asset.setName(assetRequestDto.getName());
        asset.setAssetType(assetRequestDto.getAssetType());
        asset.setPurchasePrice(assetRequestDto.getPurchasePrice());
        asset.setCurrentPrice(assetRequestDto.getCurrentPrice());

        Asset updatedAsset = assetRepository.save(asset);
        return mapToDto(updatedAsset);
    }

    @Override
    public void deleteAsset(Long assetId, User currentUser) {

        // Verify asset belongs to current user before deleting
        Asset asset = assetRepository.findByIdAndUser(assetId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset not found with id: " + assetId));

        assetRepository.delete(asset);
    }

    @Override
    public PortfolioSummaryDto getPortfolioSummary(User currentUser) {
        List<Asset> assets = assetRepository.findByUser(currentUser);

        // Compute all values server-side — same logic as frontend useMemo
        // but now authoritative and secure on the backend
        double totalInvested = assets.stream()
                .mapToDouble(Asset::getPurchasePrice)
                .sum();

        double totalCurrentValue = assets.stream()
                .mapToDouble(Asset::getCurrentPrice)
                .sum();

        double totalGainLoss = totalCurrentValue - totalInvested;

        double gainLossPercent = totalInvested > 0
                ? (totalGainLoss / totalInvested) * 100
                : 0.0;

        return new PortfolioSummaryDto(
                totalInvested,
                totalCurrentValue,
                totalGainLoss,
                gainLossPercent,
                assets.size()
        );
    }

    // ── Private helper ───────────────────────────────────────
    // Converts Asset entity to AssetDto
    // ModelMapper handles field-by-field mapping automatically
    // Then we manually set the computed fields isProfitable and gainLoss
    private AssetDto mapToDto(Asset asset) {
        AssetDto dto = modelMapper.map(asset, AssetDto.class);

        double gainLoss = asset.getCurrentPrice() - asset.getPurchasePrice();
        dto.setGainLoss(gainLoss);
        dto.setIsProfitable(gainLoss >= 0);

        return dto;
    }
}