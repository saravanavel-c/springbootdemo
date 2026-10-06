package com.example.springbootdemo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferResponse {

    private String transactionReference;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String status;
    private String message;
    private LocalDateTime timestamp;

    public TransferResponse() {
    }

    public TransferResponse(String transactionReference, String fromAccountNumber, String toAccountNumber, BigDecimal amount, String status, String message, LocalDateTime timestamp) {
        this.transactionReference = transactionReference;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
