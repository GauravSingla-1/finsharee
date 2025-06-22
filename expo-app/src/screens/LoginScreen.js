import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Ionicons } from '@expo/vector-icons';

export default function LoginScreen({ onLogin }) {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    if (!phoneNumber || phoneNumber.length < 10) {
      Alert.alert('Error', 'Please enter a valid phone number');
      return;
    }

    setLoading(true);
    try {
      // Simulate authentication with backend
      const response = await fetch('http://YOUR_IP:5000/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ phoneNumber }),
      });

      if (response.ok) {
        const data = await response.json();
        await AsyncStorage.setItem('userToken', data.token);
        await AsyncStorage.setItem('userId', data.userId);
        onLogin();
      } else {
        // For demo purposes, allow any 10-digit number
        if (phoneNumber.length === 10) {
          await AsyncStorage.setItem('userToken', 'demo-token');
          await AsyncStorage.setItem('userId', `user-${phoneNumber}`);
          onLogin();
        } else {
          Alert.alert('Error', 'Invalid credentials');
        }
      }
    } catch (error) {
      console.error('Login error:', error);
      // Demo mode - allow login with any 10-digit number
      if (phoneNumber.length === 10) {
        await AsyncStorage.setItem('userToken', 'demo-token');
        await AsyncStorage.setItem('userId', `user-${phoneNumber}`);
        onLogin();
      } else {
        Alert.alert('Error', 'Login failed. Using demo mode.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Ionicons name="wallet" size={80} color="#007AFF" />
        <Text style={styles.title}>FinShare</Text>
        <Text style={styles.subtitle}>Smart Group Expense Management</Text>
      </View>

      <View style={styles.loginForm}>
        <Text style={styles.label}>Phone Number</Text>
        <TextInput
          style={styles.input}
          placeholder="Enter your phone number"
          value={phoneNumber}
          onChangeText={setPhoneNumber}
          keyboardType="phone-pad"
          maxLength={15}
        />

        <TouchableOpacity
          style={styles.loginButton}
          onPress={handleLogin}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="white" />
          ) : (
            <Text style={styles.loginButtonText}>Login</Text>
          )}
        </TouchableOpacity>

        <Text style={styles.demoText}>
          Demo Mode: Enter any 10-digit number to login
        </Text>
      </View>

      <View style={styles.features}>
        <Text style={styles.featuresTitle}>Features:</Text>
        <Text style={styles.featureItem}>• AI-powered expense categorization</Text>
        <Text style={styles.featureItem}>• SMS expense capture</Text>
        <Text style={styles.featureItem}>• Receipt scanning with OCR</Text>
        <Text style={styles.featureItem}>• Smart expense splitting</Text>
        <Text style={styles.featureItem}>• Biometric authentication</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
    padding: 20,
    justifyContent: 'center',
  },
  header: {
    alignItems: 'center',
    marginBottom: 40,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#007AFF',
    marginTop: 10,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    marginTop: 5,
  },
  loginForm: {
    marginBottom: 40,
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 8,
    color: '#333',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 15,
    fontSize: 16,
    backgroundColor: 'white',
    marginBottom: 20,
  },
  loginButton: {
    backgroundColor: '#007AFF',
    borderRadius: 8,
    padding: 15,
    alignItems: 'center',
  },
  loginButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: '600',
  },
  demoText: {
    textAlign: 'center',
    color: '#666',
    fontSize: 12,
    marginTop: 10,
  },
  features: {
    backgroundColor: 'white',
    borderRadius: 8,
    padding: 20,
  },
  featuresTitle: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 10,
    color: '#333',
  },
  featureItem: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
});