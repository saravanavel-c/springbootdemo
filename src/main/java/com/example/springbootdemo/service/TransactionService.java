package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.TransactionRequest;
import com.example.springbootdemo.dto.TransactionResponse;
import com.example.springbootdemo.entity.Account;
import com.example.springbootdemo.entity.Beneficiary;
import com.example.springbootdemo.entity.Transaction;
import com.example.springbootdemo.exception.ResourceNotFoundException;
import com.example.springbootdemo.repository.AccountRepository;
import com.example.springbootdemo.repository.BeneficiaryRepository;
import com.example.springbootdemo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final AuditService auditService;

    public TransactionService(TransactionRepository transactionRepository,
                               AccountRepository accountRepository,
                               BeneficiaryRepository beneficiaryRepository,
                               AuditService auditService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.auditService = auditService;
    }

    @Transactional
    public TransactionResponse createTransaction(Long accountId, TransactionRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId));

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new IllegalStateException("Account " + account.getAccountNumber() + " is " + account.getStatus() + ". Transactions are not allowed.");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }

        String type = request.getType() != null ? request.getType().toUpperCase() : "DEBIT";

        if ("DEBIT".equalsIgnoreCase(type) || "TRANSFER_OUT".equalsIgnoreCase(type)) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance in account " + account.getAccountNumber() + ". Current balance: ₹" + account.getBalance());
            }
            account.setBalance(account.getBalance().subtract(amount));
        } else if ("CREDIT".equalsIgnoreCase(type) || "TRANSFER_IN".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance().add(amount));
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + type);
        }

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAccount(account);
        transaction.setStatus("SUCCESS");
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : type + " operation");
        transaction.setTransactionReference("TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());

        if (request.getBeneficiaryId() != null) {
            Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId()).orElse(null);
            transaction.setBeneficiary(beneficiary);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        auditService.logAction("TRANSACTION_" + type, "TRANSACTION", savedTransaction.getId().toString(),
                type + " ₹" + amount + " on account " + account.getAccountNumber() + " Ref: " + savedTransaction.getTransactionReference());

        return convertToResponse(savedTransaction);
    }

    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id " + accountId);
        }

        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAllByOrderByTransactionDateDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionReference(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getAccount().getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getBeneficiary() != null ? transaction.getBeneficiary().getId() : null,
                transaction.getBeneficiary() != null ? transaction.getBeneficiary().getName() : null
        );
    }
}