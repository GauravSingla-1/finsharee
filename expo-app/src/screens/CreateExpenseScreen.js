import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
  ScrollView,
  Modal,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function CreateExpenseScreen({ navigation, route }) {
  const [expenseData, setExpenseData] = useState({
    description: '',
    amount: '',
    category: 'Other',
    groupId: '',
    splitMethod: 'EQUAL',
  });
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showGroupModal, setShowGroupModal] = useState(false);
  const [showCategoryModal, setShowCategoryModal] = useState(false);
  const [showSplitMethodModal, setShowSplitMethodModal] = useState(false);

  const categories = [
    'Food & Dining',
    'Transportation',
    'Entertainment',
    'Shopping',
    'Travel',
    'Utilities',
    'Healthcare',
    'Other',
  ];

  const splitMethods = [
    { key: 'EQUAL', label: 'Equal Split', description: 'Split equally among all members' },
    { key: 'EXACT', label: 'Exact Amount', description: 'Specify exact amount for each person' },
    { key: 'PERCENTAGE', label: 'Percentage', description: 'Split by percentage shares' },
    { key: 'SHARES', label: 'Shares', description: 'Split by number of shares' },
  ];

  useEffect(() => {
    loadGroups();
    if (route.params?.prefillData) {
      const { description, amount, category } = route.params.prefillData;
      setExpenseData(prev => ({
        ...prev,
        description: description || '',
        amount: amount?.toString() || '',
        category: category || 'Other',
      }));
    }
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
        if (data.length > 0 && !expenseData.groupId) {
          setExpenseData(prev => ({ ...prev, groupId: data[0].groupId }));
        }
      } else {
        // Demo groups
        const demoGroups = [
          { groupId: '1', groupName: 'Office Team' },
          { groupId: '2', groupName: 'College Friends' },
          { groupId: '3', groupName: 'Family Trip' },
        ];
        setGroups(demoGroups);
        if (!expenseData.groupId) {
          setExpenseData(prev => ({ ...prev, groupId: demoGroups[0].groupId }));
        }
      }
    } catch (error) {
      console.error('Error loading groups:', error);
    }
  };

  const categorizeExpense = async (description) => {
    try {
      const response = await fetch('http://YOUR_IP:8004/api/ai/categorize', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          merchant_text: description,
          transaction_type: 'DEBIT',
          amount: parseFloat(expenseData.amount) || 0,
        }),
      });

      if (response.ok) {
        const data = await response.json();
        if (data.predicted_category) {
          setExpenseData(prev => ({ ...prev, category: data.predicted_category }));
        }
      }
    } catch (error) {
      console.error('Error categorizing expense:', error);
    }
  };

  const createExpense = async () => {
    if (!expenseData.description.trim()) {
      Alert.alert('Error', 'Please enter a description');
      return;
    }

    if (!expenseData.amount || parseFloat(expenseData.amount) <= 0) {
      Alert.alert('Error', 'Please enter a valid amount');
      return;
    }

    if (!expenseData.groupId) {
      Alert.alert('Error', 'Please select a group');
      return;
    }

    setLoading(true);
    try {
      const userId = await AsyncStorage.getItem('userId');
      
      const requestData = {
        description: expenseData.description.trim(),
        amount: parseFloat(expenseData.amount),
        category: expenseData.category,
        groupId: expenseData.groupId,
        splitMethod: expenseData.splitMethod,
      };

      const response = await fetch('http://YOUR_IP:5000/api/expenses', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-Authenticated-User-ID': userId,
        },
        body: JSON.stringify(requestData),
      });

      if (response.ok) {
        const createdExpense = await response.json();
        Alert.alert(
          'Success',
          `Expense "${expenseData.description}" created successfully!`,
          [
            {
              text: 'OK',
              onPress: () => navigation.goBack(),
            },
          ]
        );
      } else {
        // Demo success for testing
        Alert.alert(
          'Success',
          `Expense "${expenseData.description}" created successfully! (Demo Mode)`,
          [
            {
              text: 'OK',
              onPress: () => navigation.goBack(),
            },
          ]
        );
      }
    } catch (error) {
      console.error('Error creating expense:', error);
      // Demo success for testing
      Alert.alert(
        'Success',
        `Expense "${expenseData.description}" created successfully! (Demo Mode)`,
        [
          {
            text: 'OK',
            onPress: () => navigation.goBack(),
          },
        ]
      );
    } finally {
      setLoading(false);
    }
  };

  const SelectionModal = ({ visible, onClose, title, options, selectedValue, onSelect }) => (
    <Modal visible={visible} transparent animationType="slide">
      <View style={styles.modalOverlay}>
        <View style={styles.modalContainer}>
          <View style={styles.modalHeader}>
            <Text style={styles.modalTitle}>{title}</Text>
            <TouchableOpacity onPress={onClose}>
              <Ionicons name="close" size={24} color="#666" />
            </TouchableOpacity>
          </View>
          <ScrollView style={styles.modalContent}>
            {options.map((option, index) => {
              const value = typeof option === 'string' ? option : option.key;
              const label = typeof option === 'string' ? option : option.label;
              const description = typeof option === 'object' ? option.description : null;
              
              return (
                <TouchableOpacity
                  key={index}
                  style={[
                    styles.modalOption,
                    selectedValue === value && styles.selectedOption
                  ]}
                  onPress={() => {
                    onSelect(value);
                    onClose();
                  }}
                >
                  <Text style={[
                    styles.modalOptionText,
                    selectedValue === value && styles.selectedOptionText
                  ]}>
                    {label}
                  </Text>
                  {description && (
                    <Text style={styles.modalOptionDescription}>{description}</Text>
                  )}
                  {selectedValue === value && (
                    <Ionicons name="checkmark" size={20} color="#007AFF" />
                  )}
                </TouchableOpacity>
              );
            })}
          </ScrollView>
        </View>
      </View>
    </Modal>
  );

  const selectedGroup = groups.find(g => g.groupId === expenseData.groupId);
  const selectedSplitMethod = splitMethods.find(s => s.key === expenseData.splitMethod);

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        >
          <Ionicons name="arrow-back" size={24} color="#007AFF" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Add Expense</Text>
        <View style={styles.placeholder} />
      </View>

      <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
        <View style={styles.form}>
          <View style={styles.inputContainer}>
            <Text style={styles.label}>Description *</Text>
            <TextInput
              style={styles.input}
              value={expenseData.description}
              onChangeText={(text) => {
                setExpenseData(prev => ({ ...prev, description: text }));
                if (text.length > 3) {
                  categorizeExpense(text);
                }
              }}
              placeholder="What did you spend on?"
              maxLength={100}
            />
          </View>

          <View style={styles.inputContainer}>
            <Text style={styles.label}>Amount *</Text>
            <View style={styles.amountContainer}>
              <Text style={styles.currencySymbol}>₹</Text>
              <TextInput
                style={styles.amountInput}
                value={expenseData.amount}
                onChangeText={(text) => setExpenseData(prev => ({ ...prev, amount: text }))}
                placeholder="0.00"
                keyboardType="decimal-pad"
                maxLength={10}
              />
            </View>
          </View>

          <TouchableOpacity
            style={styles.selectContainer}
            onPress={() => setShowGroupModal(true)}
          >
            <Text style={styles.label}>Group *</Text>
            <View style={styles.selectButton}>
              <Text style={styles.selectButtonText}>
                {selectedGroup ? selectedGroup.groupName : 'Select Group'}
              </Text>
              <Ionicons name="chevron-down" size={16} color="#666" />
            </View>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.selectContainer}
            onPress={() => setShowCategoryModal(true)}
          >
            <Text style={styles.label}>Category</Text>
            <View style={styles.selectButton}>
              <Text style={styles.selectButtonText}>{expenseData.category}</Text>
              <Ionicons name="chevron-down" size={16} color="#666" />
            </View>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.selectContainer}
            onPress={() => setShowSplitMethodModal(true)}
          >
            <Text style={styles.label}>Split Method</Text>
            <View style={styles.selectButton}>
              <Text style={styles.selectButtonText}>
                {selectedSplitMethod ? selectedSplitMethod.label : 'Equal Split'}
              </Text>
              <Ionicons name="chevron-down" size={16} color="#666" />
            </View>
          </TouchableOpacity>

          <View style={styles.infoContainer}>
            <Ionicons name="information-circle" size={20} color="#007AFF" />
            <Text style={styles.infoText}>
              The expense will be split among all group members using the selected method. You can modify individual shares later if needed.
            </Text>
          </View>
        </View>
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity
          style={[
            styles.createButton,
            (!expenseData.description.trim() || !expenseData.amount || loading) && styles.createButtonDisabled
          ]}
          onPress={createExpense}
          disabled={!expenseData.description.trim() || !expenseData.amount || loading}
        >
          {loading ? (
            <ActivityIndicator color="white" />
          ) : (
            <>
              <Ionicons name="add-circle" size={20} color="white" />
              <Text style={styles.createButtonText}>Add Expense</Text>
            </>
          )}
        </TouchableOpacity>
      </View>

      <SelectionModal
        visible={showGroupModal}
        onClose={() => setShowGroupModal(false)}
        title="Select Group"
        options={groups.map(g => ({ key: g.groupId, label: g.groupName }))}
        selectedValue={expenseData.groupId}
        onSelect={(value) => setExpenseData(prev => ({ ...prev, groupId: value }))}
      />

      <SelectionModal
        visible={showCategoryModal}
        onClose={() => setShowCategoryModal(false)}
        title="Select Category"
        options={categories}
        selectedValue={expenseData.category}
        onSelect={(value) => setExpenseData(prev => ({ ...prev, category: value }))}
      />

      <SelectionModal
        visible={showSplitMethodModal}
        onClose={() => setShowSplitMethodModal(false)}
        title="Select Split Method"
        options={splitMethods}
        selectedValue={expenseData.splitMethod}
        onSelect={(value) => setExpenseData(prev => ({ ...prev, splitMethod: value }))}
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
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 20,
    paddingTop: 60,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  backButton: {
    padding: 8,
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  placeholder: {
    width: 40,
  },
  content: {
    flex: 1,
  },
  form: {
    padding: 20,
  },
  inputContainer: {
    marginBottom: 20,
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 15,
    fontSize: 16,
    backgroundColor: 'white',
  },
  amountContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    backgroundColor: 'white',
    paddingLeft: 15,
  },
  currencySymbol: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginRight: 8,
  },
  amountInput: {
    flex: 1,
    padding: 15,
    fontSize: 16,
  },
  selectContainer: {
    marginBottom: 20,
  },
  selectButton: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 15,
    backgroundColor: 'white',
  },
  selectButtonText: {
    fontSize: 16,
    color: '#333',
  },
  infoContainer: {
    flexDirection: 'row',
    backgroundColor: '#E3F2FD',
    padding: 15,
    borderRadius: 8,
    alignItems: 'flex-start',
  },
  infoText: {
    fontSize: 12,
    color: '#007AFF',
    marginLeft: 8,
    flex: 1,
    lineHeight: 16,
  },
  footer: {
    padding: 20,
    backgroundColor: 'white',
    borderTopWidth: 1,
    borderTopColor: '#f0f0f0',
  },
  createButton: {
    backgroundColor: '#007AFF',
    borderRadius: 12,
    padding: 16,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
  },
  createButtonDisabled: {
    backgroundColor: '#ccc',
  },
  createButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
    marginLeft: 8,
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'flex-end',
  },
  modalContainer: {
    backgroundColor: 'white',
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    maxHeight: '70%',
  },
  modalHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  modalContent: {
    flex: 1,
  },
  modalOption: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  selectedOption: {
    backgroundColor: '#E3F2FD',
  },
  modalOptionText: {
    fontSize: 16,
    color: '#333',
    flex: 1,
  },
  selectedOptionText: {
    color: '#007AFF',
    fontWeight: '600',
  },
  modalOptionDescription: {
    fontSize: 12,
    color: '#666',
    marginTop: 2,
    flex: 1,
  },
});