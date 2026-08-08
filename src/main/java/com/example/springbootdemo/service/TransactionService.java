package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.TransactionRequest;
import com.example.springbootdemo.dto.TransactionResponse;
import com.example.springbootdemo.entity.Account;
import com.example.springbootdemo.entity.Transaction;
import com.example.springbootdemo.exception.ResourceNotFoundException;
import com.example.springbootdemo.repository.AccountRepository;
import com.example.springbootdemo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public TransactionResponse createTransaction(
            Long accountId,
            TransactionRequest request) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found with id " + accountId
                        ));

        Transaction transaction = new Transaction();

        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAccount(account);

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return convertToResponse(savedTransaction);
    }

    public List<TransactionResponse> getTransactionsByAccount(
            Long accountId) {

        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException(
                    "Account not found with id " + accountId
            );
        }

        return transactionRepository
                .findByAccountId(accountId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse convertToResponse(
            Transaction transaction) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getAccount().getId()
        );
    }
}