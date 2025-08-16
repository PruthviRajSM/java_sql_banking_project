package com.banking.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection utility class
 * Demonstrates singleton pattern and proper resource management
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";
    
    private String url;
    private String username;
    private String password;
    private String driver;

    // Private constructor (Singleton pattern)
    private DatabaseConnection() {
        loadProperties();
    }

    // Singleton instance getter
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Load database configuration from properties file
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Could not find " + CONFIG_FILE);
            }
            properties.load(input);
            
            this.url = properties.getProperty("DB_URL");
            this.username = properties.getProperty("DB_USER");
            this.password = properties.getProperty("DB_PASSWORD");
            this.driver = properties.getProperty("DB_DRIVER");
            
            // Validate required properties
            if (url == null || username == null || password == null || driver == null) {
                throw new RuntimeException("Missing required database configuration properties");
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Error loading database configuration", e);
        }
    }

    // Get database connection
    public Connection getConnection() throws SQLException {
        try {
            // Load the JDBC driver
            Class.forName(driver);
            
            // Create and return connection
            return DriverManager.getConnection(url, username, password);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found: " + driver, e);
        }
    }

    // Test database connection
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    // Close connection safely
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // Get configuration values
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getDriver() {
        return driver;
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return String.format("DatabaseConnection{url='%s', username='%s', driver='%s'}", 
                           url, username, driver);
    }
} 