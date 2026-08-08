package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.TransactionRequest;
import com.example.springbootdemo.dto.TransactionResponse;
import com.example.springbootdemo.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        transactionService.createTransaction(
                                accountId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>>
    getTransactions(@PathVariable Long accountId) {

        return ResponseEntity.ok(
                transactionService
                        .getTransactionsByAccount(accountId)
        );
    }
}