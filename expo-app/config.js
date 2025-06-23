// API Configuration for FinShare Mobile App
export const API_CONFIG = {
  // Dynamic URL detection for mobile device testing
  BASE_URL: __DEV__ ? 'http://172.31.128.6:8000' : 'https://your-production-api.com',
  
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