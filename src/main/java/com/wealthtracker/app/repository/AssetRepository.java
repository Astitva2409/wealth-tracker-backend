package com.wealthtracker.app.repository;

import com.wealthtracker.app.entities.Asset;
import com.wealthtracker.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    // Get all assets belonging to a specific user
    List<Asset> findByUser(User user);

    // Find a specific asset only if it belongs to the requesting user
    // Prevents users from accessing other users' assets
    Optional<Asset> findByIdAndUser(Long id, User user);
}