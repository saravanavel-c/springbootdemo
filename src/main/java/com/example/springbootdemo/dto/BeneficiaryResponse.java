package com.example.springbootdemo.dto;

public class BeneficiaryResponse {

    private Long id;
    private String name;
    private String accountNumber;
    private String bankName;

    public BeneficiaryResponse() {
    }

    public BeneficiaryResponse(Long id, String name,
                               String accountNumber,
                               String bankName) {

        this.id = id;
        this.name = name;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}