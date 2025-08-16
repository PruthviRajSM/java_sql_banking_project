package com.banking.service;

import com.banking.dao.AccountDAO;
import com.banking.dao.CustomerDAO;
import com.banking.dao.TransactionDAO;
import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.Transaction;
import com.banking.util.InputValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Banking Service class - Business Logic Layer
 * Demonstrates service layer pattern, business rules, and transaction coordination
 */
public class BankingService {
    
    private final CustomerDAO customerDAO;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public BankingService() {
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    // ==================== CUSTOMER MANAGEMENT ====================

    /**
     * Register a new customer
     */
    public boolean registerCustomer(String name, int age, String email, String contactNumber) {
        // Validate input
        InputValidator.ValidationResult validation = InputValidator.validateCustomer(name, age, email, contactNumber);
        if (!validation.isValid()) {
            validation.printErrors();
            return false;
        }

        // Check if customer already exists
        if (customerDAO.existsByEmail(email)) {
            System.out.println("❌ Customer with this email already exists!");
            return false;
        }

        // Sanitize input
        String sanitizedName = InputValidator.sanitizeName(name);
        String sanitizedEmail = InputValidator.sanitizeEmail(email);
        String sanitizedContact = InputValidator.sanitizeContactNumber(contactNumber);

        // Create customer
        Customer customer = new Customer(sanitizedName, age, sanitizedEmail, sanitizedContact);
        
        return customerDAO.createCustomer(customer);
    }

    /**
     * Get customer by ID
     */
    public Optional<Customer> getCustomer(int customerId) {
        if (!InputValidator.isValidCustomerId(customerId)) {
            System.out.println("❌ Invalid customer ID!");
            return Optional.empty();
        }
        
        return customerDAO.findById(customerId);
    }

    /**
     * Get customer by email
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        if (!InputValidator.isValidEmail(email)) {
            System.out.println("❌ Invalid email format!");
            return Optional.empty();
        }
        
        return customerDAO.findByEmail(email);
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    /**
     * Search customers by name
     */
    public List<Customer> searchCustomersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("❌ Search name cannot be empty!");
            return List.of();
        }
        
        return customerDAO.searchByName(name.trim());
    }

    /**
     * Update customer information
     */
    public boolean updateCustomer(int customerId, String name, int age, String email, String contactNumber) {
        // Validate input
        InputValidator.ValidationResult validation = InputValidator.validateCustomer(name, age, email, contactNumber);
        if (!validation.isValid()) {
            validation.printErrors();
            return false;
        }

        // Check if customer exists
        Optional<Customer> existingCustomer = customerDAO.findById(customerId);
        if (existingCustomer.isEmpty()) {
            System.out.println("❌ Customer not found!");
            return false;
        }

        // Check if email is already taken by another customer
        Optional<Customer> customerWithEmail = customerDAO.findByEmail(email);
        if (customerWithEmail.isPresent() && customerWithEmail.get().getId() != customerId) {
            System.out.println("❌ Email is already taken by another customer!");
            return false;
        }

        // Sanitize input
        String sanitizedName = InputValidator.sanitizeName(name);
        String sanitizedEmail = InputValidator.sanitizeEmail(email);
        String sanitizedContact = InputValidator.sanitizeContactNumber(contactNumber);

        // Update customer
        Customer customer = new Customer(customerId, sanitizedName, age, sanitizedEmail, sanitizedContact);
        return customerDAO.updateCustomer(customer);
    }

    /**
     * Delete customer
     */
    public boolean deleteCustomer(int customerId) {
        if (!InputValidator.isValidCustomerId(customerId)) {
            System.out.println("❌ Invalid customer ID!");
            return false;
        }

        // Check if customer has accounts
        List<Account> customerAccounts = accountDAO.findByCustomerId(customerId);
        if (!customerAccounts.isEmpty()) {
            System.out.println("❌ Cannot delete customer with existing accounts!");
            System.out.println("   Customer has " + customerAccounts.size() + " account(s)");
            return false;
        }

        return customerDAO.deleteCustomer(customerId);
    }

    // ==================== ACCOUNT MANAGEMENT ====================

    /**
     * Create a new account for a customer
     */
    public boolean createAccount(int customerId, String accountType, BigDecimal initialBalance) {
        // Validate input
        InputValidator.ValidationResult validation = InputValidator.validateAccount(customerId, accountType, initialBalance);
        if (!validation.isValid()) {
            validation.printErrors();
            return false;
        }

        // Check if customer exists
        Optional<Customer> customer = customerDAO.findById(customerId);
        if (customer.isEmpty()) {
            System.out.println("❌ Customer not found!");
            return false;
        }

        // Create account
        Account account = new Account(customerId, accountType, initialBalance);
        return accountDAO.createAccount(account);
    }

    /**
     * Get account by ID
     */
    public Optional<Account> getAccount(int accountId) {
        if (!InputValidator.isValidAccountId(accountId)) {
            System.out.println("❌ Invalid account ID!");
            return Optional.empty();
        }
        
        return accountDAO.findById(accountId);
    }

    /**
     * Get all accounts for a customer
     */
    public List<Account> getCustomerAccounts(int customerId) {
        if (!InputValidator.isValidCustomerId(customerId)) {
            System.out.println("❌ Invalid customer ID!");
            return List.of();
        }
        
        return accountDAO.findByCustomerId(customerId);
    }

    /**
     * Get all accounts
     */
    public List<Account> getAllAccounts() {
        return accountDAO.findAll();
    }

    /**
     * Get accounts by type
     */
    public List<Account> getAccountsByType(String accountType) {
        if (!InputValidator.isValidAccountType(accountType)) {
            System.out.println("❌ Invalid account type!");
            return List.of();
        }
        
        return accountDAO.findByAccountType(accountType);
    }

    /**
     * Check account balance
     */
    public Optional<BigDecimal> checkBalance(int accountId) {
        Optional<Account> account = getAccount(accountId);
        if (account.isPresent()) {
            System.out.println("💰 Account Balance: $" + account.get().getBalance());
            return Optional.of(account.get().getBalance());
        }
        return Optional.empty();
    }

    // ==================== TRANSACTION OPERATIONS ====================

    /**
     * Deposit money to account
     */
    public boolean deposit(int accountId, BigDecimal amount) {
        // Validate input
        if (!InputValidator.isValidAmount(amount)) {
            System.out.println("❌ Invalid amount!");
            return false;
        }

        // Check if account exists
        Optional<Account> account = accountDAO.findById(accountId);
        if (account.isEmpty()) {
            System.out.println("❌ Account not found!");
            return false;
        }

        // Perform deposit
        boolean success = accountDAO.deposit(accountId, amount);
        
        if (success) {
            // Log transaction
            Transaction transaction = new Transaction(accountId, amount, Transaction.DEPOSIT);
            transactionDAO.createTransaction(transaction);
        }
        
        return success;
    }

    /**
     * Withdraw money from account
     */
    public boolean withdraw(int accountId, BigDecimal amount) {
        // Validate input
        if (!InputValidator.isValidAmount(amount)) {
            System.out.println("❌ Invalid amount!");
            return false;
        }

        // Check if account exists and has sufficient balance
        Optional<Account> account = accountDAO.findById(accountId);
        if (account.isEmpty()) {
            System.out.println("❌ Account not found!");
            return false;
        }

        if (!account.get().hasSufficientBalance(amount)) {
            System.out.println("❌ Insufficient balance! Current balance: $" + account.get().getBalance());
            return false;
        }

        // Perform withdrawal
        boolean success = accountDAO.withdraw(accountId, amount);
        
        if (success) {
            // Log transaction
            Transaction transaction = new Transaction(accountId, amount, Transaction.WITHDRAW);
            transactionDAO.createTransaction(transaction);
        }
        
        return success;
    }

    /**
     * Transfer money between accounts
     */
    public boolean transfer(int fromAccountId, int toAccountId, BigDecimal amount) {
        // Validate input
        if (!InputValidator.isValidAmount(amount)) {
            System.out.println("❌ Invalid amount!");
            return false;
        }

        if (fromAccountId == toAccountId) {
            System.out.println("❌ Cannot transfer to the same account!");
            return false;
        }

        // Check if both accounts exist
        Optional<Account> fromAccount = accountDAO.findById(fromAccountId);
        Optional<Account> toAccount = accountDAO.findById(toAccountId);
        
        if (fromAccount.isEmpty() || toAccount.isEmpty()) {
            System.out.println("❌ One or both accounts not found!");
            return false;
        }

        // Check sufficient balance
        if (!fromAccount.get().hasSufficientBalance(amount)) {
            System.out.println("❌ Insufficient balance in source account!");
            return false;
        }

        // Perform transfer
        boolean success = accountDAO.transfer(fromAccountId, toAccountId, amount);
        
        if (success) {
            // Log transaction
            Transaction transaction = new Transaction(fromAccountId, toAccountId, amount);
            transactionDAO.createTransaction(transaction);
        }
        
        return success;
    }

    // ==================== TRANSACTION HISTORY ====================

    /**
     * Get transaction history for an account
     */
    public List<Transaction> getAccountTransactionHistory(int accountId) {
        if (!InputValidator.isValidAccountId(accountId)) {
            System.out.println("❌ Invalid account ID!");
            return List.of();
        }
        
        return transactionDAO.findByAccountId(accountId);
    }

    /**
     * Get all transactions
     */
    public List<Transaction> getAllTransactions() {
        return transactionDAO.findAll();
    }

    /**
     * Get recent transactions
     */
    public List<Transaction> getRecentTransactions(int limit) {
        if (limit <= 0 || limit > 100) {
            System.out.println("❌ Invalid limit! Must be between 1 and 100");
            return List.of();
        }
        
        return transactionDAO.findRecentTransactions(limit);
    }

    /**
     * Get transaction summary for an account
     */
    public TransactionDAO.TransactionSummary getAccountTransactionSummary(int accountId) {
        if (!InputValidator.isValidAccountId(accountId)) {
            System.out.println("❌ Invalid account ID!");
            return new TransactionDAO.TransactionSummary(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        
        return transactionDAO.getTransactionSummary(accountId);
    }

    // ==================== SYSTEM STATISTICS ====================

    /**
     * Get system statistics
     */
    public void displaySystemStatistics() {
        System.out.println("\n📊 SYSTEM STATISTICS");
        System.out.println("====================");
        System.out.println("👥 Total Customers: " + customerDAO.getCustomerCount());
        System.out.println("💳 Total Accounts: " + accountDAO.getAccountCount());
        System.out.println("📝 Total Transactions: " + transactionDAO.getTransactionCount());
        
        // Account type distribution
        System.out.println("\n📈 Account Distribution:");
        List<Account> savingsAccounts = accountDAO.findByAccountType(Account.SAVINGS);
        List<Account> currentAccounts = accountDAO.findByAccountType(Account.CURRENT);
        List<Account> fixedDepositAccounts = accountDAO.findByAccountType(Account.FIXED_DEPOSIT);
        
        System.out.println("   💰 Savings Accounts: " + savingsAccounts.size());
        System.out.println("   🏦 Current Accounts: " + currentAccounts.size());
        System.out.println("   📅 Fixed Deposit Accounts: " + fixedDepositAccounts.size());
        
        // Recent activity
        System.out.println("\n🕒 Recent Activity:");
        List<Transaction> recentTransactions = transactionDAO.findRecentTransactions(5);
        if (recentTransactions.isEmpty()) {
            System.out.println("   No recent transactions");
        } else {
            for (Transaction transaction : recentTransactions) {
                System.out.println("   " + transaction.getDescription() + " at " + transaction.getTimestamp());
            }
        }
    }

    /**
     * Display customer details with accounts
     */
    public void displayCustomerDetails(int customerId) {
        Optional<Customer> customer = getCustomer(customerId);
        if (customer.isEmpty()) {
            return;
        }

        Customer c = customer.get();
        System.out.println("\n👤 CUSTOMER DETAILS");
        System.out.println("===================");
        System.out.println("ID: " + c.getId());
        System.out.println("Name: " + c.getName());
        System.out.println("Age: " + c.getAge());
        System.out.println("Email: " + c.getEmail());
        System.out.println("Contact: " + c.getContactNumber());

        List<Account> accounts = getCustomerAccounts(customerId);
        System.out.println("\n💳 ACCOUNTS (" + accounts.size() + ")");
        System.out.println("===============");
        
        if (accounts.isEmpty()) {
            System.out.println("No accounts found");
        } else {
            for (Account account : accounts) {
                System.out.println("Account ID: " + account.getId());
                System.out.println("Type: " + account.getAccountType());
                System.out.println("Balance: $" + account.getBalance());
                System.out.println("Created: " + account.getCreatedAt());
                System.out.println("---");
            }
        }
    }
} 