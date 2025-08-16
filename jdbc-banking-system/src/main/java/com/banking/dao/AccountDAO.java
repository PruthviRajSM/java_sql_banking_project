package com.banking.dao;

import com.banking.model.Account;
import com.banking.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Account entity
 * Demonstrates DAO pattern, prepared statements, and proper resource management
 */
public class AccountDAO {
    
    private final DatabaseConnection dbConnection;

    public AccountDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Create a new account in the database
     */
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (customer_id, account_type, balance, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, account.getCustomerId());
            pstmt.setString(2, account.getAccountType());
            pstmt.setBigDecimal(3, account.getBalance());
            pstmt.setTimestamp(4, Timestamp.valueOf(account.getCreatedAt()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        account.setId(generatedKeys.getInt(1));
                        System.out.println("✅ Account created successfully with ID: " + account.getId());
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating account: " + e.getMessage());
            if (e.getErrorCode() == 1452) { // Foreign key constraint error
                System.err.println("❌ Customer ID does not exist!");
            }
        }
        
        return false;
    }

    /**
     * Find account by ID
     */
    public Optional<Account> findById(int accountId) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAccount(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding account by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Get all accounts for a customer
     */
    public List<Account> findByCustomerId(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY created_at";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding accounts by customer ID: " + e.getMessage());
        }
        
        return accounts;
    }

    /**
     * Get all accounts
     */
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY id";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all accounts: " + e.getMessage());
        }
        
        return accounts;
    }

    /**
     * Update account balance
     */
    public boolean updateBalance(int accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setInt(2, accountId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Account balance updated successfully!");
                return true;
            } else {
                System.out.println("❌ Account not found for balance update!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating account balance: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Deposit money to account
     */
    public boolean deposit(int accountId, BigDecimal amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, amount);
            pstmt.setInt(2, accountId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Deposit successful! Amount: $" + amount);
                return true;
            } else {
                System.out.println("❌ Account not found for deposit!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error processing deposit: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Withdraw money from account
     */
    public boolean withdraw(int accountId, BigDecimal amount) {
        // First check if account has sufficient balance
        Optional<Account> accountOpt = findById(accountId);
        if (accountOpt.isEmpty()) {
            System.out.println("❌ Account not found!");
            return false;
        }
        
        Account account = accountOpt.get();
        if (!account.hasSufficientBalance(amount)) {
            System.out.println("❌ Insufficient balance! Current balance: $" + account.getBalance());
            return false;
        }
        
        String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, amount);
            pstmt.setInt(2, accountId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Withdrawal successful! Amount: $" + amount);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error processing withdrawal: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Transfer money between accounts
     */
    public boolean transfer(int fromAccountId, int toAccountId, BigDecimal amount) {
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction
            
            // Check if both accounts exist and have sufficient balance
            Optional<Account> fromAccountOpt = findById(fromAccountId);
            Optional<Account> toAccountOpt = findById(toAccountId);
            
            if (fromAccountOpt.isEmpty() || toAccountOpt.isEmpty()) {
                System.out.println("❌ One or both accounts not found!");
                return false;
            }
            
            Account fromAccount = fromAccountOpt.get();
            if (!fromAccount.hasSufficientBalance(amount)) {
                System.out.println("❌ Insufficient balance in source account!");
                return false;
            }
            
            // Perform withdrawal from source account
            String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
            try (PreparedStatement withdrawStmt = connection.prepareStatement(withdrawSql)) {
                withdrawStmt.setBigDecimal(1, amount);
                withdrawStmt.setInt(2, fromAccountId);
                
                int withdrawRows = withdrawStmt.executeUpdate();
                if (withdrawRows == 0) {
                    connection.rollback();
                    System.out.println("❌ Failed to withdraw from source account!");
                    return false;
                }
            }
            
            // Perform deposit to destination account
            String depositSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement depositStmt = connection.prepareStatement(depositSql)) {
                depositStmt.setBigDecimal(1, amount);
                depositStmt.setInt(2, toAccountId);
                
                int depositRows = depositStmt.executeUpdate();
                if (depositRows == 0) {
                    connection.rollback();
                    System.out.println("❌ Failed to deposit to destination account!");
                    return false;
                }
            }
            
            connection.commit(); // Commit transaction
            System.out.println("✅ Transfer successful! Amount: $" + amount);
            return true;
            
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback on error
                }
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Error processing transfer: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Reset auto-commit
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("❌ Error closing connection: " + e.getMessage());
            }
        }
        
        return false;
    }

    /**
     * Delete account by ID
     */
    public boolean deleteAccount(int accountId) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Account deleted successfully!");
                return true;
            } else {
                System.out.println("❌ Account not found for deletion!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting account: " + e.getMessage());
            if (e.getErrorCode() == 1451) { // Foreign key constraint error
                System.err.println("❌ Cannot delete account with existing transactions!");
            }
        }
        
        return false;
    }

    /**
     * Get accounts by type
     */
    public List<Account> findByAccountType(String accountType) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE account_type = ? ORDER BY created_at";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, accountType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding accounts by type: " + e.getMessage());
        }
        
        return accounts;
    }

    /**
     * Get account count
     */
    public int getAccountCount() {
        String sql = "SELECT COUNT(*) FROM accounts";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting account count: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Map ResultSet to Account object
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        return new Account(
            rs.getInt("id"),
            rs.getInt("customer_id"),
            rs.getString("account_type"),
            rs.getBigDecimal("balance"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
} 