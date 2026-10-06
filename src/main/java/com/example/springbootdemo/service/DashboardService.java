package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.AccountResponse;
import com.example.springbootdemo.dto.DashboardResponse;
import com.example.springbootdemo.dto.TransactionResponse;
import com.example.springbootdemo.entity.Account;
import com.example.springbootdemo.entity.Beneficiary;
import com.example.springbootdemo.entity.Transaction;
import com.example.springbootdemo.repository.AccountRepository;
import com.example.springbootdemo.repository.BeneficiaryRepository;
import com.example.springbootdemo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public DashboardService(AccountRepository accountRepository,
                             TransactionRepository transactionRepository,
                             BeneficiaryRepository beneficiaryRepository,
                             AccountService accountService,
                             TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    public DashboardResponse getDashboardData(Long customerId) {
        List<Account> accounts;
        List<Beneficiary> beneficiaries;

        if (customerId != null) {
            accounts = accountRepository.findByCustomerId(customerId);
            beneficiaries = beneficiaryRepository.findByCustomerId(customerId);
        } else {
            accounts = accountRepository.findAll();
            beneficiaries = beneficiaryRepository.findAll();
        }

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalAccounts = accounts.size();
        int activeAccounts = (int) accounts.stream()
                .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                .count();

        List<AccountResponse> accountResponses = accounts.stream()
                .map(accountService::convertToResponse)
                .collect(Collectors.toList());

        List<Transaction> allTransactions = transactionRepository.findAllByOrderByTransactionDateDesc();
        if (customerId != null) {
            List<Long> customerAccountIds = accounts.stream().map(Account::getId).toList();
            allTransactions = allTransactions.stream()
                    .filter(t -> customerAccountIds.contains(t.getAccount().getId()))
                    .collect(Collectors.toList());
        }

        int totalTransactions = allTransactions.size();

        List<TransactionResponse> recentTransactions = allTransactions.stream()
                .limit(5)
                .map(transactionService::convertToResponse)
                .collect(Collectors.toList());

        int activeBeneficiaries = (int) beneficiaries.stream()
                .filter(b -> "ACTIVE".equalsIgnoreCase(b.getStatus()))
                .count();

        return new DashboardResponse(
                totalBalance,
                totalAccounts,
                activeAccounts,
                totalTransactions,
                activeBeneficiaries,
                accountResponses,
                recentTransactions
        );
    }
}
