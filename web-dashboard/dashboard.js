// FinShare API Dashboard JavaScript

const API_CONFIG = {
    PROXY_URL: 'http://127.0.0.1:3001/proxy',
    SERVICES: {
        auth: 'http://127.0.0.1:5000',
        ai: 'http://127.0.0.1:8004',
        group: 'http://127.0.0.1:8002',
        balance: 'http://127.0.0.1:8003',
        analytics: 'http://127.0.0.1:8005',
        notification: 'http://127.0.0.1:8006'
    }
};

let currentToken = '';

// Service status checking
async function checkServiceStatus() {
    const statusContainer = document.getElementById('serviceStatus');
    const services = [
        { name: 'Auth Backend', url: API_CONFIG.SERVICES.auth, port: '5000' },
        { name: 'Group Expense', url: API_CONFIG.SERVICES.group, port: '8002' },
        { name: 'Balance Settlement', url: API_CONFIG.SERVICES.balance, port: '8003' },
        { name: 'AI Service', url: API_CONFIG.SERVICES.ai, port: '8004' },
        { name: 'Analytics', url: API_CONFIG.SERVICES.analytics, port: '8005' },
        { name: 'Notification', url: API_CONFIG.SERVICES.notification, port: '8006' }
    ];

    statusContainer.innerHTML = '';
    
    for (const service of services) {
        const card = document.createElement('div');
        card.className = 'service-card';
        card.innerHTML = `
            <h4>${service.name}</h4>
            <p>Port: ${service.port}</p>
            <p id="status-${service.port}">Checking...</p>
        `;
        statusContainer.appendChild(card);

        try {
            const response = await fetch(`${service.url}/actuator/health`, {
                method: 'GET',
                timeout: 5000
            }).catch(() => 
                fetch(`${service.url}/health`).catch(() =>
                    fetch(`${service.url}/`))
            );
            
            if (response && response.ok) {
                card.classList.add('online');
                document.getElementById(`status-${service.port}`).textContent = '✅ Online';
            } else {
                card.classList.add('offline');
                document.getElementById(`status-${service.port}`).textContent = '❌ Offline';
            }
        } catch (error) {
            card.classList.add('offline');
            document.getElementById(`status-${service.port}`).textContent = '❌ Offline';
        }
    }
}

// Quick login function
async function testLogin() {
    try {
        const response = await fetch(`${API_CONFIG.SERVICES.auth}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: 'demo@finshare.app',
                password: 'password123'
            })
        });

        if (response.ok) {
            const data = await response.json();
            currentToken = data.token;
            document.getElementById('authToken').value = data.token;
            alert(`Login successful! Token: ${data.token.substring(0, 20)}...`);
        } else {
            const error = await response.text();
            alert(`Login failed: ${error}`);
        }
    } catch (error) {
        alert(`Login error: ${error.message}`);
    }
}

// Load endpoint configuration
function loadEndpoint(service, endpoint) {
    // Remove active class from all items
    document.querySelectorAll('.endpoint-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // Add active class to clicked item
    event.target.classList.add('active');

    const endpointConfigs = {
        auth: {
            login: {
                method: 'POST',
                url: '/api/auth/login',
                service: 'auth',
                body: '{\n  "email": "demo@finshare.app",\n  "password": "password123"\n}'
            },
            register: {
                method: 'POST',
                url: '/api/auth/register',
                service: 'auth',
                body: '{\n  "email": "newuser@example.com",\n  "password": "password123",\n  "name": "New User"\n}'
            },
            user: {
                method: 'GET',
                url: '/api/auth/user',
                service: 'auth',
                requiresAuth: true
            }
        },
        group: {
            create: {
                method: 'POST',
                url: '/api/groups',
                service: 'group',
                body: '{\n  "name": "Test Group",\n  "description": "A test group for expenses"\n}',
                requiresAuth: true
            },
            list: {
                method: 'GET',
                url: '/api/groups',
                service: 'group',
                requiresAuth: true
            }
        },
        expense: {
            create: {
                method: 'POST',
                url: '/api/expenses',
                service: 'group',
                body: '{\n  "description": "Dinner at restaurant",\n  "amount": 120.50,\n  "category": "Food",\n  "groupId": "group1",\n  "splitMethod": "EQUAL"\n}',
                requiresAuth: true
            },
            list: {
                method: 'GET',
                url: '/api/expenses',
                service: 'group',
                requiresAuth: true
            }
        },
        ai: {
            categorize: {
                method: 'POST',
                url: '/categorize',
                service: 'ai',
                body: '{\n  "description": "Uber ride to downtown",\n  "amount": 25.50\n}'
            },
            chat: {
                method: 'POST',
                url: '/chat',
                service: 'ai',
                body: '{\n  "message": "How should I split a restaurant bill fairly?"\n}'
            },
            receipt: {
                method: 'POST',
                url: '/receipt-scan',
                service: 'ai',
                body: '{\n  "image_data": "base64_encoded_receipt_image",\n  "group_id": "group1"\n}'
            }
        },
        balance: {
            calculate: {
                method: 'POST',
                url: '/api/balances/calculate',
                service: 'balance',
                body: '{\n  "groupId": "group1"\n}',
                requiresAuth: true
            },
            settle: {
                method: 'POST',
                url: '/api/balances/settle',
                service: 'balance',
                body: '{\n  "fromUserId": "user1",\n  "toUserId": "user2",\n  "amount": 50.00\n}',
                requiresAuth: true
            }
        },
        analytics: {
            insights: {
                method: 'GET',
                url: '/api/analytics/insights',
                service: 'analytics',
                requiresAuth: true
            },
            budget: {
                method: 'GET',
                url: '/api/analytics/budget',
                service: 'analytics',
                requiresAuth: true
            }
        },
        notification: {
            send: {
                method: 'POST',
                url: '/api/notifications/send',
                service: 'notification',
                body: '{\n  "userId": "user1",\n  "message": "You have a new expense to review",\n  "type": "EXPENSE_ADDED"\n}',
                requiresAuth: true
            },
            list: {
                method: 'GET',
                url: '/api/notifications',
                service: 'notification',
                requiresAuth: true
            }
        }
    };

    const config = endpointConfigs[service][endpoint];
    if (!config) return;

    document.getElementById('endpointTitle').textContent = `${config.method} ${config.url}`;
    document.getElementById('endpointForm').classList.remove('hidden');
    document.getElementById('httpMethod').value = config.method;
    document.getElementById('endpoint').value = `${API_CONFIG.SERVICES[config.service]}${config.url}`;
    
    const requestBodyGroup = document.getElementById('requestBodyGroup');
    if (config.method === 'GET') {
        requestBodyGroup.style.display = 'none';
    } else {
        requestBodyGroup.style.display = 'block';
        document.getElementById('requestBody').value = config.body || '';
    }

    // Show auth requirement warning
    if (config.requiresAuth) {
        document.getElementById('endpointTitle').innerHTML += ' <span style="color: #dc3545;">(Requires Authentication)</span>';
    }
}

// Execute API request
async function executeRequest() {
    const method = document.getElementById('httpMethod').value;
    const url = document.getElementById('endpoint').value;
    const body = document.getElementById('requestBody').value;
    const token = document.getElementById('authToken').value || currentToken;

    const headers = {
        'Content-Type': 'application/json'
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const requestOptions = {
        method: method,
        headers: headers,
        mode: 'cors'
    };

    if (method !== 'GET' && body.trim()) {
        try {
            JSON.parse(body); // Validate JSON
            requestOptions.body = body;
        } catch (error) {
            displayResponse(400, { error: 'Invalid JSON in request body' });
            return;
        }
    }

    try {
        document.getElementById('apiTester').classList.add('loading');
        
        const startTime = Date.now();
        const response = await fetch(url, requestOptions);
        const endTime = Date.now();
        
        let responseData;
        const contentType = response.headers.get('content-type');
        
        if (contentType && contentType.includes('application/json')) {
            responseData = await response.json();
        } else {
            responseData = await response.text();
        }

        displayResponse(response.status, responseData, endTime - startTime);
    } catch (error) {
        displayResponse(0, { error: error.message, type: 'Network Error' });
    } finally {
        document.getElementById('apiTester').classList.remove('loading');
    }
}

// Display response
function displayResponse(status, data, responseTime = null) {
    const statusElement = document.getElementById('responseStatus');
    const bodyElement = document.getElementById('responseBody');

    // Status indicator
    let statusClass = 'status-500';
    if (status >= 200 && status < 300) statusClass = 'status-200';
    else if (status >= 400 && status < 500) statusClass = 'status-400';

    const timeText = responseTime ? ` (${responseTime}ms)` : '';
    statusElement.innerHTML = `<span class="status-indicator ${statusClass}">Status: ${status}${timeText}</span>`;

    // Response body
    if (typeof data === 'object') {
        bodyElement.textContent = JSON.stringify(data, null, 2);
    } else {
        bodyElement.textContent = data;
    }
}

// Clear response
function clearResponse() {
    document.getElementById('responseStatus').innerHTML = '';
    document.getElementById('responseBody').textContent = 'Response cleared...';
}

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    checkServiceStatus();
    
    // Auto-refresh service status every 30 seconds
    setInterval(checkServiceStatus, 30000);
    
    // Set default endpoint
    loadEndpoint('auth', 'login');
});

// Utility function to format JSON
function formatJSON(jsonString) {
    try {
        const parsed = JSON.parse(jsonString);
        return JSON.stringify(parsed, null, 2);
    } catch (error) {
        return jsonString;
    }
}