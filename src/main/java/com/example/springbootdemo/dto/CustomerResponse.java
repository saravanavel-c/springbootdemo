package com.example.springbootdemo.dto;

import java.time.LocalDateTime;

public class CustomerResponse {
    private Long id;
    private String customerNumber;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String status;
    private LocalDateTime createdAt;

    public CustomerResponse() {
    }

    public CustomerResponse(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public CustomerResponse(Long id, String customerNumber, String name, String email, String phone, String address, String status, LocalDateTime createdAt) {
        this.id = id;
        this.customerNumber = customerNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
