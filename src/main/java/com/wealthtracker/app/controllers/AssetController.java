package com.wealthtracker.app.controllers;

import com.wealthtracker.app.dto.AssetDto;
import com.wealthtracker.app.dto.AssetRequestDto;
import com.wealthtracker.app.dto.PortfolioSummaryDto;
import com.wealthtracker.app.entities.User;
import com.wealthtracker.app.services.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Manage portfolio assets")
public class AssetController {

    private final AssetService assetService;

    // POST /api/assets
    // @AuthenticationPrincipal — Spring Security injects the logged-in User
    // This is the User object set in SecurityContextHolder by JwtAuthFilter
    // No need to parse the token again — Spring handles it
    @PostMapping
    @Operation(summary = "Add a new asset to portfolio")
    public ResponseEntity<AssetDto> addAsset(
            @RequestBody @Valid AssetRequestDto assetRequestDto,
            @AuthenticationPrincipal User currentUser) {
        AssetDto response = assetService.addAsset(assetRequestDto, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/assets
    // Returns all assets belonging to the logged-in user
    @GetMapping
    @Operation(summary = "Get all assets for logged-in user")
    public ResponseEntity<List<AssetDto>> getAllAssets(
            @AuthenticationPrincipal User currentUser) {
        List<AssetDto> response = assetService.getAllAssets(currentUser);
        return ResponseEntity.ok(response);
    }

    // PUT /api/assets/{assetId}
    // Update a specific asset — service verifies ownership
    @PutMapping("/{assetId}")
    @Operation(summary = "Update an existing asset")
    public ResponseEntity<AssetDto> updateAsset(
            @PathVariable Long assetId,
            @RequestBody @Valid AssetRequestDto assetRequestDto,
            @AuthenticationPrincipal User currentUser) {
        AssetDto response = assetService.updateAsset(assetId, assetRequestDto, currentUser);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/assets/{assetId}
    // Delete a specific asset — service verifies ownership
    @DeleteMapping("/{assetId}")
    @Operation(summary = "Delete an asset from portfolio")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable Long assetId,
            @AuthenticationPrincipal User currentUser) {
        assetService.deleteAsset(assetId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // GET /api/assets/summary
    // Powers the 3 stat cards on React Dashboard
    @GetMapping("/summary")
    @Operation(summary = "Get portfolio summary — total invested, current value, gain/loss")
    public ResponseEntity<PortfolioSummaryDto> getPortfolioSummary(
            @AuthenticationPrincipal User currentUser) {
        PortfolioSummaryDto response = assetService.getPortfolioSummary(currentUser);
        return ResponseEntity.ok(response);
    }
}