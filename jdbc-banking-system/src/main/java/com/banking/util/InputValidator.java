package com.banking.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;
import com.banking.model.Account;
import com.banking.model.Transaction;

/**
 * Input validation utility class
 * Demonstrates utility methods and input validation best practices
 */
public class InputValidator {
    
    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s]{2,100}$"
    );

    // Validation methods
    public static boolean isValidName(String name) {
        return name != null && 
               !name.trim().isEmpty() && 
               NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidAge(int age) {
        return age >= 18 && age <= 120;
    }

    public static boolean isValidEmail(String email) {
        return email != null && 
               !email.trim().isEmpty() && 
               EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidContactNumber(String contactNumber) {
        return contactNumber != null && 
               !contactNumber.trim().isEmpty() && 
               PHONE_PATTERN.matcher(contactNumber.trim()).matches();
    }

    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && 
               amount.compareTo(BigDecimal.ZERO) > 0 && 
               amount.compareTo(new BigDecimal("999999999.99")) <= 0;
    }

    public static boolean isValidAccountId(int accountId) {
        return accountId > 0;
    }

    public static boolean isValidCustomerId(int customerId) {
        return customerId > 0;
    }

    public static boolean isValidAccountType(String accountType) {
        return accountType != null && 
               (accountType.equals(Account.SAVINGS) || 
                accountType.equals(Account.CURRENT) || 
                accountType.equals(Account.FIXED_DEPOSIT));
    }

    public static boolean isValidTransactionType(String transactionType) {
        return transactionType != null && 
               (transactionType.equals(Transaction.DEPOSIT) || 
                transactionType.equals(Transaction.WITHDRAW) || 
                transactionType.equals(Transaction.TRANSFER));
    }

    // Input sanitization methods
    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("[<>\"']", "");
    }

    public static String sanitizeName(String name) {
        String sanitized = sanitizeString(name);
        if (sanitized != null) {
            // Capitalize first letter of each word
            String[] words = sanitized.split("\\s+");
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) result.append(" ");
                if (words[i].length() > 0) {
                    result.append(words[i].substring(0, 1).toUpperCase())
                          .append(words[i].substring(1).toLowerCase());
                }
            }
            return result.toString();
        }
        return null;
    }

    public static String sanitizeEmail(String email) {
        String sanitized = sanitizeString(email);
        return sanitized != null ? sanitized.toLowerCase() : null;
    }

    public static String sanitizeContactNumber(String contactNumber) {
        String sanitized = sanitizeString(contactNumber);
        if (sanitized != null) {
            // Remove all non-digit characters except +
            return sanitized.replaceAll("[^0-9+]", "");
        }
        return null;
    }

    // Validation with error messages
    public static ValidationResult validateCustomer(String name, int age, String email, String contactNumber) {
        ValidationResult result = new ValidationResult();
        
        if (!isValidName(name)) {
            result.addError("Name must be 2-100 characters long and contain only letters and spaces");
        }
        
        if (!isValidAge(age)) {
            result.addError("Age must be between 18 and 120 years");
        }
        
        if (!isValidEmail(email)) {
            result.addError("Please enter a valid email address");
        }
        
        if (!isValidContactNumber(contactNumber)) {
            result.addError("Please enter a valid contact number (10-15 digits)");
        }
        
        return result;
    }

    public static ValidationResult validateAccount(int customerId, String accountType, BigDecimal balance) {
        ValidationResult result = new ValidationResult();
        
        if (!isValidCustomerId(customerId)) {
            result.addError("Invalid customer ID");
        }
        
        if (!isValidAccountType(accountType)) {
            result.addError("Invalid account type. Must be SAVINGS, CURRENT, or FIXED_DEPOSIT");
        }
        
        if (!isValidAmount(balance)) {
            result.addError("Invalid amount. Must be greater than 0 and less than 1 billion");
        }
        
        return result;
    }

    public static ValidationResult validateTransaction(Integer fromAccountId, Integer toAccountId, BigDecimal amount, String type) {
        ValidationResult result = new ValidationResult();
        
        if (!isValidTransactionType(type)) {
            result.addError("Invalid transaction type");
        }
        
        if (!isValidAmount(amount)) {
            result.addError("Invalid amount. Must be greater than 0 and less than 1 billion");
        }
        
        switch (type) {
            case Transaction.DEPOSIT:
                if (!isValidAccountId(toAccountId)) {
                    result.addError("Invalid destination account ID");
                }
                break;
            case Transaction.WITHDRAW:
                if (!isValidAccountId(fromAccountId)) {
                    result.addError("Invalid source account ID");
                }
                break;
            case Transaction.TRANSFER:
                if (!isValidAccountId(fromAccountId)) {
                    result.addError("Invalid source account ID");
                }
                if (!isValidAccountId(toAccountId)) {
                    result.addError("Invalid destination account ID");
                }
                if (fromAccountId != null && toAccountId != null && fromAccountId.equals(toAccountId)) {
                    result.addError("Source and destination accounts cannot be the same");
                }
                break;
        }
        
        return result;
    }

    // Validation result class
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();

        public void addError(String error) {
            valid = false;
            if (errors.length() > 0) {
                errors.append("\n");
            }
            errors.append(error);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrors() {
            return errors.toString();
        }

        public void printErrors() {
            if (!valid) {
                System.err.println("Validation errors:");
                System.err.println(errors.toString());
            }
        }
    }
} 