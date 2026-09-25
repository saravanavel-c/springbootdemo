package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.AccountRequest;
import com.example.springbootdemo.dto.AccountResponse;
import com.example.springbootdemo.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.createAccount(request));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {

        return ResponseEntity.ok(
                accountService.getAllAccounts()
        );
    }

    // READ ONE
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                accountService.getAccountById(accountId)
        );
    }

    // UPDATE
    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountRequest request) {

        return ResponseEntity.ok(
                accountService.updateAccount(accountId, request)
        );
    }

    // DELETE
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long accountId) {

        accountService.deleteAccount(accountId);

        return ResponseEntity.noContent().build();
    }
}