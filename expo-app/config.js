// API Configuration for FinShare Mobile App
export const API_CONFIG = {
  // Dynamic URL detection for mobile device testing
  BASE_URL: 'https://01hs0h02dgm2ywm07zq17nnzpa-5000.proxy.replit.dev',
  
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