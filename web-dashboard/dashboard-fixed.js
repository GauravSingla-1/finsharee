// FinShare API Dashboard JavaScript - Fixed Version

const API_CONFIG = {
    PROXY_URL: 'http://127.0.0.1:3001/proxy',
    SERVICES: {
        auth: 'http://127.0.0.1:5001',
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
    if (!statusContainer) return;
    
    const services = [
        { name: 'Auth Backend', url: 'http://127.0.0.1:5001', port: '5001' },
        { name: 'Group Expense', url: 'http://127.0.0.1:8002', port: '8002' },
        { name: 'Balance Settlement', url: 'http://127.0.0.1:8003', port: '8003' },
        { name: 'AI Service', url: 'http://127.0.0.1:8004', port: '8004' },
        { name: 'Analytics', url: 'http://127.0.0.1:8005', port: '8005' },
        { name: 'Notification', url: 'http://127.0.0.1:8006', port: '8006' }
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
            let response;
            let endpoint;
            
            // Try different health check endpoints based on service type
            if (service.port === '5001') {
                endpoint = '/';
                response = await fetch(`${service.url}${endpoint}`);
            } else if (service.port === '8004') {
                endpoint = '/health';
                response = await fetch(`${service.url}${endpoint}`);
            } else {
                endpoint = '/actuator/health';
                response = await fetch(`${service.url}${endpoint}`);
            }

            if (response.ok) {
                document.getElementById(`status-${service.port}`).innerHTML = '<span style="color: green;">✓ Online</span>';
                card.classList.add('online');
            } else {
                throw new Error(`HTTP ${response.status}`);
            }
        } catch (error) {
            document.getElementById(`status-${service.port}`).innerHTML = '<span style="color: red;">✗ Offline</span>';
            card.classList.add('offline');
        }
    }
}

// Quick login function
async function testLogin() {
    try {
        const response = await fetch('http://127.0.0.1:5001/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: 'demo@finshare.app',
                password: 'password123'
            })
        });

        const data = await response.json();
        if (response.ok && data.token) {
            currentToken = data.token;
            document.getElementById('authToken').value = data.token;
            alert('Login successful! Token saved.');
        } else {
            alert('Login failed: ' + (data.detail || 'Unknown error'));
        }
    } catch (error) {
        alert(`Login error: ${error.message}`);
    }
}

// Load endpoint configuration
function loadEndpoint(service, endpoint, clickedElement = null) {
    // Remove active class from all items
    document.querySelectorAll('.endpoint-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // Add active class to clicked item if provided
    if (clickedElement) {
        clickedElement.classList.add('active');
    }

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
                body: '{\n  "group_id": "group1",\n  "description": "Test Expense",\n  "amount": 25.50,\n  "category": "FOOD"\n}',
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
                url: '/api/ai/categorize',
                service: 'ai',
                body: '{\n  "merchant_text": "Starbucks Coffee Store"\n}',
                requiresAuth: true
            },
            chat: {
                method: 'POST',
                url: '/api/ai/chat',
                service: 'ai',
                body: '{\n  "message": "How can I save money on groceries?"\n}',
                requiresAuth: true
            },
            receipt: {
                method: 'POST',
                url: '/api/ai/receipt-scan',
                service: 'ai',
                body: '{\n  "receipt_text": "Restaurant ABC\\nTotal: $45.99\\nDate: 2025-06-23"\n}',
                requiresAuth: true
            }
        },
        balance: {
            calculate: {
                method: 'POST',
                url: '/api/balances/calculate',
                service: 'balance',
                body: '{\n  "group_id": "group1"\n}',
                requiresAuth: true
            },
            settle: {
                method: 'POST',
                url: '/api/balances/settle',
                service: 'balance',
                body: '{\n  "from_user_id": "user1",\n  "to_user_id": "user2",\n  "amount": 25.50\n}',
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
            list: {
                method: 'GET',
                url: '/api/notifications',
                service: 'notification',
                requiresAuth: true
            },
            send: {
                method: 'POST',
                url: '/api/notifications/send',
                service: 'notification',
                body: '{\n  "user_id": "user1",\n  "message": "Test notification",\n  "type": "info"\n}',
                requiresAuth: true
            }
        }
    };

    const config = endpointConfigs[service] && endpointConfigs[service][endpoint];
    if (!config) {
        alert('Invalid endpoint configuration');
        return;
    }

    // Populate form fields
    document.getElementById('httpMethod').value = config.method;
    document.getElementById('endpoint').value = `http://127.0.0.1:3001/proxy/${config.service}${config.url}`;
    document.getElementById('requestBody').value = config.body || '';
    document.getElementById('endpointTitle').textContent = `${config.method} ${config.url}`;

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
        const apiTester = document.getElementById('apiTester');
        if (apiTester) {
            apiTester.classList.add('loading');
        }
        
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
        const apiTester = document.getElementById('apiTester');
        if (apiTester) {
            apiTester.classList.remove('loading');
        }
    }
}

// Display response
function displayResponse(status, data, responseTime = null) {
    const statusElement = document.getElementById('responseStatus');
    const bodyElement = document.getElementById('responseBody');

    if (!statusElement || !bodyElement) {
        console.error('Response display elements not found');
        return;
    }

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
    const statusElement = document.getElementById('responseStatus');
    const bodyElement = document.getElementById('responseBody');
    
    if (statusElement) statusElement.innerHTML = '';
    if (bodyElement) bodyElement.textContent = 'Response cleared...';
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