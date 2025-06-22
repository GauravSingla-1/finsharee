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

export default function GroupsScreen({ navigation }) {
  const [groups, setGroups] = useState([]);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    loadGroups();
  }, []);

  const loadGroups = async () => {
    try {
      const userId = await AsyncStorage.getItem('userId');
      
      const response = await fetch(`http://YOUR_IP:5000/api/groups`, {
        headers: {
          'X-Authenticated-User-ID': userId,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setGroups(data);
      } else {
        // Demo data
        setGroups([
          {
            groupId: '1',
            groupName: 'Office Team',
            memberIds: ['user1', 'user2', 'user3'],
            createdAt: '2025-06-20',
            totalExpenses: 1250.00,
            pendingAmount: 420.00,
          },
          {
            groupId: '2',
            groupName: 'College Friends',
            memberIds: ['user1', 'user4', 'user5'],
            createdAt: '2025-06-18',
            totalExpenses: 850.00,
            pendingAmount: 0.00,
          },
          {
            groupId: '3',
            groupName: 'Family Trip',
            memberIds: ['user1', 'user6', 'user7', 'user8'],
            createdAt: '2025-06-15',
            totalExpenses: 3200.00,
            pendingAmount: 800.00,
          },
        ]);
      }
    } catch (error) {
      console.error('Error loading groups:', error);
      Alert.alert('Error', 'Failed to load groups');
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await loadGroups();
    setRefreshing(false);
  };

  const handleGroupPress = (group) => {
    Alert.alert(
      group.groupName,
      `Members: ${group.memberIds.length}\nTotal Expenses: ₹${group.totalExpenses}\nPending: ₹${group.pendingAmount}`,
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'View Details', onPress: () => console.log('View group details') },
      ]
    );
  };

  const renderGroupItem = ({ item }) => (
    <TouchableOpacity
      style={styles.groupCard}
      onPress={() => handleGroupPress(item)}
    >
      <View style={styles.groupHeader}>
        <View style={styles.groupIcon}>
          <Ionicons name="people" size={24} color="#007AFF" />
        </View>
        <View style={styles.groupInfo}>
          <Text style={styles.groupName}>{item.groupName}</Text>
          <Text style={styles.groupMembers}>
            {item.memberIds.length} members
          </Text>
        </View>
        <View style={styles.groupStats}>
          <Text style={styles.totalAmount}>₹{item.totalExpenses}</Text>
          {item.pendingAmount > 0 && (
            <Text style={styles.pendingAmount}>
              ₹{item.pendingAmount} pending
            </Text>
          )}
        </View>
      </View>
      
      <View style={styles.groupFooter}>
        <Text style={styles.createdDate}>
          Created {new Date(item.createdAt).toLocaleDateString()}
        </Text>
        <TouchableOpacity style={styles.actionButton}>
          <Ionicons name="add" size={16} color="#007AFF" />
          <Text style={styles.actionButtonText}>Add Expense</Text>
        </TouchableOpacity>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Groups</Text>
        <TouchableOpacity
          style={styles.createButton}
          onPress={() => navigation.navigate('CreateGroup')}
        >
          <Ionicons name="add" size={24} color="white" />
        </TouchableOpacity>
      </View>

      <FlatList
        data={groups}
        renderItem={renderGroupItem}
        keyExtractor={(item) => item.groupId}
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
  createButton: {
    backgroundColor: '#007AFF',
    borderRadius: 20,
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  listContainer: {
    padding: 20,
  },
  groupCard: {
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
  groupHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  groupIcon: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: '#E3F2FD',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  groupInfo: {
    flex: 1,
  },
  groupName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  groupMembers: {
    fontSize: 14,
    color: '#666',
  },
  groupStats: {
    alignItems: 'flex-end',
  },
  totalAmount: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  pendingAmount: {
    fontSize: 12,
    color: '#FF9800',
    marginTop: 2,
  },
  groupFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingTop: 12,
    borderTopWidth: 1,
    borderTopColor: '#f0f0f0',
  },
  createdDate: {
    fontSize: 12,
    color: '#666',
  },
  actionButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#E3F2FD',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
  },
  actionButtonText: {
    fontSize: 12,
    color: '#007AFF',
    marginLeft: 4,
    fontWeight: '600',
  },
});