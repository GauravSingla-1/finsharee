# FinShare Expo Setup Guide

## Quick Start (5 minutes)

### 1. Install Dependencies
```bash
cd expo-app
npm install
```

### 2. Update Backend URLs
Find and replace `YOUR_IP` with your computer's IP address in these files:
- `src/screens/LoginScreen.js`
- `src/screens/DashboardScreen.js`
- `src/screens/GroupsScreen.js`
- `src/screens/ExpensesScreen.js`
- `src/screens/AICoPilotScreen.js`
- `src/screens/ReceiptScanScreen.js`
- `src/screens/CreateGroupScreen.js`
- `src/screens/CreateExpenseScreen.js`

```bash
# Find your IP address:
# macOS/Linux:
ifconfig | grep "inet " | grep -v 127.0.0.1

# Windows:
ipconfig | findstr IPv4
```

### 3. Start the App
```bash
npx expo start
```

### 4. Test on Your Phone
- Install "Expo Go" app from App Store/Play Store
- Scan the QR code from terminal
- App will load on your phone

## Backend Requirements

Ensure these services are running:
- API Gateway: `http://YOUR_IP:5000`
- AI Service: `http://YOUR_IP:8004`
- Group Service: `http://YOUR_IP:8002`
- Balance Service: `http://YOUR_IP:8003`
- Analytics Service: `http://YOUR_IP:8005`
- Notification Service: `http://YOUR_IP:8006`

## Features Available

### Immediate Testing (No Setup Required)
- Login with any 10-digit phone number
- View demo dashboard with sample data
- Navigate between all screens
- Test UI components and interactions

### With Backend Connected
- Real AI expense categorization
- Actual group creation and management
- Live expense tracking and splitting
- Receipt scanning with OCR
- Push notifications
- Biometric authentication

## Demo Mode

The app includes comprehensive demo data so you can test immediately:
- Sample groups and expenses
- Mock AI responses
- Simulated categorization
- Fallback data for all features

## Production Build

When ready for production:
```bash
# Build APK
eas build --platform android

# Build for iOS
eas build --platform ios
```

## Troubleshooting

### "Network Error"
- Check if backend services are running
- Verify IP address is correct
- Test backend URL in browser: `http://YOUR_IP:5000`

### "Expo Go Not Working"
- Ensure phone and computer are on same network
- Try using tunnel mode: `npx expo start --tunnel`

### "Camera Not Working"
- Grant camera permissions in phone settings
- Restart Expo Go app

Your FinShare Expo app is ready to test with full backend integration!