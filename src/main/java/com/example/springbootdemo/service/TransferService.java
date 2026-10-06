package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.TransferRequest;
import com.example.springbootdemo.dto.TransferResponse;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final TransactionRepository transactionRepository;
    private final AuditService auditService;

    public TransferService(AccountRepository accountRepository,
                           BeneficiaryRepository beneficiaryRepository,
                           TransactionRepository transactionRepository,
                           AuditService auditService) {
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.transactionRepository = transactionRepository;
        this.auditService = auditService;
    }

    @Transactional
    public TransferResponse executeTransfer(TransferRequest request) {
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found with id " + request.getFromAccountId()));

        if (!"ACTIVE".equalsIgnoreCase(fromAccount.getStatus())) {
            throw new IllegalStateException("Source account " + fromAccount.getAccountNumber() + " is " + fromAccount.getStatus());
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in source account. Current balance: ₹" + fromAccount.getBalance());
        }

        String toAccNum = request.getToAccountNumber();
        Beneficiary beneficiary = null;

        if (request.getBeneficiaryId() != null) {
            beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId()).orElse(null);
            if (beneficiary != null) {
                toAccNum = beneficiary.getAccountNumber();
            }
        }

        if (toAccNum == null || toAccNum.isBlank()) {
            throw new IllegalArgumentException("Target account number or beneficiary is required");
        }

        if (fromAccount.getAccountNumber().equalsIgnoreCase(toAccNum)) {
            throw new IllegalArgumentException("Cannot transfer money to the same account");
        }

        // Generate unified reference
        String txnRef = "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String description = request.getDescription() != null && !request.getDescription().isBlank()
                ? request.getDescription()
                : "Transfer to " + toAccNum;

        // Deduct sender balance
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);

        Transaction senderTxn = new Transaction();
        senderTxn.setTransactionReference(txnRef);
        senderTxn.setType("TRANSFER");
        senderTxn.setAmount(amount);
        senderTxn.setStatus("SUCCESS");
        senderTxn.setDescription("Transfer Out: " + description);
        senderTxn.setTransactionDate(LocalDateTime.now());
        senderTxn.setAccount(fromAccount);
        senderTxn.setBeneficiary(beneficiary);
        transactionRepository.save(senderTxn);

        // Check if recipient account exists in internal system
        Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(toAccNum);
        if (toAccountOpt.isPresent()) {
            Account toAccount = toAccountOpt.get();
            if ("ACTIVE".equalsIgnoreCase(toAccount.getStatus())) {
                toAccount.setBalance(toAccount.getBalance().add(amount));
                accountRepository.save(toAccount);

                Transaction recipientTxn = new Transaction();
                recipientTxn.setTransactionReference(txnRef + "-REC");
                recipientTxn.setType("CREDIT");
                recipientTxn.setAmount(amount);
                recipientTxn.setStatus("SUCCESS");
                recipientTxn.setDescription("Transfer In from " + fromAccount.getAccountNumber() + ": " + description);
                recipientTxn.setTransactionDate(LocalDateTime.now());
                recipientTxn.setAccount(toAccount);
                transactionRepository.save(recipientTxn);
            }
        }

        auditService.logAction("TRANSFER_MONEY", "TRANSACTION", senderTxn.getId().toString(),
                "Transferred ₹" + amount + " from " + fromAccount.getAccountNumber() + " to " + toAccNum + " Ref: " + txnRef);

        return new TransferResponse(
                txnRef,
                fromAccount.getAccountNumber(),
                toAccNum,
                amount,
                "SUCCESS",
                "Transfer completed successfully",
                LocalDateTime.now()
        );
    }
}
