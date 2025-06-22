package com.finshare.android.presentation.screens.receipt

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.finshare.android.data.camera.ReceiptScannerManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ReceiptScanScreen(
    navController: NavController,
    viewModel: ReceiptScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Receipt") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                !cameraPermission.status.isGranted -> {
                    PermissionRequiredContent(
                        onRequestPermission = { cameraPermission.launchPermissionRequest() }
                    )
                }
                uiState.scanResult != null -> {
                    ScanResultContent(
                        scanResult = uiState.scanResult,
                        onCreateExpense = { /* Navigate to create expense */ },
                        onScanAgain = { viewModel.clearResult() }
                    )
                }
                else -> {
                    CameraContent(
                        modifier = Modifier.weight(1f),
                        onCaptureClick = { viewModel.captureReceipt() },
                        onGalleryClick = { viewModel.openGallery() },
                        isProcessing = uiState.isProcessing,
                        lifecycleOwner = lifecycleOwner,
                        onCameraReady = { previewView ->
                            viewModel.setupCamera(lifecycleOwner, previewView)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRequiredContent(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "To scan receipts, we need access to your camera. This helps you quickly add expenses by taking photos of receipts.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grant Camera Permission")
        }
    }
}

@Composable
private fun CameraContent(
    modifier: Modifier = Modifier,
    onCaptureClick: () -> Unit,
    onGalleryClick: () -> Unit,
    isProcessing: Boolean,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onCameraReady: (PreviewView) -> Unit
) {
    Box(modifier = modifier) {
        // Camera Preview
        AndroidView(
            factory = { context ->
                PreviewView(context).also { previewView ->
                    onCameraReady(previewView)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Overlay with instructions
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Text(
                text = "Position the receipt in the frame and tap capture",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
        
        // Bottom controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery button
            FloatingActionButton(
                onClick = onGalleryClick,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
            }
            
            // Capture button
            FloatingActionButton(
                onClick = onCaptureClick,
                modifier = Modifier.size(72.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Capture",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            // Spacer for balance
            Spacer(modifier = Modifier.size(56.dp))
        }
    }
}

@Composable
private fun ScanResultContent(
    scanResult: ReceiptScannerManager.ReceiptScanResult,
    onCreateExpense: () -> Unit,
    onScanAgain: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Scan Results",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    scanResult.merchantName?.let { merchant ->
                        Text(
                            text = "Merchant: $merchant",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    scanResult.totalAmount?.let { amount ->
                        Text(
                            text = "Total: $${String.format("%.2f", amount)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    scanResult.date?.let { date ->
                        Text(
                            text = "Date: $date",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Text(
                        text = "Confidence: ${(scanResult.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        if (scanResult.items.isNotEmpty()) {
            item {
                Text(
                    text = "Items Found",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(scanResult.items) { item ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.name,
                            modifier = Modifier.weight(1f)
                        )
                        item.price?.let { price ->
                            Text(
                                text = "$${String.format("%.2f", price)}",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onScanAgain,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Scan Again")
                }
                
                Button(
                    onClick = onCreateExpense,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Create Expense")
                }
            }
        }
    }
}