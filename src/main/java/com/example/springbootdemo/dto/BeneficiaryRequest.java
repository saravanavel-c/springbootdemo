package com.example.springbootdemo.dto;

import jakarta.validation.constraints.NotBlank;

public class BeneficiaryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    private String ifsc;
    private String status = "ACTIVE";
    private Long customerId;

    public BeneficiaryRequest() {
    }

    public BeneficiaryRequest(String name, String accountNumber, String bankName) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
    }

    public BeneficiaryRequest(String name, String accountNumber, String bankName, String ifsc, String status, Long customerId) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.ifsc = ifsc;
        this.status = status;
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}