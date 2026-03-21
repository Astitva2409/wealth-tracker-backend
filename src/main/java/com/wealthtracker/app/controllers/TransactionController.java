package com.wealthtracker.app.controllers;

import com.wealthtracker.app.dto.TransactionDto;
import com.wealthtracker.app.dto.TransactionRequestDto;
import com.wealthtracker.app.entities.User;
import com.wealthtracker.app.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Log and manage investment transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // POST /api/transactions
    @PostMapping
    @Operation(summary = "Log a new SIP or lump-sum transaction")
    public ResponseEntity<TransactionDto> addTransaction(
            @RequestBody @Valid TransactionRequestDto transactionRequestDto,
            @AuthenticationPrincipal User currentUser) {
        TransactionDto response = transactionService.addTransaction(
                transactionRequestDto, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/transactions
    // Returns all transactions for the logged-in user (no pagination)
    @GetMapping
    @Operation(summary = "Get all transactions for logged-in user")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(
            @AuthenticationPrincipal User currentUser) {
        List<TransactionDto> response = transactionService.getAllTransactions(currentUser);
        return ResponseEntity.ok(response);
    }

    // GET /api/transactions/paginated?page=0&size=10&sort=transactionDate,desc
    // Paginated version — same pattern as Uber project's ride history
    // Query params: page (0-indexed), size (items per page), sort field + direction
    @GetMapping("/paginated")
    @Operation(summary = "Get paginated transactions with sort options")
    public ResponseEntity<Page<TransactionDto>> getAllTransactionsPaginated(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        // Build sort direction from query param
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<TransactionDto> response = transactionService
                .getAllTransactionsPaginated(currentUser, pageRequest);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/transactions/{transactionId}
    @DeleteMapping("/{transactionId}")
    @Operation(summary = "Delete a transaction")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long transactionId,
            @AuthenticationPrincipal User currentUser) {
        transactionService.deleteTransaction(transactionId, currentUser);
        return ResponseEntity.noContent().build();
    }
}