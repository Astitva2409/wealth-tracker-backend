package com.wealthtracker.app.services;

import com.wealthtracker.app.dto.TransactionDto;
import com.wealthtracker.app.dto.TransactionRequestDto;
import com.wealthtracker.app.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;

public interface TransactionService {

    TransactionDto addTransaction(TransactionRequestDto transactionRequestDto, User currentUser);

    List<TransactionDto> getAllTransactions(User currentUser);

    // Paginated version — same Page<T> + PageRequest pattern as Uber project
    Page<TransactionDto> getAllTransactionsPaginated(User currentUser, PageRequest pageRequest);

    void deleteTransaction(Long transactionId, User currentUser);
}
