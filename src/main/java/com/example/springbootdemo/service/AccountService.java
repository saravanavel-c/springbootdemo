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
    private final AuditService auditService;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          AuditService auditService) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    public AccountResponse createAccount(AccountRequest request) {
        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        if (accountRepository.findByAccountNumber(request.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Account number " + request.getAccountNumber() + " already exists");
        }

        Account account = new Account();
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getBalance());
        account.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        account.setCustomer(customer);

        Account savedAccount = accountRepository.save(account);

        auditService.logAction("CREATE_ACCOUNT", "ACCOUNT", savedAccount.getId().toString(),
                "Created " + savedAccount.getAccountType() + " account " + savedAccount.getAccountNumber() + " for customer " + customer.getName());

        return convertToResponse(savedAccount);
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + id));
        return convertToResponse(account);
    }

    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse updateAccount(Long id, AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + id));

        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getBalance());
        if (request.getStatus() != null) {
            account.setStatus(request.getStatus());
        }
        account.setCustomer(customer);

        Account updatedAccount = accountRepository.save(account);

        auditService.logAction("UPDATE_ACCOUNT", "ACCOUNT", updatedAccount.getId().toString(),
                "Updated account " + updatedAccount.getAccountNumber() + " status: " + updatedAccount.getStatus());

        return convertToResponse(updatedAccount);
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + id));

        accountRepository.delete(account);

        auditService.logAction("DELETE_ACCOUNT", "ACCOUNT", id.toString(),
                "Deleted account " + account.getAccountNumber());
    }

    public AccountResponse convertToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus(),
                account.getCustomer().getId(),
                account.getCustomer().getName(),
                account.getCreatedAt()
        );
    }
}