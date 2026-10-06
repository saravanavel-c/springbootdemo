package com.example.springbootdemo.dto;

import java.time.LocalDateTime;

public class ConsentResponse {

    private Long id;
    private String consentReference;
    private Long customerId;
    private String customerName;
    private String status;
    private String purpose;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime expiresAt;

    public ConsentResponse() {
    }

    public ConsentResponse(Long id, String consentReference, Long customerId, String customerName, String status, String purpose, LocalDateTime requestedAt, LocalDateTime approvedAt, LocalDateTime expiresAt) {
        this.id = id;
        this.consentReference = consentReference;
        this.customerId = customerId;
        this.customerName = customerName;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
