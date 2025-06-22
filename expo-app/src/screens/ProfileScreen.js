import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Alert,
  Switch,
  ScrollView,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as LocalAuthentication from 'expo-local-authentication';

export default function ProfileScreen({ navigation }) {
  const [userInfo, setUserInfo] = useState({
    name: '',
    phone: '',
    userId: '',
  });
  const [settings, setSettings] = useState({
    biometricAuth: false,
    notifications: true,
    smsCapture: true,
    autoCategoriztion: true,
  });

  useEffect(() => {
    loadUserInfo();
    loadSettings();
  }, []);

  const loadUserInfo = async () => {
    try {
      const userId = await AsyncStorage.getItem('userId');
      const phone = await AsyncStorage.getItem('userPhone') || 'Not set';
      
      setUserInfo({
        name: `User ${userId?.slice(-4) || '****'}`,
        phone: phone,
        userId: userId || 'unknown',
      });
    } catch (error) {
      console.error('Error loading user info:', error);
    }
  };

  const loadSettings = async () => {
    try {
      const settingsData = await AsyncStorage.getItem('userSettings');
      if (settingsData) {
        setSettings(JSON.parse(settingsData));
      }
    } catch (error) {
      console.error('Error loading settings:', error);
    }
  };

  const saveSettings = async (newSettings) => {
    try {
      await AsyncStorage.setItem('userSettings', JSON.stringify(newSettings));
      setSettings(newSettings);
    } catch (error) {
      console.error('Error saving settings:', error);
    }
  };

  const toggleBiometric = async (value) => {
    if (value) {
      const hasHardware = await LocalAuthentication.hasHardwareAsync();
      const isEnrolled = await LocalAuthentication.isEnrolledAsync();
      
      if (!hasHardware || !isEnrolled) {
        Alert.alert(
          'Biometric Authentication',
          'Biometric authentication is not available on this device or not set up.',
          [{ text: 'OK' }]
        );
        return;
      }

      const result = await LocalAuthentication.authenticateAsync({
        promptMessage: 'Enable biometric authentication for FinShare',
        fallbackLabel: 'Use passcode',
      });

      if (result.success) {
        saveSettings({ ...settings, biometricAuth: true });
      }
    } else {
      saveSettings({ ...settings, biometricAuth: false });
    }
  };

  const handleLogout = () => {
    Alert.alert(
      'Logout',
      'Are you sure you want to logout?',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Logout',
          style: 'destructive',
          onPress: async () => {
            await AsyncStorage.multiRemove(['userToken', 'userId', 'userPhone']);
            navigation.reset({
              index: 0,
              routes: [{ name: 'Login' }],
            });
          },
        },
      ]
    );
  };

  const ProfileSection = ({ title, children }) => (
    <View style={styles.section}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {children}
    </View>
  );

  const ProfileItem = ({ icon, title, subtitle, onPress, rightComponent }) => (
    <TouchableOpacity style={styles.profileItem} onPress={onPress}>
      <View style={styles.itemLeft}>
        <View style={styles.iconContainer}>
          <Ionicons name={icon} size={20} color="#007AFF" />
        </View>
        <View style={styles.itemContent}>
          <Text style={styles.itemTitle}>{title}</Text>
          {subtitle && <Text style={styles.itemSubtitle}>{subtitle}</Text>}
        </View>
      </View>
      {rightComponent || <Ionicons name="chevron-forward" size={16} color="#ccc" />}
    </TouchableOpacity>
  );

  const SettingItem = ({ icon, title, subtitle, value, onToggle }) => (
    <View style={styles.profileItem}>
      <View style={styles.itemLeft}>
        <View style={styles.iconContainer}>
          <Ionicons name={icon} size={20} color="#007AFF" />
        </View>
        <View style={styles.itemContent}>
          <Text style={styles.itemTitle}>{title}</Text>
          {subtitle && <Text style={styles.itemSubtitle}>{subtitle}</Text>}
        </View>
      </View>
      <Switch
        value={value}
        onValueChange={onToggle}
        trackColor={{ false: '#767577', true: '#007AFF' }}
        thumbColor={value ? '#ffffff' : '#f4f3f4'}
      />
    </View>
  );

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <View style={styles.avatar}>
          <Ionicons name="person" size={40} color="#007AFF" />
        </View>
        <Text style={styles.userName}>{userInfo.name}</Text>
        <Text style={styles.userPhone}>{userInfo.phone}</Text>
      </View>

      <ProfileSection title="Account">
        <ProfileItem
          icon="person-outline"
          title="Edit Profile"
          subtitle="Update your personal information"
          onPress={() => Alert.alert('Coming Soon', 'Profile editing will be available soon!')}
        />
        <ProfileItem
          icon="call-outline"
          title="Phone Number"
          subtitle={userInfo.phone}
          onPress={() => Alert.alert('Phone Number', userInfo.phone)}
        />
        <ProfileItem
          icon="id-card-outline"
          title="User ID"
          subtitle={userInfo.userId}
          onPress={() => Alert.alert('User ID', userInfo.userId)}
        />
      </ProfileSection>

      <ProfileSection title="Security">
        <SettingItem
          icon="finger-print"
          title="Biometric Authentication"
          subtitle="Use fingerprint or face ID to unlock app"
          value={settings.biometricAuth}
          onToggle={toggleBiometric}
        />
      </ProfileSection>

      <ProfileSection title="Preferences">
        <SettingItem
          icon="notifications-outline"
          title="Push Notifications"
          subtitle="Receive notifications for new expenses and settlements"
          value={settings.notifications}
          onToggle={(value) => saveSettings({ ...settings, notifications: value })}
        />
        <SettingItem
          icon="chatbubble-outline"
          title="SMS Expense Capture"
          subtitle="Automatically detect expenses from bank SMS"
          value={settings.smsCapture}
          onToggle={(value) => saveSettings({ ...settings, smsCapture: value })}
        />
        <SettingItem
          icon="brain"
          title="AI Auto-Categorization"
          subtitle="Automatically categorize expenses using AI"
          value={settings.autoCategoriztion}
          onToggle={(value) => saveSettings({ ...settings, autoCategoriztion: value })}
        />
      </ProfileSection>

      <ProfileSection title="App Info">
        <ProfileItem
          icon="information-circle-outline"
          title="About FinShare"
          subtitle="Version 1.0.0"
          onPress={() => Alert.alert('FinShare', 'Smart Group Expense Management App\nVersion 1.0.0\nBuilt with Expo React Native')}
        />
        <ProfileItem
          icon="help-circle-outline"
          title="Help & Support"
          subtitle="Get help and contact support"
          onPress={() => Alert.alert('Help & Support', 'Email: support@finshare.app\nPhone: +91-XXXXXXXXXX')}
        />
        <ProfileItem
          icon="document-text-outline"
          title="Privacy Policy"
          subtitle="Read our privacy policy"
          onPress={() => Alert.alert('Privacy Policy', 'View privacy policy at: https://finshare.app/privacy')}
        />
      </ProfileSection>

      <View style={styles.logoutSection}>
        <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
          <Ionicons name="log-out-outline" size={20} color="#FF3B30" />
          <Text style={styles.logoutText}>Logout</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.footer}>
        <Text style={styles.footerText}>
          Made with ❤️ for smart expense management
        </Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  header: {
    alignItems: 'center',
    padding: 30,
    paddingTop: 80,
    backgroundColor: 'white',
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: '#E3F2FD',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 15,
  },
  userName: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 5,
  },
  userPhone: {
    fontSize: 16,
    color: '#666',
  },
  section: {
    backgroundColor: 'white',
    marginTop: 20,
    paddingVertical: 10,
  },
  sectionTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#666',
    paddingHorizontal: 20,
    paddingBottom: 10,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  profileItem: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  itemLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  iconContainer: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: '#E3F2FD',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 15,
  },
  itemContent: {
    flex: 1,
  },
  itemTitle: {
    fontSize: 16,
    fontWeight: '500',
    color: '#333',
    marginBottom: 2,
  },
  itemSubtitle: {
    fontSize: 12,
    color: '#666',
  },
  logoutSection: {
    marginTop: 20,
    paddingHorizontal: 20,
  },
  logoutButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'white',
    padding: 15,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#FF3B30',
  },
  logoutText: {
    color: '#FF3B30',
    fontSize: 16,
    fontWeight: '600',
    marginLeft: 8,
  },
  footer: {
    padding: 30,
    alignItems: 'center',
  },
  footerText: {
    fontSize: 12,
    color: '#999',
    textAlign: 'center',
  },
});