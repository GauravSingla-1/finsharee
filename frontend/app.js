// FinShare Frontend Application
// API Configuration
const API_BASE_URL = window.location.origin;
const API_ENDPOINTS = {
    gateway: `${API_BASE_URL}`,
    ai: `${API_BASE_URL}/api/ai`,
    groups: `${API_BASE_URL}/api/groups`,
    expenses: `${API_BASE_URL}/api/expenses`,
    analytics: `${API_BASE_URL}/api/analytics`,
    settlements: `${API_BASE_URL}/api/settlements`,
    notifications: `${API_BASE_URL}/api/notifications`
};

// Global state
let currentUser = null;
let chatHistory = [];

// Utility Functions
function showLoading() {
    document.getElementById('loading').classList.remove('hidden');
}

function hideLoading() {
    document.getElementById('loading').classList.add('hidden');
}

function showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'fixed top-4 right-4 bg-red-500 text-white px-4 py-2 rounded-lg z-50';
    errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle mr-2"></i>${message}`;
    document.body.appendChild(errorDiv);
    setTimeout(() => errorDiv.remove(), 5000);
}

function showSuccess(message) {
    const successDiv = document.createElement('div');
    successDiv.className = 'fixed top-4 right-4 bg-green-500 text-white px-4 py-2 rounded-lg z-50';
    successDiv.innerHTML = `<i class="fas fa-check mr-2"></i>${message}`;
    document.body.appendChild(successDiv);
    setTimeout(() => successDiv.remove(), 3000);
}

// API Helper
async function apiCall(url, options = {}) {
    showLoading();
    try {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                'X-Authenticated-User-ID': currentUser?.id || 'test-user-123'
            }
        };
        
        const response = await fetch(url, {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...(options.headers || {})
            }
        });
        
        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(`API Error: ${response.status} - ${errorData}`);
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        return await response.text();
    } catch (error) {
        console.error('API call failed:', error);
        showError(error.message);
        throw error;
    } finally {
        hideLoading();
    }
}

// Navigation Functions
function showSection(sectionName) {
    // Hide all sections
    const sections = ['welcome', 'groups', 'expenses', 'ai', 'analytics', 'settlements', 'notifications'];
    sections.forEach(section => {
        const element = document.getElementById(`${section}-section`);
        if (element) element.classList.add('hidden');
    });
    
    // Show selected section
    const targetSection = document.getElementById(`${sectionName}-section`);
    if (targetSection) {
        targetSection.classList.remove('hidden');
        
        // Load data for the section
        switch(sectionName) {
            case 'groups':
                loadGroups();
                break;
            case 'expenses':
                loadExpenses();
                break;
            case 'analytics':
                loadBudgets();
                break;
            case 'settlements':
                loadSettlements();
                break;
            case 'notifications':
                loadNotifications();
                break;
        }
    }
}

// Authentication
function login() {
    // Simulate login for demo purposes
    currentUser = {
        id: 'test-user-123',
        name: 'Demo User',
        email: 'demo@finshare.com'
    };
    
    document.getElementById('userStatus').textContent = `Welcome, ${currentUser.name}`;
    document.getElementById('loginBtn').textContent = 'Logout';
    document.getElementById('loginBtn').onclick = logout;
    
    showSuccess('Logged in successfully');
}

function logout() {
    currentUser = null;
    document.getElementById('userStatus').textContent = 'Not Authenticated';
    document.getElementById('loginBtn').textContent = 'Login';
    document.getElementById('loginBtn').onclick = login;
    showSection('welcome');
}

// Groups Management
async function loadGroups() {
    try {
        const groups = await apiCall(`${API_ENDPOINTS.groups}`);
        displayGroups(groups);
    } catch (error) {
        document.getElementById('groups-list').innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No groups found or unable to load groups</p>
                <button onclick="createSampleGroup()" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-all">
                    Create Sample Group
                </button>
            </div>
        `;
    }
}

function displayGroups(groups) {
    const groupsList = document.getElementById('groups-list');
    
    if (!groups || groups.length === 0) {
        groupsList.innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No groups found</p>
                <button onclick="createSampleGroup()" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-all">
                    Create Sample Group
                </button>
            </div>
        `;
        return;
    }
    
    groupsList.innerHTML = groups.map(group => `
        <div class="bg-gray-50 rounded-lg p-4 flex justify-between items-center">
            <div>
                <h4 class="font-semibold text-gray-800">${group.groupName}</h4>
                <p class="text-gray-600 text-sm">Created by: ${group.createdBy}</p>
                <p class="text-gray-500 text-xs">${new Date(group.createdAt).toLocaleDateString()}</p>
            </div>
            <div class="flex space-x-2">
                <button onclick="viewGroupDetails('${group.groupId}')" class="bg-blue-500 text-white px-3 py-1 rounded text-sm hover:bg-blue-600 transition-all">
                    View
                </button>
                <button onclick="deleteGroup('${group.groupId}')" class="bg-red-500 text-white px-3 py-1 rounded text-sm hover:bg-red-600 transition-all">
                    Delete
                </button>
            </div>
        </div>
    `).join('');
}

async function createSampleGroup() {
    try {
        const groupData = {
            groupName: `Family Trip ${Date.now()}`,
            createdBy: currentUser?.id || 'test-user-123'
        };
        
        const newGroup = await apiCall(`${API_ENDPOINTS.groups}`, {
            method: 'POST',
            body: JSON.stringify(groupData)
        });
        
        showSuccess('Group created successfully');
        loadGroups();
    } catch (error) {
        console.error('Failed to create group:', error);
    }
}

function showCreateGroupModal() {
    const modalContent = `
        <h3 class="text-xl font-bold mb-4">Create New Group</h3>
        <form onsubmit="createGroup(event)">
            <div class="mb-4">
                <label class="block text-gray-700 text-sm font-bold mb-2">Group Name</label>
                <input type="text" id="groupName" required 
                       class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
            </div>
            <div class="flex justify-end space-x-2">
                <button type="button" onclick="closeModal()" class="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition-all">
                    Cancel
                </button>
                <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-all">
                    Create Group
                </button>
            </div>
        </form>
    `;
    showModal(modalContent);
}

async function createGroup(event) {
    event.preventDefault();
    const groupName = document.getElementById('groupName').value;
    
    try {
        const groupData = {
            groupName,
            createdBy: currentUser?.id || 'test-user-123'
        };
        
        await apiCall(`${API_ENDPOINTS.groups}`, {
            method: 'POST',
            body: JSON.stringify(groupData)
        });
        
        closeModal();
        showSuccess('Group created successfully');
        loadGroups();
    } catch (error) {
        console.error('Failed to create group:', error);
    }
}

// Expenses Management
async function loadExpenses() {
    try {
        const expenses = await apiCall(`${API_ENDPOINTS.expenses}`);
        displayExpenses(expenses);
    } catch (error) {
        document.getElementById('expenses-list').innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No expenses found or unable to load expenses</p>
                <button onclick="createSampleExpense()" class="mt-4 bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 transition-all">
                    Create Sample Expense
                </button>
            </div>
        `;
    }
}

function displayExpenses(expenses) {
    const expensesList = document.getElementById('expenses-list');
    
    if (!expenses || expenses.length === 0) {
        expensesList.innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No expenses found</p>
                <button onclick="createSampleExpense()" class="mt-4 bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 transition-all">
                    Create Sample Expense
                </button>
            </div>
        `;
        return;
    }
    
    expensesList.innerHTML = expenses.map(expense => `
        <div class="bg-gray-50 rounded-lg p-4 flex justify-between items-center">
            <div>
                <h4 class="font-semibold text-gray-800">${expense.description}</h4>
                <p class="text-gray-600">Amount: $${expense.amount}</p>
                <p class="text-gray-600">Category: ${expense.category || 'Uncategorized'}</p>
                <p class="text-gray-500 text-xs">${new Date(expense.createdAt).toLocaleDateString()}</p>
            </div>
            <div class="flex space-x-2">
                <button onclick="viewExpenseDetails('${expense.expenseId}')" class="bg-green-500 text-white px-3 py-1 rounded text-sm hover:bg-green-600 transition-all">
                    View
                </button>
                <button onclick="deleteExpense('${expense.expenseId}')" class="bg-red-500 text-white px-3 py-1 rounded text-sm hover:bg-red-600 transition-all">
                    Delete
                </button>
            </div>
        </div>
    `).join('');
}

async function createSampleExpense() {
    try {
        // First, get available groups
        const groups = await apiCall(`${API_ENDPOINTS.groups}`);
        let groupId = null;
        
        if (groups && groups.length > 0) {
            groupId = groups[0].groupId;
        } else {
            // Create a group first
            const newGroup = await apiCall(`${API_ENDPOINTS.groups}`, {
                method: 'POST',
                body: JSON.stringify({
                    groupName: 'Default Group',
                    createdBy: currentUser?.id || 'test-user-123'
                })
            });
            groupId = newGroup.groupId;
        }
        
        const expenseData = {
            description: `Lunch at Restaurant ${Date.now()}`,
            amount: 25.50,
            groupId: groupId,
            createdBy: currentUser?.id || 'test-user-123',
            splitMethod: 'EQUAL',
            category: 'Food & Dining'
        };
        
        await apiCall(`${API_ENDPOINTS.expenses}`, {
            method: 'POST',
            body: JSON.stringify(expenseData)
        });
        
        showSuccess('Expense created successfully');
        loadExpenses();
    } catch (error) {
        console.error('Failed to create expense:', error);
    }
}

function showCreateExpenseModal() {
    const modalContent = `
        <h3 class="text-xl font-bold mb-4">Add New Expense</h3>
        <form onsubmit="createExpense(event)">
            <div class="mb-4">
                <label class="block text-gray-700 text-sm font-bold mb-2">Description</label>
                <input type="text" id="expenseDescription" required 
                       class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500">
            </div>
            <div class="mb-4">
                <label class="block text-gray-700 text-sm font-bold mb-2">Amount</label>
                <input type="number" step="0.01" id="expenseAmount" required 
                       class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500">
            </div>
            <div class="mb-4">
                <label class="block text-gray-700 text-sm font-bold mb-2">Category</label>
                <select id="expenseCategory" class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500">
                    <option value="Food & Dining">Food & Dining</option>
                    <option value="Transportation">Transportation</option>
                    <option value="Entertainment">Entertainment</option>
                    <option value="Utilities">Utilities</option>
                    <option value="Shopping">Shopping</option>
                    <option value="Other">Other</option>
                </select>
            </div>
            <div class="flex justify-end space-x-2">
                <button type="button" onclick="closeModal()" class="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition-all">
                    Cancel
                </button>
                <button type="submit" class="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 transition-all">
                    Add Expense
                </button>
            </div>
        </form>
    `;
    showModal(modalContent);
}

// AI Features
async function categorizeExpense() {
    const merchantText = document.getElementById('merchant-text').value;
    const transactionType = document.getElementById('transaction-type').value;
    const amount = document.getElementById('amount').value;
    
    if (!merchantText) {
        showError('Please enter a merchant name');
        return;
    }
    
    try {
        const data = {
            merchant_text: merchantText,
            transaction_type: transactionType,
            amount: amount ? parseFloat(amount) : null
        };
        
        const result = await apiCall(`${API_ENDPOINTS.ai}/categorize`, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        
        const resultDiv = document.getElementById('categorization-result');
        resultDiv.innerHTML = `
            <div class="bg-white border rounded-lg p-4">
                <h4 class="font-semibold text-green-700 mb-2">
                    <i class="fas fa-check-circle mr-2"></i>Categorization Result
                </h4>
                <div class="space-y-2">
                    <p><strong>Predicted Category:</strong> ${result.predicted_category}</p>
                    <p><strong>Confidence Score:</strong> ${(result.confidence_score * 100).toFixed(1)}%</p>
                    ${result.alternative_categories ? `
                        <p><strong>Alternative Suggestions:</strong> ${result.alternative_categories.join(', ')}</p>
                    ` : ''}
                </div>
            </div>
        `;
    } catch (error) {
        document.getElementById('categorization-result').innerHTML = `
            <div class="bg-red-50 border border-red-200 rounded-lg p-4">
                <p class="text-red-700">Failed to categorize expense: ${error.message}</p>
            </div>
        `;
    }
}

async function generateTripBudget() {
    const description = document.getElementById('trip-description').value;
    const destination = document.getElementById('destination').value;
    const duration = document.getElementById('duration').value;
    const budgetRange = document.getElementById('budget-range').value;
    
    if (!description) {
        showError('Please provide a trip description');
        return;
    }
    
    try {
        const data = {
            prompt_text: description,
            destination: destination || null,
            duration_days: duration ? parseInt(duration) : null,
            budget_range: budgetRange || null
        };
        
        const result = await apiCall(`${API_ENDPOINTS.ai}/copilot/trip-budget`, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        
        const resultDiv = document.getElementById('trip-budget-result');
        resultDiv.innerHTML = `
            <div class="bg-white border rounded-lg p-4">
                <h4 class="font-semibold text-blue-700 mb-4">
                    <i class="fas fa-map-marked-alt mr-2"></i>Trip Budget Plan
                </h4>
                <div class="space-y-4">
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        ${result.budget_items.map(item => `
                            <div class="bg-gray-50 p-3 rounded">
                                <h5 class="font-semibold">${item.category}</h5>
                                <p class="text-gray-600">${item.description}</p>
                                <p class="text-green-600 font-semibold">$${item.estimated_cost.toFixed(2)}</p>
                            </div>
                        `).join('')}
                    </div>
                    <div class="border-t pt-4">
                        <p class="text-xl font-bold text-blue-700">
                            Total Estimated Cost: $${result.total_estimated_cost.toFixed(2)} ${result.currency}
                        </p>
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        document.getElementById('trip-budget-result').innerHTML = `
            <div class="bg-red-50 border border-red-200 rounded-lg p-4">
                <p class="text-red-700">Failed to generate trip budget: ${error.message}</p>
            </div>
        `;
    }
}

function handleChatKeyPress(event) {
    if (event.key === 'Enter') {
        sendChatMessage();
    }
}

async function sendChatMessage() {
    const input = document.getElementById('chat-input');
    const message = input.value.trim();
    
    if (!message) return;
    
    // Add user message to chat
    addChatMessage('user', message);
    input.value = '';
    
    try {
        const data = {
            message: message,
            conversation_history: chatHistory,
            user_context: currentUser
        };
        
        const result = await apiCall(`${API_ENDPOINTS.ai}/copilot/chat`, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        
        // Add AI response to chat
        addChatMessage('model', result.reply);
        
        // Update chat history
        chatHistory.push(
            { role: 'user', text: message },
            { role: 'model', text: result.reply }
        );
        
        // Keep only last 10 messages
        if (chatHistory.length > 10) {
            chatHistory = chatHistory.slice(-10);
        }
        
    } catch (error) {
        addChatMessage('model', 'Sorry, I encountered an error. Please try again.');
    }
}

function addChatMessage(role, text) {
    const messagesContainer = document.getElementById('chat-messages');
    const isUser = role === 'user';
    
    // Clear initial message if this is the first real message
    if (messagesContainer.children.length === 1 && messagesContainer.children[0].textContent.includes('Start a conversation')) {
        messagesContainer.innerHTML = '';
    }
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `mb-4 ${isUser ? 'text-right' : 'text-left'}`;
    messageDiv.innerHTML = `
        <div class="inline-block max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
            isUser 
                ? 'bg-purple-500 text-white' 
                : 'bg-gray-200 text-gray-800'
        }">
            <p class="text-sm">${text}</p>
        </div>
    `;
    
    messagesContainer.appendChild(messageDiv);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// Analytics Features
async function loadBudgets() {
    try {
        const budgets = await apiCall(`${API_ENDPOINTS.analytics}/budgets`);
        displayBudgets(budgets);
    } catch (error) {
        document.getElementById('budgets-list').innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No budgets found or unable to load budgets</p>
                <button onclick="createSampleBudget()" class="mt-4 bg-indigo-500 text-white px-4 py-2 rounded-lg hover:bg-indigo-600 transition-all">
                    Create Sample Budget
                </button>
            </div>
        `;
    }
}

function displayBudgets(budgets) {
    const budgetsList = document.getElementById('budgets-list');
    
    if (!budgets || budgets.length === 0) {
        budgetsList.innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No budgets found</p>
                <button onclick="createSampleBudget()" class="mt-4 bg-indigo-500 text-white px-4 py-2 rounded-lg hover:bg-indigo-600 transition-all">
                    Create Sample Budget
                </button>
            </div>
        `;
        return;
    }
    
    budgetsList.innerHTML = budgets.map(budget => `
        <div class="bg-gray-50 rounded-lg p-4 flex justify-between items-center">
            <div>
                <h4 class="font-semibold text-gray-800">${budget.category}</h4>
                <p class="text-gray-600">Amount: $${budget.amount}</p>
                <p class="text-gray-600">Period: ${budget.period}</p>
            </div>
            <div class="flex space-x-2">
                <button onclick="deleteBudget('${budget.budgetId}')" class="bg-red-500 text-white px-3 py-1 rounded text-sm hover:bg-red-600 transition-all">
                    Delete
                </button>
            </div>
        </div>
    `).join('');
}

async function createSampleBudget() {
    try {
        const budgetData = {
            category: 'Food & Dining',
            amount: 500.00,
            period: 'MONTHLY',
            userId: currentUser?.id || 'test-user-123'
        };
        
        await apiCall(`${API_ENDPOINTS.analytics}/budgets`, {
            method: 'POST',
            body: JSON.stringify(budgetData)
        });
        
        showSuccess('Budget created successfully');
        loadBudgets();
    } catch (error) {
        console.error('Failed to create budget:', error);
    }
}

function showCreateBudgetModal() {
    const modalContent = `
        <h3 class="text-xl font-bold mb-4">Create Budget</h3>
        <form onsubmit="createBudget(event)">
            <div class="mb-4">
                <label class="block text-gray-700 text-sm font-bold mb-2">Category</label>
                <select id="budgetCategory" class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                    <option value="Food & Dining">Food & Dining</option>
                    <option value="Transportation">Transportation</option>
                    <option value="Entertainment">Entertainment</option>
                    <option value="Utilities">Utilities</option>
                    <option value="Shopping">Shopping</option>
                    <option value="Other">Other</option>
                </select>
            </div>
            <div class="mb-4">
                <label class="block text-gray-700 text-sm font-bold mb-2">Amount</label>
                <input type="number" step="0.01" id="budgetAmount" required 
                       class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500">
            </div>
            <div class="mb-4">
                <label class="block text-gray-700 text-sm font-bold mb-2">Period</label>
                <select id="budgetPeriod" class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                    <option value="MONTHLY">Monthly</option>
                    <option value="WEEKLY">Weekly</option>
                </select>
            </div>
            <div class="flex justify-end space-x-2">
                <button type="button" onclick="closeModal()" class="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition-all">
                    Cancel
                </button>
                <button type="submit" class="bg-indigo-500 text-white px-4 py-2 rounded-lg hover:bg-indigo-600 transition-all">
                    Create Budget
                </button>
            </div>
        </form>
    `;
    showModal(modalContent);
}

async function createBudget(event) {
    event.preventDefault();
    const category = document.getElementById('budgetCategory').value;
    const amount = parseFloat(document.getElementById('budgetAmount').value);
    const period = document.getElementById('budgetPeriod').value;
    
    try {
        const budgetData = {
            category,
            amount,
            period,
            userId: currentUser?.id || 'test-user-123'
        };
        
        await apiCall(`${API_ENDPOINTS.analytics}/budgets`, {
            method: 'POST',
            body: JSON.stringify(budgetData)
        });
        
        closeModal();
        showSuccess('Budget created successfully');
        loadBudgets();
    } catch (error) {
        console.error('Failed to create budget:', error);
    }
}

async function getMonthlyOverview() {
    try {
        const data = await apiCall(`${API_ENDPOINTS.analytics}/insights/monthly-overview`);
        document.getElementById('monthly-overview').innerHTML = `
            <div class="bg-white border rounded p-4">
                <h5 class="font-semibold mb-2">Monthly Overview</h5>
                <pre class="text-sm text-gray-600">${JSON.stringify(data, null, 2)}</pre>
            </div>
        `;
    } catch (error) {
        document.getElementById('monthly-overview').innerHTML = `
            <div class="bg-red-50 border border-red-200 rounded p-4">
                <p class="text-red-700">Unable to load monthly overview</p>
            </div>
        `;
    }
}

async function getCategoryBreakdown() {
    try {
        const data = await apiCall(`${API_ENDPOINTS.analytics}/insights/category-breakdown`);
        document.getElementById('category-breakdown').innerHTML = `
            <div class="bg-white border rounded p-4">
                <h5 class="font-semibold mb-2">Category Breakdown</h5>
                <pre class="text-sm text-gray-600">${JSON.stringify(data, null, 2)}</pre>
            </div>
        `;
    } catch (error) {
        document.getElementById('category-breakdown').innerHTML = `
            <div class="bg-red-50 border border-red-200 rounded p-4">
                <p class="text-red-700">Unable to load category breakdown</p>
            </div>
        `;
    }
}

// Settlements
async function loadSettlements() {
    try {
        const settlements = await apiCall(`${API_ENDPOINTS.settlements}/transactions`);
        displaySettlements(settlements);
    } catch (error) {
        document.getElementById('settlements-list').innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No settlements found or unable to load settlements</p>
                <button onclick="createSampleSettlement()" class="mt-4 bg-orange-500 text-white px-4 py-2 rounded-lg hover:bg-orange-600 transition-all">
                    Create Sample Settlement
                </button>
            </div>
        `;
    }
}

function displaySettlements(settlements) {
    const settlementsList = document.getElementById('settlements-list');
    
    if (!settlements || settlements.length === 0) {
        settlementsList.innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No settlements found</p>
                <button onclick="createSampleSettlement()" class="mt-4 bg-orange-500 text-white px-4 py-2 rounded-lg hover:bg-orange-600 transition-all">
                    Create Sample Settlement
                </button>
            </div>
        `;
        return;
    }
    
    settlementsList.innerHTML = settlements.map(settlement => `
        <div class="bg-gray-50 rounded-lg p-4 flex justify-between items-center">
            <div>
                <h4 class="font-semibold text-gray-800">Settlement Transaction</h4>
                <p class="text-gray-600">From: ${settlement.fromUserId}</p>
                <p class="text-gray-600">To: ${settlement.toUserId}</p>
                <p class="text-gray-600">Amount: $${settlement.amount}</p>
                <p class="text-gray-600">Status: ${settlement.settled ? 'Settled' : 'Pending'}</p>
            </div>
            <div class="flex space-x-2">
                ${!settlement.settled ? `
                    <button onclick="settleTransaction('${settlement.transactionId}')" class="bg-green-500 text-white px-3 py-1 rounded text-sm hover:bg-green-600 transition-all">
                        Settle
                    </button>
                ` : ''}
            </div>
        </div>
    `).join('');
}

async function createSampleSettlement() {
    try {
        const transactionData = {
            fromUserId: 'user-1',
            toUserId: currentUser?.id || 'test-user-123',
            amount: 15.75,
            expenseId: 'sample-expense-' + Date.now(),
            groupId: 'sample-group-' + Date.now()
        };
        
        await apiCall(`${API_ENDPOINTS.settlements}/transactions`, {
            method: 'POST',
            body: JSON.stringify(transactionData)
        });
        
        showSuccess('Settlement transaction created');
        loadSettlements();
    } catch (error) {
        console.error('Failed to create settlement:', error);
    }
}

// Notifications
async function loadNotifications() {
    try {
        const notifications = await apiCall(`${API_ENDPOINTS.notifications}`);
        displayNotifications(notifications);
    } catch (error) {
        document.getElementById('notifications-list').innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No notifications found or unable to load notifications</p>
                <button onclick="sendTestNotification()" class="mt-4 bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-all">
                    Send Test Notification
                </button>
            </div>
        `;
    }
}

function displayNotifications(notifications) {
    const notificationsList = document.getElementById('notifications-list');
    
    if (!notifications || notifications.length === 0) {
        notificationsList.innerHTML = `
            <div class="text-center py-8">
                <p class="text-gray-500">No notifications found</p>
                <button onclick="sendTestNotification()" class="mt-4 bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-all">
                    Send Test Notification
                </button>
            </div>
        `;
        return;
    }
    
    notificationsList.innerHTML = notifications.map(notification => `
        <div class="bg-gray-50 rounded-lg p-4">
            <h4 class="font-semibold text-gray-800">${notification.title}</h4>
            <p class="text-gray-600">${notification.message}</p>
            <p class="text-gray-500 text-xs">${new Date(notification.timestamp).toLocaleString()}</p>
        </div>
    `).join('');
}

async function sendTestNotification() {
    try {
        const notificationData = {
            recipientId: currentUser?.id || 'test-user-123',
            type: 'EXPENSE_ADDED',
            title: 'Test Notification',
            message: 'This is a test notification from FinShare',
            metadata: {}
        };
        
        await apiCall(`${API_ENDPOINTS.notifications}/send`, {
            method: 'POST',
            body: JSON.stringify(notificationData)
        });
        
        showSuccess('Test notification sent');
        loadNotifications();
    } catch (error) {
        console.error('Failed to send notification:', error);
    }
}

// Modal Functions
function showModal(content) {
    document.getElementById('modal-content').innerHTML = content;
    document.getElementById('modal-overlay').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('modal-overlay').classList.add('hidden');
}

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    // Set up login button
    document.getElementById('loginBtn').onclick = login;
    
    // Close modal when clicking overlay
    document.getElementById('modal-overlay').onclick = function(e) {
        if (e.target === this) {
            closeModal();
        }
    };
    
    // Auto-login for demo
    login();
});

// Health Check
async function checkApiHealth() {
    try {
        const response = await fetch(`${API_BASE_URL}/actuator/health`);
        const data = await response.json();
        console.log('API Gateway Health:', data);
    } catch (error) {
        console.error('API Gateway health check failed:', error);
    }
}

// Run health check on load
checkApiHealth();