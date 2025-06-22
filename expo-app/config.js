// API Configuration for FinShare Mobile App
export const API_CONFIG = {
  // Use tunnel URL for mobile device testing with new authenticated backend
  BASE_URL: 'http://172.31.128.45:8000',
  
  ENDPOINTS: {
    // Authentication endpoints
    LOGIN: '/api/auth/login',
    REGISTER: '/api/auth/register',
    USER: '/api/auth/user',
    
    // Core feature endpoints
    DASHBOARD: '/api/dashboard',
    GROUPS: '/api/groups',
    EXPENSES: '/api/expenses',
    AI_CATEGORIZE: '/api/ai/categorize',
    AI_CHAT: '/api/ai/chat',
    NOTIFICATIONS: '/api/notifications',
  },
  
  HEADERS: {
    'Content-Type': 'application/json',
  }
};