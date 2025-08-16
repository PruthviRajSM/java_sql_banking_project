package com.banking.dao;

import com.banking.model.Transaction;
import com.banking.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Transaction entity
 * Demonstrates DAO pattern, prepared statements, and proper resource management
 */
public class TransactionDAO {
    
    private final DatabaseConnection dbConnection;

    public TransactionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Create a new transaction in the database
     */
    public boolean createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (from_account, to_account, amount, type, timestamp) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setObject(1, transaction.getFromAccountId());
            pstmt.setObject(2, transaction.getToAccountId());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getType());
            pstmt.setTimestamp(5, Timestamp.valueOf(transaction.getTimestamp()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setId(generatedKeys.getInt(1));
                        System.out.println("✅ Transaction logged successfully with ID: " + transaction.getId());
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating transaction: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Find transaction by ID
     */
    public Optional<Transaction> findById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding transaction by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Get all transactions for an account
     */
    public List<Transaction> findByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_account = ? OR to_account = ? ORDER BY timestamp DESC";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding transactions by account ID: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * Get all transactions
     */
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * Get transactions by type
     */
    public List<Transaction> findByType(String type) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE type = ? ORDER BY timestamp DESC";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding transactions by type: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * Get transactions within a date range
     */
    public List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding transactions by date range: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * Get transactions with amount greater than specified value
     */
    public List<Transaction> findByAmountGreaterThan(BigDecimal amount) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE amount > ? ORDER BY amount DESC, timestamp DESC";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, amount);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding transactions by amount: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * Get recent transactions (last N transactions)
     */
    public List<Transaction> findRecentTransactions(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding recent transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * Get transaction summary for an account
     */
    public TransactionSummary getTransactionSummary(int accountId) {
        String sql = "SELECT " +
                    "COUNT(*) as total_transactions, " +
                    "SUM(CASE WHEN type = 'DEPOSIT' THEN amount ELSE 0 END) as total_deposits, " +
                    "SUM(CASE WHEN type = 'WITHDRAW' THEN amount ELSE 0 END) as total_withdrawals, " +
                    "SUM(CASE WHEN type = 'TRANSFER' AND from_account = ? THEN amount ELSE 0 END) as total_sent, " +
                    "SUM(CASE WHEN type = 'TRANSFER' AND to_account = ? THEN amount ELSE 0 END) as total_received " +
                    "FROM transactions WHERE from_account = ? OR to_account = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);
            pstmt.setInt(3, accountId);
            pstmt.setInt(4, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TransactionSummary(
                        rs.getInt("total_transactions"),
                        rs.getBigDecimal("total_deposits"),
                        rs.getBigDecimal("total_withdrawals"),
                        rs.getBigDecimal("total_sent"),
                        rs.getBigDecimal("total_received")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting transaction summary: " + e.getMessage());
        }
        
        return new TransactionSummary(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Delete transaction by ID
     */
    public boolean deleteTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, transactionId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Transaction deleted successfully!");
                return true;
            } else {
                System.out.println("❌ Transaction not found for deletion!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting transaction: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get transaction count
     */
    public int getTransactionCount() {
        String sql = "SELECT COUNT(*) FROM transactions";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting transaction count: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Map ResultSet to Transaction object
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("id"),
            rs.getObject("from_account", Integer.class),
            rs.getObject("to_account", Integer.class),
            rs.getBigDecimal("amount"),
            rs.getString("type"),
            rs.getTimestamp("timestamp").toLocalDateTime()
        );
    }

    /**
     * Transaction summary class
     */
    public static class TransactionSummary {
        private final int totalTransactions;
        private final BigDecimal totalDeposits;
        private final BigDecimal totalWithdrawals;
        private final BigDecimal totalSent;
        private final BigDecimal totalReceived;

        public TransactionSummary(int totalTransactions, BigDecimal totalDeposits, 
                                BigDecimal totalWithdrawals, BigDecimal totalSent, BigDecimal totalReceived) {
            this.totalTransactions = totalTransactions;
            this.totalDeposits = totalDeposits != null ? totalDeposits : BigDecimal.ZERO;
            this.totalWithdrawals = totalWithdrawals != null ? totalWithdrawals : BigDecimal.ZERO;
            this.totalSent = totalSent != null ? totalSent : BigDecimal.ZERO;
            this.totalReceived = totalReceived != null ? totalReceived : BigDecimal.ZERO;
        }

        public int getTotalTransactions() { return totalTransactions; }
        public BigDecimal getTotalDeposits() { return totalDeposits; }
        public BigDecimal getTotalWithdrawals() { return totalWithdrawals; }
        public BigDecimal getTotalSent() { return totalSent; }
        public BigDecimal getTotalReceived() { return totalReceived; }

        @Override
        public String toString() {
            return String.format("TransactionSummary{total=%d, deposits=%s, withdrawals=%s, sent=%s, received=%s}", 
                               totalTransactions, totalDeposits, totalWithdrawals, totalSent, totalReceived);
        }
    }
} 