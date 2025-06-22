import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  RefreshControl,
  Alert,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_CONFIG } from '../../config';

export default function ExpensesScreen({ navigation }) {
  const [expenses, setExpenses] = useState([]);
  const [refreshing, setRefreshing] = useState(false);
  const [filter, setFilter] = useState('all'); // all, pending, settled

  useEffect(() => {
    loadExpenses();
  }, []);

  const loadExpenses = async () => {
    try {
      const userId = await AsyncStorage.getItem('userId');
      
      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.EXPENSES}`, {
        headers: {
          'X-Authenticated-User-ID': userId,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setExpenses(data);
      } else {
        // Demo data
        setExpenses([
          {
            expenseId: '1',
            description: 'Team Lunch at Pizza Palace',
            amount: 450.00,
            category: 'Food & Dining',
            groupId: '1',
            groupName: 'Office Team',
            createdBy: 'user1',
            createdAt: '2025-06-22T10:30:00Z',
            splitMethod: 'EQUAL',
            yourShare: 150.00,
            status: 'pending',
          },
          {
            expenseId: '2',
            description: 'Movie Tickets',
            amount: 280.00,
            category: 'Entertainment',
            groupId: '2',
            groupName: 'College Friends',
            createdBy: 'user4',
            createdAt: '2025-06-21T19:15:00Z',
            splitMethod: 'EQUAL',
            yourShare: 93.33,
            status: 'settled',
          },
          {
            expenseId: '3',
            description: 'Grocery Shopping',
            amount: 1200.00,
            category: 'Shopping',
            groupId: '3',
            groupName: 'Family Trip',
            createdBy: 'user1',
            createdAt: '2025-06-20T14:20:00Z',
            splitMethod: 'EXACT',
            yourShare: 300.00,
            status: 'pending',
          },
        ]);
      }
    } catch (error) {
      console.error('Error loading expenses:', error);
      Alert.alert('Error', 'Failed to load expenses');
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await loadExpenses();
    setRefreshing(false);
  };

  const getCategoryIcon = (category) => {
    const icons = {
      'Food & Dining': 'restaurant',
      'Entertainment': 'film',
      'Shopping': 'bag',
      'Transportation': 'car',
      'Travel': 'airplane',
      'Utilities': 'flash',
      'Other': 'ellipse',
    };
    return icons[category] || 'ellipse';
  };

  const getCategoryColor = (category) => {
    const colors = {
      'Food & Dining': '#4CAF50',
      'Entertainment': '#FF9800',
      'Shopping': '#2196F3',
      'Transportation': '#9C27B0',
      'Travel': '#00BCD4',
      'Utilities': '#FFC107',
      'Other': '#607D8B',
    };
    return colors[category] || '#607D8B';
  };

  const filteredExpenses = expenses.filter((expense) => {
    if (filter === 'all') return true;
    return expense.status === filter;
  });

  const renderExpenseItem = ({ item }) => (
    <TouchableOpacity
      style={styles.expenseCard}
      onPress={() => {
        Alert.alert(
          item.description,
          `Amount: ₹${item.amount}\nYour Share: ₹${item.yourShare}\nGroup: ${item.groupName}\nStatus: ${item.status}`,
          [
            { text: 'OK' },
            { text: 'View Details', onPress: () => console.log('View details') },
          ]
        );
      }}
    >
      <View style={styles.expenseHeader}>
        <View style={[styles.categoryIcon, { backgroundColor: getCategoryColor(item.category) + '20' }]}>
          <Ionicons
            name={getCategoryIcon(item.category)}
            size={20}
            color={getCategoryColor(item.category)}
          />
        </View>
        <View style={styles.expenseInfo}>
          <Text style={styles.expenseDescription} numberOfLines={2}>
            {item.description}
          </Text>
          <Text style={styles.groupName}>{item.groupName}</Text>
        </View>
        <View style={styles.amountContainer}>
          <Text style={styles.totalAmount}>₹{item.amount}</Text>
          <Text style={styles.yourShare}>Your share: ₹{item.yourShare}</Text>
        </View>
      </View>

      <View style={styles.expenseFooter}>
        <View style={styles.expenseDetails}>
          <Text style={styles.category}>{item.category}</Text>
          <Text style={styles.splitMethod}>{item.splitMethod} split</Text>
        </View>
        <View style={styles.statusContainer}>
          <View style={[
            styles.statusBadge,
            { backgroundColor: item.status === 'settled' ? '#4CAF50' : '#FF9800' }
          ]}>
            <Text style={styles.statusText}>
              {item.status.toUpperCase()}
            </Text>
          </View>
        </View>
      </View>

      <Text style={styles.date}>
        {new Date(item.createdAt).toLocaleDateString()} at{' '}
        {new Date(item.createdAt).toLocaleTimeString([], {
          hour: '2-digit',
          minute: '2-digit'
        })}
      </Text>
    </TouchableOpacity>
  );

  const FilterButton = ({ title, isActive, onPress }) => (
    <TouchableOpacity
      style={[styles.filterButton, isActive && styles.activeFilterButton]}
      onPress={onPress}
    >
      <Text style={[styles.filterButtonText, isActive && styles.activeFilterButtonText]}>
        {title}
      </Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Expenses</Text>
        <TouchableOpacity
          style={styles.addButton}
          onPress={() => navigation.navigate('CreateExpense')}
        >
          <Ionicons name="add" size={24} color="white" />
        </TouchableOpacity>
      </View>

      <View style={styles.filterContainer}>
        <FilterButton
          title="All"
          isActive={filter === 'all'}
          onPress={() => setFilter('all')}
        />
        <FilterButton
          title="Pending"
          isActive={filter === 'pending'}
          onPress={() => setFilter('pending')}
        />
        <FilterButton
          title="Settled"
          isActive={filter === 'settled'}
          onPress={() => setFilter('settled')}
        />
      </View>

      <FlatList
        data={filteredExpenses}
        renderItem={renderExpenseItem}
        keyExtractor={(item) => item.expenseId}
        contentContainerStyle={styles.listContainer}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
        showsVerticalScrollIndicator={false}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    paddingTop: 60,
    backgroundColor: 'white',
  },
  headerTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#333',
  },
  addButton: {
    backgroundColor: '#007AFF',
    borderRadius: 20,
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  filterContainer: {
    flexDirection: 'row',
    paddingHorizontal: 20,
    paddingVertical: 10,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  filterButton: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
    marginRight: 10,
    backgroundColor: '#f0f0f0',
  },
  activeFilterButton: {
    backgroundColor: '#007AFF',
  },
  filterButtonText: {
    fontSize: 14,
    color: '#666',
    fontWeight: '600',
  },
  activeFilterButtonText: {
    color: 'white',
  },
  listContainer: {
    padding: 20,
  },
  expenseCard: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  expenseHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 12,
  },
  categoryIcon: {
    width: 40,
    height: 40,
    borderRadius: 20,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  expenseInfo: {
    flex: 1,
  },
  expenseDescription: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  groupName: {
    fontSize: 14,
    color: '#666',
  },
  amountContainer: {
    alignItems: 'flex-end',
  },
  totalAmount: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  yourShare: {
    fontSize: 12,
    color: '#666',
    marginTop: 2,
  },
  expenseFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  expenseDetails: {
    flex: 1,
  },
  category: {
    fontSize: 12,
    color: '#666',
    marginBottom: 2,
  },
  splitMethod: {
    fontSize: 12,
    color: '#666',
  },
  statusContainer: {
    alignItems: 'flex-end',
  },
  statusBadge: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
  },
  statusText: {
    fontSize: 10,
    color: 'white',
    fontWeight: 'bold',
  },
  date: {
    fontSize: 12,
    color: '#999',
  },
});