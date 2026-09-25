package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.AccountRequest;
import com.example.springbootdemo.dto.AccountResponse;
import com.example.springbootdemo.entity.Account;
import com.example.springbootdemo.entity.Customer;
import com.example.springbootdemo.exception.CustomerNotFoundException;
import com.example.springbootdemo.exception.ResourceNotFoundException;
import com.example.springbootdemo.repository.AccountRepository;
import com.example.springbootdemo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository) {

        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    // CREATE
    public AccountResponse createAccount(AccountRequest request) {

        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                request.getCustomerId()
                        ));

        Account account = new Account();

        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getBalance());
        account.setCustomer(customer);

        Account savedAccount = accountRepository.save(account);

        return convertToResponse(savedAccount);
    }

    // READ ALL
    public List<AccountResponse> getAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // READ ONE
    public AccountResponse getAccountById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found with id " + id
                        ));

        return convertToResponse(account);
    }

    // UPDATE
    public AccountResponse updateAccount(
            Long id,
            AccountRequest request) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found with id " + id
                        ));

        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                request.getCustomerId()
                        ));

        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getBalance());
        account.setCustomer(customer);

        Account updatedAccount = accountRepository.save(account);

        return convertToResponse(updatedAccount);
    }

    // DELETE
    public void deleteAccount(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found with id " + id
                        ));

        accountRepository.delete(account);
    }

    // ENTITY → RESPONSE DTO
    private AccountResponse convertToResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getCustomer().getId()
        );
    }
}