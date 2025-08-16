package com.banking.model;

import java.util.Objects;

/**
 * Customer model class representing a bank customer
 * Demonstrates encapsulation, validation, and clean OOP principles
 */
public class Customer {
    private int id;
    private String name;
    private int age;
    private String email;
    private String contactNumber;

    // Default constructor
    public Customer() {}

    // Parameterized constructor
    public Customer(String name, int age, String email, String contactNumber) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.contactNumber = contactNumber;
    }

    // Full constructor with ID
    public Customer(int id, String name, int age, String email, String contactNumber) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.contactNumber = contactNumber;
    }

    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    // Validation methods
    public boolean isValidAge() {
        return age >= 18 && age <= 120;
    }

    public boolean isValidEmail() {
        return email != null && email.contains("@") && email.contains(".");
    }

    public boolean isValidContactNumber() {
        return contactNumber != null && contactNumber.length() >= 10;
    }

    public boolean isValidName() {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }

    // Override methods for proper object behavior
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return id == customer.id && 
               age == customer.age && 
               Objects.equals(name, customer.name) && 
               Objects.equals(email, customer.email) && 
               Objects.equals(contactNumber, customer.contactNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, email, contactNumber);
    }

    @Override
    public String toString() {
        return String.format("Customer{id=%d, name='%s', age=%d, email='%s', contactNumber='%s'}", 
                           id, name, age, email, contactNumber);
    }
} 