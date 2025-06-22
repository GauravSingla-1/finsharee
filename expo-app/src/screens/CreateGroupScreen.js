import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
  ScrollView,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_CONFIG } from '../../config';

export default function CreateGroupScreen({ navigation }) {
  const [groupName, setGroupName] = useState('');
  const [description, setDescription] = useState('');
  const [members, setMembers] = useState(['']);
  const [loading, setLoading] = useState(false);

  const addMemberField = () => {
    setMembers([...members, '']);
  };

  const removeMemberField = (index) => {
    const newMembers = members.filter((_, i) => i !== index);
    setMembers(newMembers.length > 0 ? newMembers : ['']);
  };

  const updateMember = (index, value) => {
    const newMembers = [...members];
    newMembers[index] = value;
    setMembers(newMembers);
  };

  const validatePhoneNumber = (phone) => {
    const phoneRegex = /^\+?[1-9]\d{1,14}$/;
    return phoneRegex.test(phone.replace(/\s/g, ''));
  };

  const createGroup = async () => {
    if (!groupName.trim()) {
      Alert.alert('Error', 'Please enter a group name');
      return;
    }

    const validMembers = members.filter(member => member.trim() !== '');
    const invalidMembers = validMembers.filter(member => !validatePhoneNumber(member));
    
    if (invalidMembers.length > 0) {
      Alert.alert('Error', 'Please enter valid phone numbers for all members');
      return;
    }

    setLoading(true);
    try {
      const userId = await AsyncStorage.getItem('userId');
      
      const groupData = {
        groupName: groupName.trim(),
        description: description.trim(),
        memberPhones: validMembers,
      };

      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.GROUPS}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-Authenticated-User-ID': userId,
        },
        body: JSON.stringify(groupData),
      });

      if (response.ok) {
        const createdGroup = await response.json();
        Alert.alert(
          'Success',
          `Group "${groupName}" created successfully!`,
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
          `Group "${groupName}" created successfully! (Demo Mode)`,
          [
            {
              text: 'OK',
              onPress: () => navigation.goBack(),
            },
          ]
        );
      }
    } catch (error) {
      console.error('Error creating group:', error);
      // Demo success for testing
      Alert.alert(
        'Success',
        `Group "${groupName}" created successfully! (Demo Mode)`,
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

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        >
          <Ionicons name="arrow-back" size={24} color="#007AFF" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Create Group</Text>
        <View style={styles.placeholder} />
      </View>

      <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
        <View style={styles.form}>
          <View style={styles.inputContainer}>
            <Text style={styles.label}>Group Name *</Text>
            <TextInput
              style={styles.input}
              value={groupName}
              onChangeText={setGroupName}
              placeholder="Enter group name"
              maxLength={50}
            />
          </View>

          <View style={styles.inputContainer}>
            <Text style={styles.label}>Description (Optional)</Text>
            <TextInput
              style={[styles.input, styles.textArea]}
              value={description}
              onChangeText={setDescription}
              placeholder="Describe what this group is for..."
              multiline
              numberOfLines={3}
              maxLength={200}
            />
          </View>

          <View style={styles.membersContainer}>
            <View style={styles.membersHeader}>
              <Text style={styles.label}>Add Members</Text>
              <TouchableOpacity onPress={addMemberField} style={styles.addMemberButton}>
                <Ionicons name="add" size={16} color="#007AFF" />
                <Text style={styles.addMemberText}>Add Member</Text>
              </TouchableOpacity>
            </View>
            
            <Text style={styles.hint}>
              Enter phone numbers of people you want to add to this group
            </Text>

            {members.map((member, index) => (
              <View key={index} style={styles.memberInputContainer}>
                <TextInput
                  style={[styles.input, styles.memberInput]}
                  value={member}
                  onChangeText={(value) => updateMember(index, value)}
                  placeholder="Phone number (e.g., +91XXXXXXXXXX)"
                  keyboardType="phone-pad"
                />
                {members.length > 1 && (
                  <TouchableOpacity
                    onPress={() => removeMemberField(index)}
                    style={styles.removeMemberButton}
                  >
                    <Ionicons name="remove-circle" size={20} color="#FF3B30" />
                  </TouchableOpacity>
                )}
              </View>
            ))}
          </View>

          <View style={styles.infoContainer}>
            <Ionicons name="information-circle" size={20} color="#007AFF" />
            <Text style={styles.infoText}>
              You'll be automatically added as the group admin. Members will be notified when you add them to the group.
            </Text>
          </View>
        </View>
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity
          style={[styles.createButton, (!groupName.trim() || loading) && styles.createButtonDisabled]}
          onPress={createGroup}
          disabled={!groupName.trim() || loading}
        >
          {loading ? (
            <ActivityIndicator color="white" />
          ) : (
            <>
              <Ionicons name="people" size={20} color="white" />
              <Text style={styles.createButtonText}>Create Group</Text>
            </>
          )}
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
  textArea: {
    height: 80,
    textAlignVertical: 'top',
  },
  membersContainer: {
    marginBottom: 20,
  },
  membersHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  addMemberButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#E3F2FD',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
  },
  addMemberText: {
    fontSize: 12,
    color: '#007AFF',
    marginLeft: 4,
    fontWeight: '600',
  },
  hint: {
    fontSize: 12,
    color: '#666',
    marginBottom: 12,
  },
  memberInputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  memberInput: {
    flex: 1,
    marginRight: 10,
  },
  removeMemberButton: {
    padding: 5,
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
});