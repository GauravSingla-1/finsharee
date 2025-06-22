// API Configuration for FinShare Mobile App
export const API_CONFIG = {
  // Use tunnel URL for mobile device testing
  BASE_URL: 'http://172.31.128.45:5000',
  
  // Alternative: Use Replit's public URL format
  // BASE_URL: 'https://workspace-username.replit.app',
  
  // For local development (won't work on mobile devices)
  // BASE_URL: 'http://localhost:5000',
  
  ENDPOINTS: {
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