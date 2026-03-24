package com.wealthtracker.app.services.impl;

import com.wealthtracker.app.dto.TransactionDto;
import com.wealthtracker.app.dto.TransactionRequestDto;
import com.wealthtracker.app.entities.Transaction;
import com.wealthtracker.app.entities.User;
import com.wealthtracker.app.entities.enums.TransactionType;
import com.wealthtracker.app.exception.ResourceNotFoundException;
import com.wealthtracker.app.repository.AssetRepository;
import com.wealthtracker.app.repository.TransactionRepository;
import com.wealthtracker.app.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final AssetRepository assetRepository;

    @Override
    public TransactionDto addTransaction(TransactionRequestDto dto, User user) {

        // 1. Compute units for this transaction
        Double units;
        if (dto.getNavAtPurchase() != null && dto.getNavAtPurchase() > 0) {
            units = dto.getAmount() / dto.getNavAtPurchase();
        } else {
            units = null;
        }

        // 2. Save transaction record with computed units
        Transaction transaction = Transaction.builder()
                .assetName(dto.getAssetName())
                .amount(dto.getAmount())
                .navAtPurchase(dto.getNavAtPurchase())
                .units(units)
                .transactionType(dto.getTransactionType())
                .assetType(dto.getAssetType())
                .transactionDate(dto.getTransactionDate())
                .user(user)
                .build();

        transactionRepository.save(transaction);

        // 3. Update linked asset
        assetRepository.findByNameAndUser(dto.getAssetName(), user)
                .ifPresent(asset -> {

                    if (dto.getTransactionType() == TransactionType.SIP ||
                            dto.getTransactionType() == TransactionType.LUMP_SUM) {

                        // Increase total amount invested
                        asset.setPurchasePrice(asset.getPurchasePrice() + dto.getAmount());

                        // Add newly bought units to existing units
                        if (units != null) {
                            double existingUnits = asset.getUnits() != null ? asset.getUnits() : 0;
                            asset.setUnits(existingUnits + units);
                        }
                    }

                    assetRepository.save(asset);
                });

        return modelMapper.map(transaction, TransactionDto.class);
    }

    @Override
    public List<TransactionDto> getAllTransactions(User currentUser) {
        return transactionRepository.findByUser(currentUser)
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .toList();
    }

    @Override
    public Page<TransactionDto> getAllTransactionsPaginated(User currentUser,
                                                            PageRequest pageRequest) {
        // Same Page<T> pattern as Uber project's getAllRidesOfRider()
        return transactionRepository.findByUser(currentUser, pageRequest)
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public void deleteTransaction(Long transactionId, User currentUser) {
        // findByIdAndUser — same ownership check as AssetServiceImpl
        // User can only delete their own transactions
        Transaction transaction = transactionRepository
                .findByIdAndUser(transactionId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found with id: " + transactionId));

        transactionRepository.delete(transaction);
    }
}