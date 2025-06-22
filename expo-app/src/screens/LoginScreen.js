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
import { API_CONFIG } from '../../config';

export default function LoginScreen({ onLogin }) {
  const [email, setEmail] = useState('demo@finshare.app');
  const [password, setPassword] = useState('password123');
  const [loading, setLoading] = useState(false);
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  const [name, setName] = useState('Demo User');

  const makeAuthenticatedRequest = async (endpoint, options = {}) => {
    const url = `${API_CONFIG.BASE_URL}${endpoint}`;
    const response = await fetch(url, {
      ...options,
      headers: {
        ...API_CONFIG.HEADERS,
        ...options.headers,
      },
    });
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ detail: 'Network error' }));
      throw new Error(errorData.detail || `HTTP ${response.status}`);
    }
    
    return response.json();
  };

  const handleLogin = async () => {
    if (!email.trim() || !password.trim()) {
      Alert.alert('Error', 'Please enter email and password');
      return;
    }

    setLoading(true);
    try {
      const result = await makeAuthenticatedRequest(API_CONFIG.ENDPOINTS.LOGIN, {
        method: 'POST',
        body: JSON.stringify({
          email: email.trim(),
          password: password.trim(),
        }),
      });

      // Store token and user info
      await AsyncStorage.setItem('authToken', result.token);
      await AsyncStorage.setItem('user', JSON.stringify(result.user));
      
      onLogin(result.user);
    } catch (error) {
      Alert.alert('Login Failed', error.message || 'Please check your credentials');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async () => {
    if (!email.trim() || !password.trim() || !name.trim()) {
      Alert.alert('Error', 'Please fill in all fields');
      return;
    }

    setLoading(true);
    try {
      const result = await makeAuthenticatedRequest(API_CONFIG.ENDPOINTS.REGISTER, {
        method: 'POST',
        body: JSON.stringify({
          email: email.trim(),
          password: password.trim(),
          name: name.trim(),
        }),
      });

      // Store token and user info
      await AsyncStorage.setItem('authToken', result.token);
      await AsyncStorage.setItem('user', JSON.stringify(result.user));
      
      Alert.alert('Success', 'Account created successfully!');
      onLogin(result.user);
    } catch (error) {
      Alert.alert('Registration Failed', error.message || 'Please try again');
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
        <Text style={styles.label}>{isRegisterMode ? 'Create Account' : 'Login to FinShare'}</Text>
        
        {isRegisterMode && (
          <>
            <Text style={styles.fieldLabel}>Full Name</Text>
            <TextInput
              style={styles.input}
              placeholder="Enter your full name"
              value={name}
              onChangeText={setName}
              autoCapitalize="words"
            />
          </>
        )}
        
        <Text style={styles.fieldLabel}>Email</Text>
        <TextInput
          style={styles.input}
          placeholder="Enter your email"
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
          autoCapitalize="none"
          autoCorrect={false}
        />

        <Text style={styles.fieldLabel}>Password</Text>
        <TextInput
          style={styles.input}
          placeholder="Enter your password"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
          autoCapitalize="none"
        />

        <TouchableOpacity
          style={styles.loginButton}
          onPress={isRegisterMode ? handleRegister : handleLogin}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="white" />
          ) : (
            <Text style={styles.loginButtonText}>
              {isRegisterMode ? 'Create Account' : 'Login'}
            </Text>
          )}
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.switchButton}
          onPress={() => setIsRegisterMode(!isRegisterMode)}
          disabled={loading}
        >
          <Text style={styles.switchButtonText}>
            {isRegisterMode ? 'Already have an account? Login' : 'Need an account? Sign up'}
          </Text>
        </TouchableOpacity>

        <Text style={styles.demoText}>
          Demo credentials: demo@finshare.app / password123
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
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 20,
    color: '#333',
    textAlign: 'center',
  },
  fieldLabel: {
    fontSize: 14,
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
  switchButton: {
    marginTop: 15,
    alignItems: 'center',
  },
  switchButtonText: {
    color: '#007AFF',
    fontSize: 16,
    fontWeight: '500',
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