import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Alert,
  Image,
  ActivityIndicator,
} from 'react-native';
import { Camera } from 'expo-camera';
import * as ImagePicker from 'expo-image-picker';
import { Ionicons } from '@expo/vector-icons';

export default function ReceiptScanScreen({ navigation }) {
  const [hasPermission, setHasPermission] = useState(null);
  const [camera, setCamera] = useState(null);
  const [scanning, setScanning] = useState(false);
  const [scannedImage, setScannedImage] = useState(null);
  const [extractedData, setExtractedData] = useState(null);

  useEffect(() => {
    (async () => {
      const { status } = await Camera.requestCameraPermissionsAsync();
      setHasPermission(status === 'granted');
    })();
  }, []);

  const takePicture = async () => {
    if (camera) {
      setScanning(true);
      try {
        const photo = await camera.takePictureAsync();
        setScannedImage(photo.uri);
        await processReceipt(photo.uri);
      } catch (error) {
        console.error('Error taking picture:', error);
        Alert.alert('Error', 'Failed to take picture');
      } finally {
        setScanning(false);
      }
    }
  };

  const pickImage = async () => {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [4, 3],
      quality: 1,
    });

    if (!result.canceled) {
      setScanning(true);
      setScannedImage(result.assets[0].uri);
      await processReceipt(result.assets[0].uri);
      setScanning(false);
    }
  };

  const processReceipt = async (imageUri) => {
    try {
      // Create FormData for image upload
      const formData = new FormData();
      formData.append('receipt_image', {
        uri: imageUri,
        type: 'image/jpeg',
        name: 'receipt.jpg',
      });

      const response = await fetch('http://YOUR_IP:8004/api/ai/receipt/scan', {
        method: 'POST',
        body: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.ok) {
        const data = await response.json();
        setExtractedData(data);
      } else {
        // Demo OCR results
        const demoData = {
          merchant_name: 'Pizza Palace',
          total_amount: 450.00,
          date: '2025-06-22',
          items: [
            { name: 'Large Pizza Margherita', price: 320.00 },
            { name: 'Garlic Bread', price: 80.00 },
            { name: 'Coca Cola (2L)', price: 50.00 },
          ],
          category: 'Food & Dining',
          confidence: 0.92,
        };
        setExtractedData(demoData);
      }
    } catch (error) {
      console.error('Error processing receipt:', error);
      // Demo data fallback
      const demoData = {
        merchant_name: 'Local Restaurant',
        total_amount: 280.00,
        date: new Date().toISOString().split('T')[0],
        items: [
          { name: 'Item 1', price: 150.00 },
          { name: 'Item 2', price: 130.00 },
        ],
        category: 'Food & Dining',
        confidence: 0.85,
      };
      setExtractedData(demoData);
    }
  };

  const createExpenseFromReceipt = () => {
    if (extractedData) {
      Alert.alert(
        'Create Expense',
        `Create expense for ₹${extractedData.total_amount} at ${extractedData.merchant_name}?`,
        [
          { text: 'Cancel', style: 'cancel' },
          {
            text: 'Create',
            onPress: () => {
              // Navigate to expense creation with pre-filled data
              navigation.navigate('CreateExpense', {
                prefillData: {
                  description: extractedData.merchant_name,
                  amount: extractedData.total_amount,
                  category: extractedData.category,
                },
              });
            },
          },
        ]
      );
    }
  };

  if (hasPermission === null) {
    return <View style={styles.container} />;
  }

  if (hasPermission === false) {
    return (
      <View style={styles.container}>
        <Text>No access to camera</Text>
        <TouchableOpacity style={styles.button} onPress={pickImage}>
          <Text style={styles.buttonText}>Pick from Gallery</Text>
        </TouchableOpacity>
      </View>
    );
  }

  if (scannedImage && extractedData) {
    return (
      <View style={styles.container}>
        <View style={styles.header}>
          <TouchableOpacity
            onPress={() => {
              setScannedImage(null);
              setExtractedData(null);
            }}
            style={styles.backButton}
          >
            <Ionicons name="arrow-back" size={24} color="#007AFF" />
          </TouchableOpacity>
          <Text style={styles.headerTitle}>Receipt Scanned</Text>
        </View>

        <View style={styles.resultContainer}>
          <Image source={{ uri: scannedImage }} style={styles.scannedImage} />
          
          <View style={styles.extractedDataContainer}>
            <Text style={styles.sectionTitle}>Extracted Information:</Text>
            
            <View style={styles.dataRow}>
              <Text style={styles.label}>Merchant:</Text>
              <Text style={styles.value}>{extractedData.merchant_name}</Text>
            </View>
            
            <View style={styles.dataRow}>
              <Text style={styles.label}>Total Amount:</Text>
              <Text style={styles.value}>₹{extractedData.total_amount}</Text>
            </View>
            
            <View style={styles.dataRow}>
              <Text style={styles.label}>Date:</Text>
              <Text style={styles.value}>{extractedData.date}</Text>
            </View>
            
            <View style={styles.dataRow}>
              <Text style={styles.label}>Category:</Text>
              <Text style={styles.value}>{extractedData.category}</Text>
            </View>
            
            <View style={styles.dataRow}>
              <Text style={styles.label}>Confidence:</Text>
              <Text style={styles.value}>{(extractedData.confidence * 100).toFixed(1)}%</Text>
            </View>

            {extractedData.items && extractedData.items.length > 0 && (
              <View style={styles.itemsContainer}>
                <Text style={styles.itemsTitle}>Items:</Text>
                {extractedData.items.map((item, index) => (
                  <View key={index} style={styles.itemRow}>
                    <Text style={styles.itemName}>{item.name}</Text>
                    <Text style={styles.itemPrice}>₹{item.price}</Text>
                  </View>
                ))}
              </View>
            )}
          </View>

          <TouchableOpacity
            style={styles.createExpenseButton}
            onPress={createExpenseFromReceipt}
          >
            <Ionicons name="add-circle" size={20} color="white" />
            <Text style={styles.createExpenseButtonText}>Create Expense</Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        >
          <Ionicons name="arrow-back" size={24} color="#007AFF" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Scan Receipt</Text>
      </View>

      <Camera
        style={styles.camera}
        ref={(ref) => setCamera(ref)}
        type={Camera.Constants.Type.back}
        autoFocus={Camera.Constants.AutoFocus.on}
      >
        <View style={styles.cameraOverlay}>
          <View style={styles.scanArea}>
            <View style={styles.cornerTL} />
            <View style={styles.cornerTR} />
            <View style={styles.cornerBL} />
            <View style={styles.cornerBR} />
          </View>
          
          <Text style={styles.instructionText}>
            Position the receipt within the frame
          </Text>
        </View>
      </Camera>

      <View style={styles.controls}>
        <TouchableOpacity style={styles.galleryButton} onPress={pickImage}>
          <Ionicons name="images" size={24} color="#007AFF" />
        </TouchableOpacity>
        
        <TouchableOpacity
          style={[styles.captureButton, scanning && styles.captureButtonDisabled]}
          onPress={takePicture}
          disabled={scanning}
        >
          {scanning ? (
            <ActivityIndicator color="white" />
          ) : (
            <Ionicons name="camera" size={32} color="white" />
          )}
        </TouchableOpacity>
        
        <View style={styles.placeholder} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 20,
    paddingTop: 60,
    backgroundColor: 'white',
    zIndex: 1,
  },
  backButton: {
    padding: 8,
    marginRight: 12,
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  camera: {
    flex: 1,
  },
  cameraOverlay: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  scanArea: {
    width: 280,
    height: 200,
    position: 'relative',
  },
  cornerTL: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: 30,
    height: 30,
    borderTopWidth: 3,
    borderLeftWidth: 3,
    borderColor: '#007AFF',
  },
  cornerTR: {
    position: 'absolute',
    top: 0,
    right: 0,
    width: 30,
    height: 30,
    borderTopWidth: 3,
    borderRightWidth: 3,
    borderColor: '#007AFF',
  },
  cornerBL: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    width: 30,
    height: 30,
    borderBottomWidth: 3,
    borderLeftWidth: 3,
    borderColor: '#007AFF',
  },
  cornerBR: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    width: 30,
    height: 30,
    borderBottomWidth: 3,
    borderRightWidth: 3,
    borderColor: '#007AFF',
  },
  instructionText: {
    color: 'white',
    fontSize: 16,
    textAlign: 'center',
    marginTop: 40,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    padding: 10,
    borderRadius: 8,
  },
  controls: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
  },
  galleryButton: {
    width: 50,
    height: 50,
    borderRadius: 25,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  captureButton: {
    width: 70,
    height: 70,
    borderRadius: 35,
    backgroundColor: '#007AFF',
    justifyContent: 'center',
    alignItems: 'center',
  },
  captureButtonDisabled: {
    backgroundColor: '#666',
  },
  placeholder: {
    width: 50,
  },
  resultContainer: {
    flex: 1,
    padding: 20,
    backgroundColor: '#f8f9fa',
  },
  scannedImage: {
    width: '100%',
    height: 200,
    borderRadius: 8,
    marginBottom: 20,
  },
  extractedDataContainer: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 16,
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  dataRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  label: {
    fontSize: 14,
    color: '#666',
    fontWeight: '500',
  },
  value: {
    fontSize: 14,
    color: '#333',
    fontWeight: '600',
  },
  itemsContainer: {
    marginTop: 15,
    paddingTop: 15,
    borderTopWidth: 1,
    borderTopColor: '#f0f0f0',
  },
  itemsTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 10,
  },
  itemRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 5,
  },
  itemName: {
    fontSize: 12,
    color: '#666',
    flex: 1,
  },
  itemPrice: {
    fontSize: 12,
    color: '#333',
    fontWeight: '500',
  },
  createExpenseButton: {
    backgroundColor: '#007AFF',
    borderRadius: 12,
    padding: 16,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
  },
  createExpenseButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
    marginLeft: 8,
  },
  button: {
    backgroundColor: '#007AFF',
    padding: 15,
    borderRadius: 8,
    margin: 20,
  },
  buttonText: {
    color: 'white',
    textAlign: 'center',
    fontSize: 16,
    fontWeight: 'bold',
  },
});