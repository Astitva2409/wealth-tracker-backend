package com.wealthtracker.app.services.impl;

import com.wealthtracker.app.dto.TransactionDto;
import com.wealthtracker.app.dto.TransactionRequestDto;
import com.wealthtracker.app.entities.Transaction;
import com.wealthtracker.app.entities.User;
import com.wealthtracker.app.exception.ResourceNotFoundException;
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

    @Override
    public TransactionDto addTransaction(TransactionRequestDto transactionRequestDto,
                                         User currentUser) {
        // Builder pattern — consistent with AssetServiceImpl
        Transaction transaction = Transaction.builder()
                .assetName(transactionRequestDto.getAssetName())
                .assetType(transactionRequestDto.getAssetType())
                .transactionType(transactionRequestDto.getTransactionType())
                .amount(transactionRequestDto.getAmount())
                .transactionDate(transactionRequestDto.getTransactionDate())
                .notes(transactionRequestDto.getNotes())
                .user(currentUser)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return modelMapper.map(savedTransaction, TransactionDto.class);
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