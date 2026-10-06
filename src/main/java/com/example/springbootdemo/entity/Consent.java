package com.example.springbootdemo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consents")
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String consentReference;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, EXPIRED

    @Column(nullable = false)
    private String purpose;

    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        this.requestedAt = LocalDateTime.now();
        if (this.consentReference == null || this.consentReference.isBlank()) {
            this.consentReference = "CNS" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        }
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.expiresAt == null) {
            this.expiresAt = LocalDateTime.now().plusDays(30);
        }
    }

    public Consent() {
    }

    public Consent(Long id, String consentReference, Customer customer, String status, String purpose, LocalDateTime requestedAt, LocalDateTime approvedAt, LocalDateTime expiresAt) {
        this.id = id;
        this.consentReference = consentReference;
        this.customer = customer;
        this.status = status;
        this.purpose = purpose;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConsentReference() {
        return consentReference;
    }

    public void setConsentReference(String consentReference) {
        this.consentReference = consentReference;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
