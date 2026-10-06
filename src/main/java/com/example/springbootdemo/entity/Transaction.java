package com.example.springbootdemo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String transactionReference;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status = "SUCCESS";

    private String description;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "beneficiary_id")
    private Beneficiary beneficiary;

    @PrePersist
    protected void onCreate() {
        if (this.transactionDate == null) {
            this.transactionDate = LocalDateTime.now();
        }
        this.createdAt = LocalDateTime.now();
        if (this.transactionReference == null || this.transactionReference.isBlank()) {
            this.transactionReference = "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }
        if (this.status == null) {
            this.status = "SUCCESS";
        }
    }

    public Transaction() {
    }

    public Transaction(Long id, String type, BigDecimal amount,
                       LocalDateTime transactionDate, Account account) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.account = account;
    }

    public Transaction(Long id, String transactionReference, String type, BigDecimal amount,
                       String status, String description, LocalDateTime transactionDate,
                       Account account, Beneficiary beneficiary) {
        this.id = id;
        this.transactionReference = transactionReference;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.description = description;
        this.transactionDate = transactionDate;
        this.account = account;
        this.beneficiary = beneficiary;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }
}