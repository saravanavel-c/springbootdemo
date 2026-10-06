package com.example.springbootdemo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private String transactionReference;
    private String type;
    private BigDecimal amount;
    private String status;
    private String description;
    private LocalDateTime transactionDate;
    private Long accountId;
    private String accountNumber;
    private Long beneficiaryId;
    private String beneficiaryName;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, String type, BigDecimal amount,
                               LocalDateTime transactionDate, Long accountId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.accountId = accountId;
    }

    public TransactionResponse(Long id, String transactionReference, String type, BigDecimal amount, String status, String description, LocalDateTime transactionDate, Long accountId, String accountNumber, Long beneficiaryId, String beneficiaryName) {
        this.id = id;
        this.transactionReference = transactionReference;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.description = description;
        this.transactionDate = transactionDate;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.beneficiaryId = beneficiaryId;
        this.beneficiaryName = beneficiaryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }
}