package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Account model class representing a bank account
 * Demonstrates encapsulation, validation, and business logic
 */
public class Account {
    private int id;
    private int customerId;
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    // Account types as constants
    public static final String SAVINGS = "SAVINGS";
    public static final String CURRENT = "CURRENT";
    public static final String FIXED_DEPOSIT = "FIXED_DEPOSIT";

    // Default constructor
    public Account() {
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    // Parameterized constructor
    public Account(int customerId, String accountType, BigDecimal balance) {
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    // Full constructor with ID
    public Account(int id, int customerId, String accountType, BigDecimal balance, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Business logic methods
    public boolean isValidAccountType() {
        return accountType != null && 
               (accountType.equals(SAVINGS) || 
                accountType.equals(CURRENT) || 
                accountType.equals(FIXED_DEPOSIT));
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return balance != null && amount != null && balance.compareTo(amount) >= 0;
    }

    public void deposit(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
        }
    }

    public boolean withdraw(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0 && hasSufficientBalance(amount)) {
            this.balance = this.balance.subtract(amount);
            return true;
        }
        return false;
    }

    public boolean transfer(Account targetAccount, BigDecimal amount) {
        if (targetAccount != null && withdraw(amount)) {
            targetAccount.deposit(amount);
            return true;
        }
        return false;
    }

    // Validation methods
    public boolean isValid() {
        return customerId > 0 && 
               isValidAccountType() && 
               balance != null && 
               balance.compareTo(BigDecimal.ZERO) >= 0;
    }

    // Override methods for proper object behavior
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return id == account.id && 
               customerId == account.customerId && 
               Objects.equals(accountType, account.accountType) && 
               Objects.equals(balance, account.balance) && 
               Objects.equals(createdAt, account.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, accountType, balance, createdAt);
    }

    @Override
    public String toString() {
        return String.format("Account{id=%d, customerId=%d, accountType='%s', balance=%s, createdAt=%s}", 
                           id, customerId, accountType, balance, createdAt);
    }
} 