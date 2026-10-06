package com.example.springbootdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CustomerRequest {

    private String customerNumber;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number is not valid")
    private String phone;

    private String address;
    private String status = "ACTIVE";

    public CustomerRequest() {
    }

    public CustomerRequest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public CustomerRequest(String customerNumber, String name, String email, String phone, String address, String status) {
        this.customerNumber = customerNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status;
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
}
