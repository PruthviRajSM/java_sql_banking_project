package com.banking.dao;

import com.banking.model.Customer;
import com.banking.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Customer entity
 * Demonstrates DAO pattern, prepared statements, and proper resource management
 */
public class CustomerDAO {
    
    private final DatabaseConnection dbConnection;

    public CustomerDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Create a new customer in the database
     */
    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, age, email, contact_number) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setInt(2, customer.getAge());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getContactNumber());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setId(generatedKeys.getInt(1));
                        System.out.println("✅ Customer created successfully with ID: " + customer.getId());
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating customer: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // Duplicate entry error
                System.err.println("❌ Customer with this email already exists!");
            }
        }
        
        return false;
    }

    /**
     * Find customer by ID
     */
    public Optional<Customer> findById(int customerId) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding customer by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Find customer by email
     */
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding customer by email: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Get all customers
     */
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY id";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all customers: " + e.getMessage());
        }
        
        return customers;
    }

    /**
     * Update customer information
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET name = ?, age = ?, email = ?, contact_number = ? WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setInt(2, customer.getAge());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getContactNumber());
            pstmt.setInt(5, customer.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Customer updated successfully!");
                return true;
            } else {
                System.out.println("❌ Customer not found for update!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating customer: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // Duplicate entry error
                System.err.println("❌ Customer with this email already exists!");
            }
        }
        
        return false;
    }

    /**
     * Delete customer by ID
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Customer deleted successfully!");
                return true;
            } else {
                System.out.println("❌ Customer not found for deletion!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting customer: " + e.getMessage());
            if (e.getErrorCode() == 1451) { // Foreign key constraint error
                System.err.println("❌ Cannot delete customer with existing accounts!");
            }
        }
        
        return false;
    }

    /**
     * Search customers by name (partial match)
     */
    public List<Customer> searchByName(String name) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE name LIKE ? ORDER BY name";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error searching customers by name: " + e.getMessage());
        }
        
        return customers;
    }

    /**
     * Check if customer exists by email
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking customer existence: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get customer count
     */
    public int getCustomerCount() {
        String sql = "SELECT COUNT(*) FROM customers";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting customer count: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Map ResultSet to Customer object
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("email"),
            rs.getString("contact_number")
        );
    }
} 