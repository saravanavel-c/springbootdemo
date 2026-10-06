package com.example.springbootdemo.dto;

import java.util.List;

public class UserProfileResponse {

    private String username;
    private String email;
    private List<String> roles;
    private CustomerResponse customer;

    public UserProfileResponse() {
    }

    public UserProfileResponse(String username, String email, List<String> roles, CustomerResponse customer) {
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.customer = customer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public CustomerResponse getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerResponse customer) {
        this.customer = customer;
    }
}
