// Global variables
let currentSection = 'dashboard';
let customers = [];
let accounts = [];
let transactions = [];

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
    loadDashboardData();
});

// Initialize the application
function initializeApp() {
    // Load sample data for demonstration
    loadSampleData();
    
    // Show dashboard by default
    showSection('dashboard');
    
    // Update header stats
    updateHeaderStats();
}

// Setup event listeners
function setupEventListeners() {
    // Navigation buttons
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const section = this.getAttribute('data-section');
            showSection(section);
        });
    });

    // Form submissions
    document.getElementById('addCustomerForm').addEventListener('submit', handleAddCustomer);
    document.getElementById('addAccountForm').addEventListener('submit', handleAddAccount);
    document.getElementById('depositForm').addEventListener('submit', handleDeposit);
    document.getElementById('withdrawForm').addEventListener('submit', handleWithdraw);
    document.getElementById('transferForm').addEventListener('submit', handleTransfer);

    // Search functionality
    document.getElementById('customerSearch').addEventListener('input', debounce(searchCustomers, 300));
    document.getElementById('accountSearch').addEventListener('input', debounce(searchAccounts, 300));

    // Modal close on outside click
    window.addEventListener('click', function(event) {
        if (event.target.classList.contains('modal')) {
            event.target.style.display = 'none';
        }
    });
}

// Navigation functions
function showSection(sectionName) {
    // Hide all sections
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });

    // Remove active class from all nav buttons
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    // Show selected section
    document.getElementById(sectionName).classList.add('active');

    // Add active class to corresponding nav button
    document.querySelector(`[data-section="${sectionName}"]`).classList.add('active');

    currentSection = sectionName;

    // Load section-specific data
    switch(sectionName) {
        case 'customers':
            loadCustomers();
            break;
        case 'accounts':
            loadAccounts();
            break;
        case 'transactions':
            loadTransactions();
            break;
        case 'reports':
            loadReports();
            break;
        case 'admin':
            loadAdminData();
            break;
        case 'dashboard':
            loadDashboardData();
            break;
    }
}

// Modal functions
function showModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
    // Reset form
    document.querySelector(`#${modalId} form`).reset();
}

// Data loading functions
function loadSampleData() {
    // Sample customers
    customers = [
        { id: 1, name: 'John Doe', age: 28, email: 'john.doe@email.com', contact: '+1-555-0123' },
        { id: 2, name: 'Jane Smith', age: 32, email: 'jane.smith@email.com', contact: '+1-555-0124' },
        { id: 3, name: 'Mike Johnson', age: 25, email: 'mike.johnson@email.com', contact: '+1-555-0125' }
    ];

    // Sample accounts
    accounts = [
        { id: 1, customerId: 1, customerName: 'John Doe', type: 'SAVINGS', balance: 5000.00, createdAt: '2024-01-15' },
        { id: 2, customerId: 1, customerName: 'John Doe', type: 'CURRENT', balance: 2500.00, createdAt: '2024-02-01' },
        { id: 3, customerId: 2, customerName: 'Jane Smith', type: 'SAVINGS', balance: 8000.00, createdAt: '2024-01-20' },
        { id: 4, customerId: 3, customerName: 'Mike Johnson', type: 'FIXED_DEPOSIT', balance: 15000.00, createdAt: '2024-03-01' }
    ];

    // Sample transactions
    transactions = [
        { id: 1, type: 'DEPOSIT', fromAccount: null, toAccount: 1, amount: 5000.00, timestamp: '2024-01-15 10:30:00' },
        { id: 2, type: 'DEPOSIT', fromAccount: null, toAccount: 2, amount: 2500.00, timestamp: '2024-02-01 14:20:00' },
        { id: 3, type: 'WITHDRAW', fromAccount: 1, toAccount: null, amount: 500.00, timestamp: '2024-02-15 09:15:00' },
        { id: 4, type: 'TRANSFER', fromAccount: 1, toAccount: 3, amount: 1000.00, timestamp: '2024-03-01 16:45:00' }
    ];
}

function loadDashboardData() {
    updateDashboardStats();
    updateRecentActivity();
}

function loadCustomers() {
    const tbody = document.getElementById('customersTableBody');
    tbody.innerHTML = '';

    customers.forEach(customer => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${customer.id}</td>
            <td>${customer.name}</td>
            <td>${customer.age}</td>
            <td>${customer.email}</td>
            <td>${customer.contact}</td>
            <td>
                <button class="btn btn-secondary" onclick="editCustomer(${customer.id})">
                    <i class="fas fa-edit"></i> Edit
                </button>
                <button class="btn btn-danger" onclick="deleteCustomer(${customer.id})">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function loadAccounts() {
    const tbody = document.getElementById('accountsTableBody');
    tbody.innerHTML = '';

    accounts.forEach(account => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${account.id}</td>
            <td>${account.customerName}</td>
            <td><span class="badge badge-${getAccountTypeClass(account.type)}">${account.type}</span></td>
            <td>$${account.balance.toFixed(2)}</td>
            <td>${formatDate(account.createdAt)}</td>
            <td>
                <button class="btn btn-info" onclick="viewAccountDetails(${account.id})">
                    <i class="fas fa-eye"></i> View
                </button>
                <button class="btn btn-danger" onclick="deleteAccount(${account.id})">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function loadTransactions() {
    const tbody = document.getElementById('transactionsTableBody');
    tbody.innerHTML = '';

    transactions.slice(0, 10).forEach(transaction => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${transaction.id}</td>
            <td><span class="badge badge-${getTransactionTypeClass(transaction.type)}">${transaction.type}</span></td>
            <td>${transaction.fromAccount || '-'}</td>
            <td>${transaction.toAccount || '-'}</td>
            <td>$${transaction.amount.toFixed(2)}</td>
            <td>${formatDateTime(transaction.timestamp)}</td>
        `;
        tbody.appendChild(row);
    });
}

function loadReports() {
    updateSystemStats();
}

function loadAdminData() {
    document.getElementById('adminCustomerCount').textContent = customers.length;
    document.getElementById('adminAccountCount').textContent = accounts.length;
    document.getElementById('adminTransactionCount').textContent = transactions.length;
}

// Form handlers
function handleAddCustomer(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const customer = {
        id: customers.length + 1,
        name: formData.get('customerName') || document.getElementById('customerName').value,
        age: parseInt(formData.get('customerAge') || document.getElementById('customerAge').value),
        email: formData.get('customerEmail') || document.getElementById('customerEmail').value,
        contact: formData.get('customerContact') || document.getElementById('customerContact').value
    };

    customers.push(customer);
    closeModal('addCustomerModal');
    showNotification('Customer added successfully!', 'success');
    
    if (currentSection === 'customers') {
        loadCustomers();
    }
    updateHeaderStats();
    updateDashboardStats();
}

function handleAddAccount(event) {
    event.preventDefault();
    
    const customerId = parseInt(document.getElementById('accountCustomerId').value);
    const customer = customers.find(c => c.id === customerId);
    
    if (!customer) {
        showNotification('Customer not found!', 'error');
        return;
    }

    const account = {
        id: accounts.length + 1,
        customerId: customerId,
        customerName: customer.name,
        type: document.getElementById('accountType').value,
        balance: parseFloat(document.getElementById('accountBalance').value),
        createdAt: new Date().toISOString().split('T')[0]
    };

    accounts.push(account);
    closeModal('addAccountModal');
    showNotification('Account created successfully!', 'success');
    
    if (currentSection === 'accounts') {
        loadAccounts();
    }
    updateHeaderStats();
    updateDashboardStats();
}

function handleDeposit(event) {
    event.preventDefault();
    
    const accountId = parseInt(document.getElementById('depositAccountId').value);
    const amount = parseFloat(document.getElementById('depositAmount').value);
    
    const account = accounts.find(a => a.id === accountId);
    if (!account) {
        showNotification('Account not found!', 'error');
        return;
    }

    account.balance += amount;
    
    const transaction = {
        id: transactions.length + 1,
        type: 'DEPOSIT',
        fromAccount: null,
        toAccount: accountId,
        amount: amount,
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19)
    };

    transactions.unshift(transaction);
    
    event.target.reset();
    showNotification(`$${amount.toFixed(2)} deposited successfully!`, 'success');
    
    if (currentSection === 'transactions') {
        loadTransactions();
    }
    updateHeaderStats();
    updateDashboardStats();
    updateRecentActivity();
}

function handleWithdraw(event) {
    event.preventDefault();
    
    const accountId = parseInt(document.getElementById('withdrawAccountId').value);
    const amount = parseFloat(document.getElementById('withdrawAmount').value);
    
    const account = accounts.find(a => a.id === accountId);
    if (!account) {
        showNotification('Account not found!', 'error');
        return;
    }

    if (account.balance < amount) {
        showNotification('Insufficient balance!', 'error');
        return;
    }

    account.balance -= amount;
    
    const transaction = {
        id: transactions.length + 1,
        type: 'WITHDRAW',
        fromAccount: accountId,
        toAccount: null,
        amount: amount,
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19)
    };

    transactions.unshift(transaction);
    
    event.target.reset();
    showNotification(`$${amount.toFixed(2)} withdrawn successfully!`, 'success');
    
    if (currentSection === 'transactions') {
        loadTransactions();
    }
    updateHeaderStats();
    updateDashboardStats();
    updateRecentActivity();
}

function handleTransfer(event) {
    event.preventDefault();
    
    const fromAccountId = parseInt(document.getElementById('transferFromId').value);
    const toAccountId = parseInt(document.getElementById('transferToId').value);
    const amount = parseFloat(document.getElementById('transferAmount').value);
    
    const fromAccount = accounts.find(a => a.id === fromAccountId);
    const toAccount = accounts.find(a => a.id === toAccountId);
    
    if (!fromAccount || !toAccount) {
        showNotification('One or both accounts not found!', 'error');
        return;
    }

    if (fromAccount.balance < amount) {
        showNotification('Insufficient balance in source account!', 'error');
        return;
    }

    fromAccount.balance -= amount;
    toAccount.balance += amount;
    
    const transaction = {
        id: transactions.length + 1,
        type: 'TRANSFER',
        fromAccount: fromAccountId,
        toAccount: toAccountId,
        amount: amount,
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19)
    };

    transactions.unshift(transaction);
    
    event.target.reset();
    showNotification(`$${amount.toFixed(2)} transferred successfully!`, 'success');
    
    if (currentSection === 'transactions') {
        loadTransactions();
    }
    updateHeaderStats();
    updateDashboardStats();
    updateRecentActivity();
}

// Search functions
function searchCustomers() {
    const searchTerm = document.getElementById('customerSearch').value.toLowerCase();
    const filteredCustomers = customers.filter(customer => 
        customer.name.toLowerCase().includes(searchTerm) ||
        customer.email.toLowerCase().includes(searchTerm)
    );
    
    const tbody = document.getElementById('customersTableBody');
    tbody.innerHTML = '';

    filteredCustomers.forEach(customer => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${customer.id}</td>
            <td>${customer.name}</td>
            <td>${customer.age}</td>
            <td>${customer.email}</td>
            <td>${customer.contact}</td>
            <td>
                <button class="btn btn-secondary" onclick="editCustomer(${customer.id})">
                    <i class="fas fa-edit"></i> Edit
                </button>
                <button class="btn btn-danger" onclick="deleteCustomer(${customer.id})">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function searchAccounts() {
    const searchTerm = document.getElementById('accountSearch').value.toLowerCase();
    const filteredAccounts = accounts.filter(account => 
        account.customerName.toLowerCase().includes(searchTerm)
    );
    
    const tbody = document.getElementById('accountsTableBody');
    tbody.innerHTML = '';

    filteredAccounts.forEach(account => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${account.id}</td>
            <td>${account.customerName}</td>
            <td><span class="badge badge-${getAccountTypeClass(account.type)}">${account.type}</span></td>
            <td>$${account.balance.toFixed(2)}</td>
            <td>${formatDate(account.createdAt)}</td>
            <td>
                <button class="btn btn-info" onclick="viewAccountDetails(${account.id})">
                    <i class="fas fa-eye"></i> View
                </button>
                <button class="btn btn-danger" onclick="deleteAccount(${account.id})">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Update functions
function updateHeaderStats() {
    document.getElementById('customerCount').textContent = customers.length;
    document.getElementById('accountCount').textContent = accounts.length;
    document.getElementById('transactionCount').textContent = transactions.length;
}

function updateDashboardStats() {
    document.getElementById('totalCustomers').textContent = customers.length;
    document.getElementById('totalAccounts').textContent = accounts.length;
    document.getElementById('totalTransactions').textContent = transactions.length;
    
    const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0);
    document.getElementById('totalBalance').textContent = `$${totalBalance.toFixed(2)}`;
}

function updateRecentActivity() {
    const recentActivity = document.getElementById('recentActivity');
    recentActivity.innerHTML = '';

    const recentTransactions = transactions.slice(0, 5);
    
    if (recentTransactions.length === 0) {
        recentActivity.innerHTML = `
            <div class="activity-item">
                <i class="fas fa-info-circle"></i>
                <span>No recent activity</span>
            </div>
        `;
        return;
    }

    recentTransactions.forEach(transaction => {
        const activityItem = document.createElement('div');
        activityItem.className = 'activity-item';
        
        let icon, description;
        switch(transaction.type) {
            case 'DEPOSIT':
                icon = 'fas fa-plus-circle';
                description = `$${transaction.amount.toFixed(2)} deposited to account ${transaction.toAccount}`;
                break;
            case 'WITHDRAW':
                icon = 'fas fa-minus-circle';
                description = `$${transaction.amount.toFixed(2)} withdrawn from account ${transaction.fromAccount}`;
                break;
            case 'TRANSFER':
                icon = 'fas fa-exchange-alt';
                description = `$${transaction.amount.toFixed(2)} transferred from account ${transaction.fromAccount} to ${transaction.toAccount}`;
                break;
        }
        
        activityItem.innerHTML = `
            <i class="${icon}"></i>
            <span>${description}</span>
        `;
        recentActivity.appendChild(activityItem);
    });
}

function updateSystemStats() {
    const systemStats = document.getElementById('systemStats');
    
    const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0);
    const avgBalance = accounts.length > 0 ? totalBalance / accounts.length : 0;
    const totalDeposits = transactions.filter(t => t.type === 'DEPOSIT').reduce((sum, t) => sum + t.amount, 0);
    const totalWithdrawals = transactions.filter(t => t.type === 'WITHDRAW').reduce((sum, t) => sum + t.amount, 0);
    
    systemStats.innerHTML = `
        <div class="admin-stat">
            <span class="stat-label">Total Balance:</span>
            <span class="stat-value">$${totalBalance.toFixed(2)}</span>
        </div>
        <div class="admin-stat">
            <span class="stat-label">Average Balance:</span>
            <span class="stat-value">$${avgBalance.toFixed(2)}</span>
        </div>
        <div class="admin-stat">
            <span class="stat-label">Total Deposits:</span>
            <span class="stat-value">$${totalDeposits.toFixed(2)}</span>
        </div>
        <div class="admin-stat">
            <span class="stat-label">Total Withdrawals:</span>
            <span class="stat-value">$${totalWithdrawals.toFixed(2)}</span>
        </div>
    `;
}

// Utility functions
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type} show`;
    
    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString();
}

function formatDateTime(dateTimeString) {
    return new Date(dateTimeString).toLocaleString();
}

function getAccountTypeClass(type) {
    switch(type) {
        case 'SAVINGS': return 'success';
        case 'CURRENT': return 'primary';
        case 'FIXED_DEPOSIT': return 'warning';
        default: return 'secondary';
    }
}

function getTransactionTypeClass(type) {
    switch(type) {
        case 'DEPOSIT': return 'success';
        case 'WITHDRAW': return 'warning';
        case 'TRANSFER': return 'info';
        default: return 'secondary';
    }
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Admin functions
function clearAllData() {
    if (confirm('Are you sure you want to clear all data? This action cannot be undone.')) {
        customers = [];
        accounts = [];
        transactions = [];
        
        updateHeaderStats();
        updateDashboardStats();
        updateRecentActivity();
        
        if (currentSection === 'customers') loadCustomers();
        if (currentSection === 'accounts') loadAccounts();
        if (currentSection === 'transactions') loadTransactions();
        if (currentSection === 'admin') loadAdminData();
        
        showNotification('All data cleared successfully!', 'success');
    }
}

function exportData() {
    const data = {
        customers: customers,
        accounts: accounts,
        transactions: transactions,
        exportDate: new Date().toISOString()
    };
    
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `banking-system-data-${new Date().toISOString().split('T')[0]}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    
    showNotification('Data exported successfully!', 'success');
}

// Report functions
function getTransactionHistory() {
    const accountId = parseInt(document.getElementById('reportAccountId').value);
    const account = accounts.find(a => a.id === accountId);
    
    if (!account) {
        showNotification('Account not found!', 'error');
        return;
    }
    
    const accountTransactions = transactions.filter(t => 
        t.fromAccount === accountId || t.toAccount === accountId
    );
    
    const historyDiv = document.getElementById('transactionHistory');
    
    if (accountTransactions.length === 0) {
        historyDiv.innerHTML = '<p>No transactions found for this account.</p>';
        return;
    }
    
    let html = `
        <h4>Transaction History for Account ${accountId}</h4>
        <p><strong>Current Balance:</strong> $${account.balance.toFixed(2)}</p>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Amount</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
    `;
    
    accountTransactions.forEach(transaction => {
        let description;
        if (transaction.type === 'DEPOSIT') {
            description = 'Deposit to account';
        } else if (transaction.type === 'WITHDRAW') {
            description = 'Withdrawal from account';
        } else if (transaction.type === 'TRANSFER') {
            if (transaction.fromAccount === accountId) {
                description = `Transfer to account ${transaction.toAccount}`;
            } else {
                description = `Transfer from account ${transaction.fromAccount}`;
            }
        }
        
        html += `
            <tr>
                <td>${formatDateTime(transaction.timestamp)}</td>
                <td><span class="badge badge-${getTransactionTypeClass(transaction.type)}">${transaction.type}</span></td>
                <td>$${transaction.amount.toFixed(2)}</td>
                <td>${description}</td>
            </tr>
        `;
    });
    
    html += '</tbody></table>';
    historyDiv.innerHTML = html;
}

// Placeholder functions for future implementation
function editCustomer(customerId) {
    showNotification('Edit functionality will be implemented soon!', 'info');
}

function deleteCustomer(customerId) {
    if (confirm('Are you sure you want to delete this customer?')) {
        customers = customers.filter(c => c.id !== customerId);
        accounts = accounts.filter(a => a.customerId !== customerId);
        
        if (currentSection === 'customers') loadCustomers();
        if (currentSection === 'accounts') loadAccounts();
        
        updateHeaderStats();
        updateDashboardStats();
        showNotification('Customer deleted successfully!', 'success');
    }
}

function viewAccountDetails(accountId) {
    showNotification('Account details view will be implemented soon!', 'info');
}

function deleteAccount(accountId) {
    if (confirm('Are you sure you want to delete this account?')) {
        accounts = accounts.filter(a => a.id !== accountId);
        
        if (currentSection === 'accounts') loadAccounts();
        
        updateHeaderStats();
        updateDashboardStats();
        showNotification('Account deleted successfully!', 'success');
    }
}

// Add CSS for badges
const style = document.createElement('style');
style.textContent = `
    .badge {
        padding: 0.25rem 0.5rem;
        border-radius: 4px;
        font-size: 0.75rem;
        font-weight: 500;
        text-transform: uppercase;
    }
    .badge-success { background: #27ae60; color: white; }
    .badge-primary { background: #3498db; color: white; }
    .badge-warning { background: #f39c12; color: white; }
    .badge-info { background: #17a2b8; color: white; }
    .badge-secondary { background: #95a5a6; color: white; }
`;
document.head.appendChild(style); 