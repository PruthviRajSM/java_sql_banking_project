package com.banking;

import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.Transaction;
import com.banking.service.BankingService;
import com.banking.util.DatabaseConnection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Main Banking Application
 * Demonstrates a complete console-based banking system with user-friendly interface
 */
public class Main {
    
    private static final BankingService bankingService = new BankingService();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        System.out.println("🏦 Welcome to Java OOP Banking System! 🏦");
        System.out.println("==========================================");
        
        // Test database connection
        if (!testDatabaseConnection()) {
            System.out.println("❌ Database connection failed! Please check your configuration.");
            System.out.println("   Make sure MySQL is running and config.properties is set correctly.");
            return;
        }
        
        System.out.println("✅ Database connection successful!");
        System.out.println();
        
        // Main application loop
        while (true) {
            displayMainMenu();
            int choice = getValidChoice(1, 8);
            
            switch (choice) {
                case 1:
                    customerManagementMenu();
                    break;
                case 2:
                    accountManagementMenu();
                    break;
                case 3:
                    transactionMenu();
                    break;
                case 4:
                    reportsMenu();
                    break;
                case 5:
                    systemStatistics();
                    break;
                case 6:
                    adminMenu();
                    break;
                case 7:
                    helpMenu();
                    break;
                case 8:
                    System.out.println("\n👋 Thank you for using Java OOP Banking System!");
                    System.out.println("   Goodbye! 🙏");
                    scanner.close();
                    return;
                default:
                    System.out.println("❌ Invalid choice! Please try again.");
            }
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
    }

    // ==================== MENU DISPLAY METHODS ====================

    private static void displayMainMenu() {
        System.out.println("\n🏦 MAIN MENU");
        System.out.println("============");
        System.out.println("1. 👤 Customer Management");
        System.out.println("2. 💳 Account Management");
        System.out.println("3. 💰 Transaction Operations");
        System.out.println("4. 📊 Reports & History");
        System.out.println("5. 📈 System Statistics");
        System.out.println("6. 🔐 Admin Panel");
        System.out.println("7. ❓ Help");
        System.out.println("8. 🚪 Exit");
        System.out.print("\nEnter your choice (1-8): ");
    }

    private static void customerManagementMenu() {
        while (true) {
            System.out.println("\n👤 CUSTOMER MANAGEMENT");
            System.out.println("=====================");
            System.out.println("1. ➕ Register New Customer");
            System.out.println("2. 🔍 Find Customer by ID");
            System.out.println("3. 📧 Find Customer by Email");
            System.out.println("4. 🔍 Search Customers by Name");
            System.out.println("5. 📋 View All Customers");
            System.out.println("6. ✏️  Update Customer");
            System.out.println("7. 🗑️  Delete Customer");
            System.out.println("8. ⬅️  Back to Main Menu");
            
            int choice = getValidChoice(1, 8);
            
            switch (choice) {
                case 1:
                    registerNewCustomer();
                    break;
                case 2:
                    findCustomerById();
                    break;
                case 3:
                    findCustomerByEmail();
                    break;
                case 4:
                    searchCustomersByName();
                    break;
                case 5:
                    viewAllCustomers();
                    break;
                case 6:
                    updateCustomer();
                    break;
                case 7:
                    deleteCustomer();
                    break;
                case 8:
                    return;
            }
        }
    }

    private static void accountManagementMenu() {
        while (true) {
            System.out.println("\n💳 ACCOUNT MANAGEMENT");
            System.out.println("====================");
            System.out.println("1. ➕ Create New Account");
            System.out.println("2. 🔍 Find Account by ID");
            System.out.println("3. 👤 View Customer Accounts");
            System.out.println("4. 📋 View All Accounts");
            System.out.println("5. 💰 Check Balance");
            System.out.println("6. 🗑️  Delete Account");
            System.out.println("7. ⬅️  Back to Main Menu");
            
            int choice = getValidChoice(1, 7);
            
            switch (choice) {
                case 1:
                    createNewAccount();
                    break;
                case 2:
                    findAccountById();
                    break;
                case 3:
                    viewCustomerAccounts();
                    break;
                case 4:
                    viewAllAccounts();
                    break;
                case 5:
                    checkBalance();
                    break;
                case 6:
                    deleteAccount();
                    break;
                case 7:
                    return;
            }
        }
    }

    private static void transactionMenu() {
        while (true) {
            System.out.println("\n💰 TRANSACTION OPERATIONS");
            System.out.println("=========================");
            System.out.println("1. 💵 Deposit Money");
            System.out.println("2. 💸 Withdraw Money");
            System.out.println("3. 🔄 Transfer Money");
            System.out.println("4. ⬅️  Back to Main Menu");
            
            int choice = getValidChoice(1, 4);
            
            switch (choice) {
                case 1:
                    depositMoney();
                    break;
                case 2:
                    withdrawMoney();
                    break;
                case 3:
                    transferMoney();
                    break;
                case 4:
                    return;
            }
        }
    }

    private static void reportsMenu() {
        while (true) {
            System.out.println("\n📊 REPORTS & HISTORY");
            System.out.println("====================");
            System.out.println("1. 📝 Account Transaction History");
            System.out.println("2. 📊 Account Transaction Summary");
            System.out.println("3. 📋 All Transactions");
            System.out.println("4. 🕒 Recent Transactions");
            System.out.println("5. 👤 Customer Details with Accounts");
            System.out.println("6. ⬅️  Back to Main Menu");
            
            int choice = getValidChoice(1, 6);
            
            switch (choice) {
                case 1:
                    accountTransactionHistory();
                    break;
                case 2:
                    accountTransactionSummary();
                    break;
                case 3:
                    viewAllTransactions();
                    break;
                case 4:
                    viewRecentTransactions();
                    break;
                case 5:
                    viewCustomerDetails();
                    break;
                case 6:
                    return;
            }
        }
    }

    private static void adminMenu() {
        System.out.print("\n🔐 Enter admin password: ");
        String password = scanner.nextLine();
        
        if (!ADMIN_PASSWORD.equals(password)) {
            System.out.println("❌ Invalid password! Access denied.");
            return;
        }
        
        System.out.println("✅ Admin access granted!");
        
        while (true) {
            System.out.println("\n🔐 ADMIN PANEL");
            System.out.println("==============");
            System.out.println("1. 📊 System Statistics");
            System.out.println("2. 🗑️  Delete All Data (DANGER!)");
            System.out.println("3. ⬅️  Back to Main Menu");
            
            int choice = getValidChoice(1, 3);
            
            switch (choice) {
                case 1:
                    systemStatistics();
                    break;
                case 2:
                    deleteAllData();
                    break;
                case 3:
                    return;
            }
        }
    }

    private static void helpMenu() {
        System.out.println("\n❓ HELP & INFORMATION");
        System.out.println("=====================");
        System.out.println("🏦 Java OOP Banking System");
        System.out.println("   A comprehensive banking management system built with:");
        System.out.println("   • Java Object-Oriented Programming");
        System.out.println("   • JDBC for database connectivity");
        System.out.println("   • MySQL database");
        System.out.println("   • Clean architecture patterns");
        System.out.println();
        System.out.println("📋 Features:");
        System.out.println("   • Customer management (CRUD operations)");
        System.out.println("   • Multiple account types (Savings, Current, Fixed Deposit)");
        System.out.println("   • Transaction operations (Deposit, Withdraw, Transfer)");
        System.out.println("   • Transaction history and reporting");
        System.out.println("   • Input validation and error handling");
        System.out.println("   • Secure database operations");
        System.out.println();
        System.out.println("🔧 Technical Highlights:");
        System.out.println("   • DAO Pattern for data access");
        System.out.println("   • Service Layer for business logic");
        System.out.println("   • Model classes with proper encapsulation");
        System.out.println("   • Utility classes for validation and connection");
        System.out.println("   • Prepared statements for SQL injection prevention");
        System.out.println("   • Transaction management for data integrity");
        System.out.println();
        System.out.println("👨‍💻 Admin Password: admin123");
    }

    // ==================== CUSTOMER OPERATIONS ====================

    private static void registerNewCustomer() {
        System.out.println("\n➕ REGISTER NEW CUSTOMER");
        System.out.println("=======================");
        
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter customer age: ");
        int age = getValidInteger();
        
        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine();
        
        boolean success = bankingService.registerCustomer(name, age, email, contactNumber);
        if (success) {
            System.out.println("✅ Customer registered successfully!");
        }
    }

    private static void findCustomerById() {
        System.out.println("\n🔍 FIND CUSTOMER BY ID");
        System.out.println("======================");
        
        System.out.print("Enter customer ID: ");
        int customerId = getValidInteger();
        
        var customer = bankingService.getCustomer(customerId);
        if (customer.isPresent()) {
            displayCustomer(customer.get());
        } else {
            System.out.println("❌ Customer not found!");
        }
    }

    private static void findCustomerByEmail() {
        System.out.println("\n📧 FIND CUSTOMER BY EMAIL");
        System.out.println("=========================");
        
        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();
        
        var customer = bankingService.getCustomerByEmail(email);
        if (customer.isPresent()) {
            displayCustomer(customer.get());
        } else {
            System.out.println("❌ Customer not found!");
        }
    }

    private static void searchCustomersByName() {
        System.out.println("\n🔍 SEARCH CUSTOMERS BY NAME");
        System.out.println("===========================");
        
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine();
        
        List<Customer> customers = bankingService.searchCustomersByName(searchTerm);
        if (customers.isEmpty()) {
            System.out.println("❌ No customers found!");
        } else {
            System.out.println("✅ Found " + customers.size() + " customer(s):");
            customers.forEach(Main::displayCustomer);
        }
    }

    private static void viewAllCustomers() {
        System.out.println("\n📋 ALL CUSTOMERS");
        System.out.println("================");
        
        List<Customer> customers = bankingService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("❌ No customers found!");
        } else {
            System.out.println("✅ Total customers: " + customers.size());
            customers.forEach(Main::displayCustomer);
        }
    }

    private static void updateCustomer() {
        System.out.println("\n✏️  UPDATE CUSTOMER");
        System.out.println("==================");
        
        System.out.print("Enter customer ID: ");
        int customerId = getValidInteger();
        
        var existingCustomer = bankingService.getCustomer(customerId);
        if (existingCustomer.isEmpty()) {
            System.out.println("❌ Customer not found!");
            return;
        }
        
        Customer customer = existingCustomer.get();
        System.out.println("Current customer details:");
        displayCustomer(customer);
        
        System.out.print("Enter new name (or press Enter to keep current): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) name = customer.getName();
        
        System.out.print("Enter new age (or 0 to keep current): ");
        int age = getValidInteger();
        if (age == 0) age = customer.getAge();
        
        System.out.print("Enter new email (or press Enter to keep current): ");
        String email = scanner.nextLine();
        if (email.trim().isEmpty()) email = customer.getEmail();
        
        System.out.print("Enter new contact number (or press Enter to keep current): ");
        String contactNumber = scanner.nextLine();
        if (contactNumber.trim().isEmpty()) contactNumber = customer.getContactNumber();
        
        boolean success = bankingService.updateCustomer(customerId, name, age, email, contactNumber);
        if (success) {
            System.out.println("✅ Customer updated successfully!");
        }
    }

    private static void deleteCustomer() {
        System.out.println("\n🗑️  DELETE CUSTOMER");
        System.out.println("==================");
        
        System.out.print("Enter customer ID: ");
        int customerId = getValidInteger();
        
        boolean success = bankingService.deleteCustomer(customerId);
        if (success) {
            System.out.println("✅ Customer deleted successfully!");
        }
    }

    // ==================== ACCOUNT OPERATIONS ====================

    private static void createNewAccount() {
        System.out.println("\n➕ CREATE NEW ACCOUNT");
        System.out.println("====================");
        
        System.out.print("Enter customer ID: ");
        int customerId = getValidInteger();
        
        System.out.println("Select account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        System.out.println("3. Fixed Deposit Account");
        int typeChoice = getValidChoice(1, 3);
        
        String accountType;
        switch (typeChoice) {
            case 1: accountType = Account.SAVINGS; break;
            case 2: accountType = Account.CURRENT; break;
            case 3: accountType = Account.FIXED_DEPOSIT; break;
            default: accountType = Account.SAVINGS;
        }
        
        System.out.print("Enter initial balance: $");
        BigDecimal initialBalance = getValidBigDecimal();
        
        boolean success = bankingService.createAccount(customerId, accountType, initialBalance);
        if (success) {
            System.out.println("✅ Account created successfully!");
        }
    }

    private static void findAccountById() {
        System.out.println("\n🔍 FIND ACCOUNT BY ID");
        System.out.println("=====================");
        
        System.out.print("Enter account ID: ");
        int accountId = getValidInteger();
        
        var account = bankingService.getAccount(accountId);
        if (account.isPresent()) {
            displayAccount(account.get());
        } else {
            System.out.println("❌ Account not found!");
        }
    }

    private static void viewCustomerAccounts() {
        System.out.println("\n👤 VIEW CUSTOMER ACCOUNTS");
        System.out.println("=========================");
        
        System.out.print("Enter customer ID: ");
        int customerId = getValidInteger();
        
        List<Account> accounts = bankingService.getCustomerAccounts(customerId);
        if (accounts.isEmpty()) {
            System.out.println("❌ No accounts found for this customer!");
        } else {
            System.out.println("✅ Found " + accounts.size() + " account(s):");
            accounts.forEach(Main::displayAccount);
        }
    }

    private static void viewAllAccounts() {
        System.out.println("\n📋 ALL ACCOUNTS");
        System.out.println("===============");
        
        List<Account> accounts = bankingService.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("❌ No accounts found!");
        } else {
            System.out.println("✅ Total accounts: " + accounts.size());
            accounts.forEach(Main::displayAccount);
        }
    }

    private static void checkBalance() {
        System.out.println("\n💰 CHECK BALANCE");
        System.out.println("================");
        
        System.out.print("Enter account ID: ");
        int accountId = getValidInteger();
        
        var balance = bankingService.checkBalance(accountId);
        if (balance.isEmpty()) {
            System.out.println("❌ Account not found!");
        }
    }

    private static void deleteAccount() {
        System.out.println("\n🗑️  DELETE ACCOUNT");
        System.out.println("==================");
        
        System.out.print("Enter account ID: ");
        int accountId = getValidInteger();
        
        // This would need to be implemented in the service layer
        System.out.println("❌ Account deletion not implemented for safety reasons.");
    }

    // ==================== TRANSACTION OPERATIONS ====================

    private static void depositMoney() {
        System.out.println("\n💵 DEPOSIT MONEY");
        System.out.println("================");
        
        System.out.print("Enter account ID: ");
        int accountId = getValidInteger();
        
        System.out.print("Enter amount to deposit: $");
        BigDecimal amount = getValidBigDecimal();
        
        boolean success = bankingService.deposit(accountId, amount);
        if (success) {
            System.out.println("✅ Deposit successful!");
        }
    }

    private static void withdrawMoney() {
        System.out.println("\n💸 WITHDRAW MONEY");
        System.out.println("=================");
        
        System.out.print("Enter account ID: ");
        int accountId = getValidInteger();
        
        System.out.print("Enter amount to withdraw: $");
        BigDecimal amount = getValidBigDecimal();
        
        boolean success = bankingService.withdraw(accountId, amount);
        if (success) {
            System.out.println("✅ Withdrawal successful!");
        }
    }

    private static void transferMoney() {
        System.out.println("\n🔄 TRANSFER MONEY");
        System.out.println("=================");
        
        System.out.print("Enter source account ID: ");
        int fromAccountId = getValidInteger();
        
        System.out.print("Enter destination account ID: ");
        int toAccountId = getValidInteger();
        
        System.out.print("Enter amount to transfer: $");
        BigDecimal amount = getValidBigDecimal();
        
        boolean success = bankingService.transfer(fromAccountId, toAccountId, amount);
        if (success) {
            System.out.println("✅ Transfer successful!");
        }
    }

    // ==================== REPORT OPERATIONS ====================

    private static void accountTransactionHistory() {
        System.out.println("\n📝 ACCOUNT TRANSACTION HISTORY");
        System.out.println("=============================");
        
        System.out.print("Enter account ID: ");
        int accountId = getValidInteger();
        
        List<Transaction> transactions = bankingService.getAccountTransactionHistory(accountId);
        if (transactions.isEmpty()) {
            System.out.println("❌ No transactions found for this account!");
        } else {
            System.out.println("✅ Found " + transactions.size() + " transaction(s):");
            transactions.forEach(Main::displayTransaction);
        }
    }

    private static void accountTransactionSummary() {
        System.out.println("\n📊 ACCOUNT TRANSACTION SUMMARY");
        System.out.println("==============================");
        
        System.out.print("Enter account ID: ");
        int accountId = getValidInteger();
        
        var summary = bankingService.getAccountTransactionSummary(accountId);
        System.out.println("📈 Transaction Summary for Account " + accountId + ":");
        System.out.println("   Total Transactions: " + summary.getTotalTransactions());
        System.out.println("   Total Deposits: $" + summary.getTotalDeposits());
        System.out.println("   Total Withdrawals: $" + summary.getTotalWithdrawals());
        System.out.println("   Total Sent: $" + summary.getTotalSent());
        System.out.println("   Total Received: $" + summary.getTotalReceived());
    }

    private static void viewAllTransactions() {
        System.out.println("\n📋 ALL TRANSACTIONS");
        System.out.println("===================");
        
        List<Transaction> transactions = bankingService.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("❌ No transactions found!");
        } else {
            System.out.println("✅ Total transactions: " + transactions.size());
            transactions.forEach(Main::displayTransaction);
        }
    }

    private static void viewRecentTransactions() {
        System.out.println("\n🕒 RECENT TRANSACTIONS");
        System.out.println("=====================");
        
        System.out.print("Enter number of recent transactions to view (1-100): ");
        int limit = getValidChoice(1, 100);
        
        List<Transaction> transactions = bankingService.getRecentTransactions(limit);
        if (transactions.isEmpty()) {
            System.out.println("❌ No transactions found!");
        } else {
            System.out.println("✅ Recent " + transactions.size() + " transaction(s):");
            transactions.forEach(Main::displayTransaction);
        }
    }

    private static void viewCustomerDetails() {
        System.out.println("\n👤 CUSTOMER DETAILS WITH ACCOUNTS");
        System.out.println("=================================");
        
        System.out.print("Enter customer ID: ");
        int customerId = getValidInteger();
        
        bankingService.displayCustomerDetails(customerId);
    }

    // ==================== SYSTEM OPERATIONS ====================

    private static void systemStatistics() {
        bankingService.displaySystemStatistics();
    }

    private static void deleteAllData() {
        System.out.println("\n⚠️  DANGER: DELETE ALL DATA");
        System.out.println("==========================");
        System.out.println("This will permanently delete ALL customers, accounts, and transactions!");
        System.out.print("Type 'DELETE' to confirm: ");
        String confirmation = scanner.nextLine();
        
        if ("DELETE".equals(confirmation)) {
            System.out.println("❌ Data deletion not implemented for safety reasons.");
            System.out.println("   Use database management tools to clear data if needed.");
        } else {
            System.out.println("✅ Operation cancelled.");
        }
    }

    // ==================== UTILITY METHODS ====================

    private static boolean testDatabaseConnection() {
        try {
            return DatabaseConnection.getInstance().testConnection();
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    private static int getValidChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("❌ Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("❌ Please enter a valid number: ");
            }
        }
    }

    private static int getValidInteger() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("❌ Please enter a valid number: ");
            }
        }
    }

    private static BigDecimal getValidBigDecimal() {
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("❌ Please enter a valid amount: ");
            }
        }
    }

    private static void displayCustomer(Customer customer) {
        System.out.println("👤 Customer ID: " + customer.getId());
        System.out.println("   Name: " + customer.getName());
        System.out.println("   Age: " + customer.getAge());
        System.out.println("   Email: " + customer.getEmail());
        System.out.println("   Contact: " + customer.getContactNumber());
        System.out.println("---");
    }

    private static void displayAccount(Account account) {
        System.out.println("💳 Account ID: " + account.getId());
        System.out.println("   Customer ID: " + account.getCustomerId());
        System.out.println("   Type: " + account.getAccountType());
        System.out.println("   Balance: $" + account.getBalance());
        System.out.println("   Created: " + account.getCreatedAt());
        System.out.println("---");
    }

    private static void displayTransaction(Transaction transaction) {
        System.out.println("📝 Transaction ID: " + transaction.getId());
        System.out.println("   Type: " + transaction.getType());
        System.out.println("   Amount: $" + transaction.getAmount());
        if (transaction.getFromAccountId() != null) {
            System.out.println("   From Account: " + transaction.getFromAccountId());
        }
        if (transaction.getToAccountId() != null) {
            System.out.println("   To Account: " + transaction.getToAccountId());
        }
        System.out.println("   Timestamp: " + transaction.getTimestamp());
        System.out.println("---");
    }
} 