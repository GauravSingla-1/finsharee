import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  RefreshControl,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_CONFIG } from '../../config';

export default function DashboardScreen({ navigation }) {
  const [dashboardData, setDashboardData] = useState({
    totalExpenses: 0,
    pendingSettlements: 0,
    activeGroups: 0,
    recentTransactions: [],
  });
  const [refreshing, setRefreshing] = useState(false);
  const [userName, setUserName] = useState('');

  useEffect(() => {
    loadDashboardData();
    loadUserName();
  }, []);

  const loadUserName = async () => {
    try {
      const userId = await AsyncStorage.getItem('userId');
      setUserName(userId ? `User ${userId.slice(-4)}` : 'User');
    } catch (error) {
      console.error('Error loading user name:', error);
    }
  };

  const loadDashboardData = async () => {
    try {
      const userId = await AsyncStorage.getItem('userId');
      
      // Try to fetch from backend
      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.DASHBOARD}`, {
        headers: {
          'X-Authenticated-User-ID': userId,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setDashboardData(data);
      } else {
        // Demo data if backend not available
        setDashboardData({
          totalExpenses: 2450.75,
          pendingSettlements: 3,
          activeGroups: 4,
          recentTransactions: [
            {
              id: '1',
              description: 'Team Lunch',
              amount: 450.00,
              type: 'expense',
              date: '2025-06-22',
            },
            {
              id: '2',
              description: 'Movie Night',
              amount: 280.00,
              type: 'expense',
              date: '2025-06-21',
            },
            {
              id: '3',
              description: 'Settlement from John',
              amount: 150.00,
              type: 'settlement',
              date: '2025-06-20',
            },
          ],
        });
      }
    } catch (error) {
      console.error('Error loading dashboard data:', error);
      // Use demo data
      setDashboardData({
        totalExpenses: 2450.75,
        pendingSettlements: 3,
        activeGroups: 4,
        recentTransactions: [],
      });
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await loadDashboardData();
    setRefreshing(false);
  };

  const QuickActionCard = ({ icon, title, onPress, color = '#007AFF' }) => (
    <TouchableOpacity style={styles.quickActionCard} onPress={onPress}>
      <Ionicons name={icon} size={30} color={color} />
      <Text style={styles.quickActionText}>{title}</Text>
    </TouchableOpacity>
  );

  const StatCard = ({ title, value, icon, color = '#007AFF' }) => (
    <View style={styles.statCard}>
      <Ionicons name={icon} size={24} color={color} />
      <Text style={styles.statValue}>{value}</Text>
      <Text style={styles.statTitle}>{title}</Text>
    </View>
  );

  return (
    <ScrollView
      style={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
      }
    >
      <View style={styles.header}>
        <Text style={styles.welcomeText}>Welcome back,</Text>
        <Text style={styles.userName}>{userName}</Text>
      </View>

      <View style={styles.statsContainer}>
        <StatCard
          title="Total Expenses"
          value={`₹${dashboardData.totalExpenses}`}
          icon="wallet"
          color="#4CAF50"
        />
        <StatCard
          title="Pending"
          value={dashboardData.pendingSettlements}
          icon="time"
          color="#FF9800"
        />
        <StatCard
          title="Active Groups"
          value={dashboardData.activeGroups}
          icon="people"
          color="#2196F3"
        />
      </View>

      <View style={styles.quickActionsContainer}>
        <Text style={styles.sectionTitle}>Quick Actions</Text>
        <View style={styles.quickActionsGrid}>
          <QuickActionCard
            icon="camera"
            title="Scan Receipt"
            onPress={() => navigation.navigate('ReceiptScan')}
            color="#4CAF50"
          />
          <QuickActionCard
            icon="add"
            title="Add Expense"
            onPress={() => navigation.navigate('CreateExpense')}
            color="#2196F3"
          />
          <QuickActionCard
            icon="people"
            title="Create Group"
            onPress={() => navigation.navigate('CreateGroup')}
            color="#FF9800"
          />
          <QuickActionCard
            icon="brain"
            title="AI Assistant"
            onPress={() => navigation.navigate('AI Co-Pilot')}
            color="#9C27B0"
          />
        </View>
      </View>

      <View style={styles.recentTransactionsContainer}>
        <Text style={styles.sectionTitle}>Recent Activity</Text>
        {dashboardData.recentTransactions.map((transaction) => (
          <View key={transaction.id} style={styles.transactionItem}>
            <Ionicons
              name={transaction.type === 'expense' ? 'receipt' : 'arrow-down'}
              size={20}
              color={transaction.type === 'expense' ? '#FF5722' : '#4CAF50'}
            />
            <View style={styles.transactionDetails}>
              <Text style={styles.transactionDescription}>
                {transaction.description}
              </Text>
              <Text style={styles.transactionDate}>{transaction.date}</Text>
            </View>
            <Text
              style={[
                styles.transactionAmount,
                {
                  color: transaction.type === 'expense' ? '#FF5722' : '#4CAF50',
                },
              ]}
            >
              {transaction.type === 'expense' ? '-' : '+'}₹{transaction.amount}
            </Text>
          </View>
        ))}
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
    padding: 20,
    paddingTop: 60,
    backgroundColor: 'white',
  },
  welcomeText: {
    fontSize: 16,
    color: '#666',
  },
  userName: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
  },
  statsContainer: {
    flexDirection: 'row',
    paddingHorizontal: 20,
    paddingVertical: 10,
    justifyContent: 'space-between',
  },
  statCard: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 15,
    alignItems: 'center',
    flex: 1,
    marginHorizontal: 5,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  statValue: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginTop: 5,
  },
  statTitle: {
    fontSize: 12,
    color: '#666',
    textAlign: 'center',
  },
  quickActionsContainer: {
    padding: 20,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  quickActionsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  quickActionCard: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    alignItems: 'center',
    width: '47%',
    marginBottom: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  quickActionText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
    marginTop: 8,
    textAlign: 'center',
  },
  recentTransactionsContainer: {
    padding: 20,
  },
  transactionItem: {
    backgroundColor: 'white',
    borderRadius: 8,
    padding: 15,
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  transactionDetails: {
    flex: 1,
    marginLeft: 15,
  },
  transactionDescription: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
  },
  transactionDate: {
    fontSize: 12,
    color: '#666',
    marginTop: 2,
  },
  transactionAmount: {
    fontSize: 16,
    fontWeight: 'bold',
  },
});