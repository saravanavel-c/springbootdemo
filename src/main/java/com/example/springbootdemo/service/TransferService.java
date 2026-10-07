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

        // ==========================================
        // 1. FIND SOURCE ACCOUNT
        // ==========================================

        Account fromAccount = accountRepository.findById(
                request.getFromAccountId()
        ).orElseThrow(() -> new ResourceNotFoundException(
                "Source account not found with id "
                        + request.getFromAccountId()
        ));

        // ==========================================
        // 2. CHECK SOURCE ACCOUNT STATUS
        // ==========================================

        if (!"ACTIVE".equalsIgnoreCase(fromAccount.getStatus())) {
            throw new IllegalStateException(
                    "Source account "
                            + fromAccount.getAccountNumber()
                            + " is "
                            + fromAccount.getStatus()
            );
        }

        // ==========================================
        // 3. VALIDATE AMOUNT
        // ==========================================

        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero"
            );
        }

        // ==========================================
        // 4. CHECK SOURCE BALANCE
        // ==========================================

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient balance in source account. Current balance: ₹"
                            + fromAccount.getBalance()
            );
        }

        // ==========================================
        // 5. RESOLVE TRANSFER DESTINATION
        // ==========================================

        String toAccNum = request.getToAccountNumber();

        Beneficiary beneficiary = null;
        Account toAccount = null;

        /*
         * OPTION A:
         * User selected a saved beneficiary.
         *
         * A beneficiary can belong to another bank.
         * Therefore, DO NOT search the Account table.
         */

        if (request.getBeneficiaryId() != null) {

            beneficiary = beneficiaryRepository.findById(
                    request.getBeneficiaryId()
            ).orElseThrow(() -> new ResourceNotFoundException(
                    "Beneficiary not found with id "
                            + request.getBeneficiaryId()
            ));

            // Check beneficiary status
            if (!"ACTIVE".equalsIgnoreCase(beneficiary.getStatus())) {
                throw new IllegalStateException(
                        "Beneficiary "
                                + beneficiary.getName()
                                + " is "
                                + beneficiary.getStatus()
                );
            }

            // Get destination from beneficiary
            toAccNum = beneficiary.getAccountNumber();

            if (toAccNum == null || toAccNum.isBlank()) {
                throw new IllegalArgumentException(
                        "Beneficiary account number is missing"
                );
            }

        }

        /*
         * OPTION B:
         * User entered an account number directly.
         *
         * This is treated as an internal transfer.
         */

        else {

            if (toAccNum == null || toAccNum.isBlank()) {
                throw new IllegalArgumentException(
                        "Target account number or beneficiary is required"
                );
            }

            // Prevent transfer to same account
            if (fromAccount.getAccountNumber()
                    .equalsIgnoreCase(toAccNum)) {

                throw new IllegalArgumentException(
                        "Cannot transfer money to the same account"
                );
            }

            // Find internal destination account
            final String destinationAccountNumber = toAccNum;

            toAccount = accountRepository
                    .findByAccountNumber(destinationAccountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Destination account not found with account number "
                                    + destinationAccountNumber
                    ));

            // Check destination account status
            if (!"ACTIVE".equalsIgnoreCase(toAccount.getStatus())) {
                throw new IllegalStateException(
                        "Destination account "
                                + toAccount.getAccountNumber()
                                + " is "
                                + toAccount.getStatus()
                );
            }
        }

        // ==========================================
        // 6. GENERATE TRANSACTION REFERENCE
        // ==========================================

        String txnRef =
                "TXN"
                        + System.currentTimeMillis()
                        + UUID.randomUUID()
                                .toString()
                                .substring(0, 4)
                                .toUpperCase();

        String description =
                request.getDescription() != null
                        && !request.getDescription().isBlank()
                        ? request.getDescription()
                        : "Transfer to " + toAccNum;

        // ==========================================
        // 7. DEBIT SOURCE ACCOUNT
        // ==========================================

        fromAccount.setBalance(
                fromAccount.getBalance().subtract(amount)
        );

        accountRepository.save(fromAccount);

        // ==========================================
        // 8. CREATE SENDER TRANSACTION
        // ==========================================

        Transaction senderTxn = new Transaction();

        senderTxn.setTransactionReference(txnRef);
        senderTxn.setType("TRANSFER");
        senderTxn.setAmount(amount);
        senderTxn.setStatus("SUCCESS");
        senderTxn.setDescription(
                "Transfer Out: " + description
        );
        senderTxn.setTransactionDate(
                LocalDateTime.now()
        );
        senderTxn.setAccount(fromAccount);
        senderTxn.setBeneficiary(beneficiary);

        transactionRepository.save(senderTxn);

        // ==========================================
        // 9. INTERNAL TRANSFER
        // ==========================================

        /*
         * If toAccount is not null, this was a direct
         * internal account transfer.
         *
         * If beneficiary was selected, toAccount is null
         * because the beneficiary may belong to another bank.
         */

        if (toAccount != null) {

            // Credit destination account
            toAccount.setBalance(
                    toAccount.getBalance().add(amount)
            );

            accountRepository.save(toAccount);

            // Create receiver transaction
            Transaction recipientTxn = new Transaction();

            recipientTxn.setTransactionReference(
                    txnRef + "-REC"
            );
            recipientTxn.setType("CREDIT");
            recipientTxn.setAmount(amount);
            recipientTxn.setStatus("SUCCESS");
            recipientTxn.setDescription(
                    "Transfer In from "
                            + fromAccount.getAccountNumber()
                            + ": "
                            + description
            );
            recipientTxn.setTransactionDate(
                    LocalDateTime.now()
            );
            recipientTxn.setAccount(toAccount);

            transactionRepository.save(recipientTxn);
        }

        // ==========================================
        // 10. AUDIT LOG
        // ==========================================

        auditService.logAction(
                "TRANSFER_MONEY",
                "TRANSACTION",
                senderTxn.getId().toString(),
                "Transferred ₹"
                        + amount
                        + " from "
                        + fromAccount.getAccountNumber()
                        + " to "
                        + toAccNum
                        + " Ref: "
                        + txnRef
        );

        // ==========================================
        // 11. RETURN RESPONSE
        // ==========================================

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