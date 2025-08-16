package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transaction model class representing a bank transaction
 * Demonstrates encapsulation and transaction tracking
 */
public class Transaction {
    private int id;
    private Integer fromAccountId;
    private Integer toAccountId;
    private BigDecimal amount;
    private String type;
    private LocalDateTime timestamp;

    // Transaction types as constants
    public static final String DEPOSIT = "DEPOSIT";
    public static final String WITHDRAW = "WITHDRAW";
    public static final String TRANSFER = "TRANSFER";

    // Default constructor
    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for deposit/withdraw transactions
    public Transaction(Integer accountId, BigDecimal amount, String type) {
        this.fromAccountId = type.equals(WITHDRAW) ? accountId : null;
        this.toAccountId = type.equals(DEPOSIT) ? accountId : null;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for transfer transactions
    public Transaction(Integer fromAccountId, Integer toAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = TRANSFER;
        this.timestamp = LocalDateTime.now();
    }

    // Full constructor with ID
    public Transaction(int id, Integer fromAccountId, Integer toAccountId, BigDecimal amount, String type, LocalDateTime timestamp) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Integer fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Integer getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Integer toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Business logic methods
    public boolean isValidType() {
        return type != null && 
               (type.equals(DEPOSIT) || 
                type.equals(WITHDRAW) || 
                type.equals(TRANSFER));
    }

    public boolean isValidAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isTransfer() {
        return TRANSFER.equals(type);
    }

    public boolean isDeposit() {
        return DEPOSIT.equals(type);
    }

    public boolean isWithdraw() {
        return WITHDRAW.equals(type);
    }

    public String getDescription() {
        switch (type) {
            case DEPOSIT:
                return String.format("Deposit of $%s to account %d", amount, toAccountId);
            case WITHDRAW:
                return String.format("Withdrawal of $%s from account %d", amount, fromAccountId);
            case TRANSFER:
                return String.format("Transfer of $%s from account %d to account %d", amount, fromAccountId, toAccountId);
            default:
                return "Unknown transaction type";
        }
    }

    // Validation methods
    public boolean isValid() {
        if (!isValidType() || !isValidAmount()) {
            return false;
        }

        switch (type) {
            case DEPOSIT:
                return toAccountId != null && fromAccountId == null;
            case WITHDRAW:
                return fromAccountId != null && toAccountId == null;
            case TRANSFER:
                return fromAccountId != null && toAccountId != null && !fromAccountId.equals(toAccountId);
            default:
                return false;
        }
    }

    // Override methods for proper object behavior
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction that = (Transaction) obj;
        return id == that.id && 
               Objects.equals(fromAccountId, that.fromAccountId) && 
               Objects.equals(toAccountId, that.toAccountId) && 
               Objects.equals(amount, that.amount) && 
               Objects.equals(type, that.type) && 
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromAccountId, toAccountId, amount, type, timestamp);
    }

    @Override
    public String toString() {
        return String.format("Transaction{id=%d, fromAccountId=%s, toAccountId=%s, amount=%s, type='%s', timestamp=%s}", 
                           id, fromAccountId, toAccountId, amount, type, timestamp);
    }
} 