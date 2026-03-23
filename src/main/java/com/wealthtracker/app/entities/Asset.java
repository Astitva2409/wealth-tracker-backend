package com.wealthtracker.app.entities;

import com.wealthtracker.app.entities.enums.AssetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "assets",
        indexes = {
                @Index(name = "idx_asset_user", columnList = "user_id"),
                @Index(name = "idx_asset_type", columnList = "assetType")
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;

    // Amount originally invested by the user
    @Column(nullable = false)
    private Double purchasePrice;

    // Current market value of the asset
    @Column(nullable = false)
    private Double currentPrice;

    // Every asset belongs to one user
    // FetchType.LAZY
    // Only loads the User from DB when explicitly accessed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isProfitable = false;

    @Column
    private String symbol;

    @Column
    private Double units;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}