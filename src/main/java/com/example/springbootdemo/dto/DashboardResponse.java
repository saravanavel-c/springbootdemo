package com.example.springbootdemo.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private BigDecimal totalBalance;
    private int totalAccounts;
    private int activeAccountsCount;
    private int totalTransactions;
    private int activeBeneficiariesCount;
    private List<AccountResponse> accounts;
    private List<TransactionResponse> recentTransactions;

    public DashboardResponse() {
    }

    public DashboardResponse(BigDecimal totalBalance, int totalAccounts, int activeAccountsCount, int totalTransactions, int activeBeneficiariesCount, List<AccountResponse> accounts, List<TransactionResponse> recentTransactions) {
        this.totalBalance = totalBalance;
        this.totalAccounts = totalAccounts;
        this.activeAccountsCount = activeAccountsCount;
        this.totalTransactions = totalTransactions;
        this.activeBeneficiariesCount = activeBeneficiariesCount;
        this.accounts = accounts;
        this.recentTransactions = recentTransactions;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public int getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(int totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public int getActiveAccountsCount() {
        return activeAccountsCount;
    }

    public void setActiveAccountsCount(int activeAccountsCount) {
        this.activeAccountsCount = activeAccountsCount;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getActiveBeneficiariesCount() {
        return activeBeneficiariesCount;
    }

    public void setActiveBeneficiariesCount(int activeBeneficiariesCount) {
        this.activeBeneficiariesCount = activeBeneficiariesCount;
    }

    public List<AccountResponse> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountResponse> accounts) {
        this.accounts = accounts;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
