import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  FlatList,
  StyleSheet,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_CONFIG } from '../../config';

export default function AICoPilotScreen() {
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Welcome message
    setMessages([
      {
        id: '1',
        text: "Hi! I'm your FinShare AI assistant. I can help you with expense categorization, budget planning, and financial insights. What would you like to know?",
        isUser: false,
        timestamp: new Date(),
      },
    ]);
  }, []);

  const makeAuthenticatedRequest = async (endpoint, options = {}) => {
    const token = await AsyncStorage.getItem('authToken');
    const url = `${API_CONFIG.BASE_URL}${endpoint}`;
    
    const response = await fetch(url, {
      ...options,
      headers: {
        ...API_CONFIG.HEADERS,
        'Authorization': `Bearer ${token}`,
        ...options.headers,
      },
    });
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ detail: 'Network error' }));
      throw new Error(errorData.detail || `HTTP ${response.status}`);
    }
    
    return response.json();
  };

  const sendMessage = async () => {
    if (!inputText.trim()) return;

    const userMessage = {
      id: Date.now().toString(),
      text: inputText,
      isUser: true,
      timestamp: new Date(),
    };

    setMessages(prev => [...prev, userMessage]);
    const messageToSend = inputText;
    setInputText('');
    setLoading(true);

    try {
      const response = await makeAuthenticatedRequest(API_CONFIG.ENDPOINTS.AI_CHAT, {
        method: 'POST',
        body: JSON.stringify({
          message: messageToSend,
          conversation_history: messages.slice(-5).map(msg => ({
            role: msg.isUser ? 'user' : 'model',
            text: msg.text,
          })),
        }),
      });

      const aiMessage = {
        id: (Date.now() + 1).toString(),
        text: response.reply,
        isUser: false,
        timestamp: new Date(),
      };

      setMessages(prev => [...prev, aiMessage]);
    } catch (error) {
      console.error('Error sending message:', error);
      
      const errorMessage = {
        id: (Date.now() + 1).toString(),
        text: getAIResponse(inputText),
        isUser: false,
        timestamp: new Date(),
      };

      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const getAIResponse = (userInput) => {
    const input = userInput.toLowerCase();
    
    if (input.includes('budget') || input.includes('trip')) {
      return "I can help you create a budget! For a trip, consider these categories:\n\n• Transportation: ₹2,000-5,000\n• Accommodation: ₹1,500-3,000 per night\n• Food & Dining: ₹500-1,500 per day\n• Activities: ₹1,000-2,500\n• Shopping: ₹1,000-3,000\n\nWould you like me to create a detailed budget for a specific destination?";
    }
    
    if (input.includes('categorize') || input.includes('expense')) {
      return "I can automatically categorize your expenses! Here are the main categories I use:\n\n• Food & Dining\n• Transportation\n• Entertainment\n• Shopping\n• Utilities\n• Travel\n• Healthcare\n\nJust tell me the merchant name or transaction description, and I'll categorize it for you!";
    }
    
    if (input.includes('save') || input.includes('money')) {
      return "Here are some smart ways to save money:\n\n💡 Track all expenses in FinShare\n💡 Set monthly budget limits\n💡 Use the 50/30/20 rule (needs/wants/savings)\n💡 Split recurring expenses with roommates\n💡 Review spending patterns weekly\n\nWould you like me to analyze your current spending patterns?";
    }
    
    if (input.includes('split') || input.includes('group')) {
      return "FinShare offers 4 splitting methods:\n\n🟦 Equal Split: Divide equally among all members\n🟦 Exact Split: Specify exact amounts for each person\n🟦 Percentage Split: Split by percentage shares\n🟦 Shares Split: Split by number of shares\n\nWhich method would work best for your expense?";
    }
    
    return "I understand you're asking about: " + userInput + "\n\nI can help you with:\n• Expense categorization\n• Budget planning\n• Trip cost estimation\n• Spending analysis\n• Money-saving tips\n• Group expense splitting\n\nWhat specific area would you like to explore?";
  };

  const renderQuickActions = () => (
    <View style={styles.quickActionsContainer}>
      <Text style={styles.quickActionsTitle}>Quick Actions:</Text>
      <View style={styles.quickActionsGrid}>
        {[
          'Categorize expense',
          'Plan trip budget',
          'Saving tips',
          'Split methods',
        ].map((action, index) => (
          <TouchableOpacity
            key={index}
            style={styles.quickActionButton}
            onPress={() => setInputText(action)}
          >
            <Text style={styles.quickActionText}>{action}</Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );

  const renderMessage = ({ item }) => (
    <View style={[
      styles.messageContainer,
      item.isUser ? styles.userMessage : styles.aiMessage
    ]}>
      <View style={[
        styles.messageBubble,
        item.isUser ? styles.userBubble : styles.aiBubble
      ]}>
        <Text style={[
          styles.messageText,
          item.isUser ? styles.userMessageText : styles.aiMessageText
        ]}>
          {item.text}
        </Text>
        <Text style={[
          styles.timestamp,
          item.isUser ? styles.userTimestamp : styles.aiTimestamp
        ]}>
          {item.timestamp.toLocaleTimeString([], {
            hour: '2-digit',
            minute: '2-digit'
          })}
        </Text>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Ionicons name="bulb" size={24} color="#007AFF" />
        <Text style={styles.headerTitle}>AI Co-Pilot</Text>
        <TouchableOpacity
          onPress={() => setMessages(messages.slice(0, 1))}
          style={styles.clearButton}
        >
          <Ionicons name="refresh" size={20} color="#666" />
        </TouchableOpacity>
      </View>

      {messages.length === 1 && renderQuickActions()}

      <FlatList
        data={messages}
        renderItem={renderMessage}
        keyExtractor={(item) => item.id}
        style={styles.messagesList}
        contentContainerStyle={styles.messagesContainer}
        showsVerticalScrollIndicator={false}
      />

      {loading && (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="small" color="#007AFF" />
          <Text style={styles.loadingText}>AI is thinking...</Text>
        </View>
      )}

      <View style={styles.inputContainer}>
        <TextInput
          style={styles.textInput}
          value={inputText}
          onChangeText={setInputText}
          placeholder="Ask me anything about expenses..."
          multiline
          maxLength={500}
        />
        <TouchableOpacity
          style={[styles.sendButton, !inputText.trim() && styles.sendButtonDisabled]}
          onPress={sendMessage}
          disabled={loading || !inputText.trim()}
        >
          <Ionicons name="send" size={20} color="white" />
        </TouchableOpacity>
      </View>
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
    padding: 20,
    paddingTop: 60,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginLeft: 10,
    flex: 1,
  },
  clearButton: {
    padding: 8,
  },
  quickActionsContainer: {
    padding: 20,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  quickActionsTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 10,
  },
  quickActionsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  quickActionButton: {
    backgroundColor: '#E3F2FD',
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 16,
    marginRight: 8,
    marginBottom: 8,
  },
  quickActionText: {
    fontSize: 12,
    color: '#007AFF',
    fontWeight: '500',
  },
  messagesList: {
    flex: 1,
  },
  messagesContainer: {
    padding: 20,
  },
  messageContainer: {
    marginBottom: 16,
  },
  userMessage: {
    alignItems: 'flex-end',
  },
  aiMessage: {
    alignItems: 'flex-start',
  },
  messageBubble: {
    maxWidth: '80%',
    padding: 12,
    borderRadius: 16,
  },
  userBubble: {
    backgroundColor: '#007AFF',
    borderBottomRightRadius: 4,
  },
  aiBubble: {
    backgroundColor: 'white',
    borderBottomLeftRadius: 4,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  messageText: {
    fontSize: 16,
    lineHeight: 20,
  },
  userMessageText: {
    color: 'white',
  },
  aiMessageText: {
    color: '#333',
  },
  timestamp: {
    fontSize: 11,
    marginTop: 4,
  },
  userTimestamp: {
    color: 'rgba(255, 255, 255, 0.7)',
  },
  aiTimestamp: {
    color: '#999',
  },
  loadingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 10,
  },
  loadingText: {
    marginLeft: 8,
    color: '#666',
    fontSize: 14,
  },
  inputContainer: {
    flexDirection: 'row',
    padding: 20,
    backgroundColor: 'white',
    borderTopWidth: 1,
    borderTopColor: '#f0f0f0',
    alignItems: 'flex-end',
  },
  textInput: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 20,
    paddingHorizontal: 16,
    paddingVertical: 12,
    fontSize: 16,
    maxHeight: 100,
    marginRight: 12,
    backgroundColor: '#f8f9fa',
  },
  sendButton: {
    backgroundColor: '#007AFF',
    borderRadius: 20,
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  sendButtonDisabled: {
    backgroundColor: '#ccc',
  },
});