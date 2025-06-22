package com.finshare.android.presentation.screens.receipt

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finshare.android.data.camera.ReceiptScannerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptScanViewModel @Inject constructor(
    private val receiptScannerManager: ReceiptScannerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiptScanUiState())
    val uiState: StateFlow<ReceiptScanUiState> = _uiState.asStateFlow()

    fun setupCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        viewModelScope.launch {
            try {
                val success = receiptScannerManager.setupCamera(lifecycleOwner, previewView)
                _uiState.value = _uiState.value.copy(
                    isCameraReady = success
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to setup camera: ${e.message}"
                )
            }
        }
    }

    fun captureReceipt() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            
            try {
                val result = receiptScannerManager.captureAndScanReceipt()
                _uiState.value = _uiState.value.copy(
                    scanResult = result,
                    isProcessing = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = "Failed to scan receipt: ${e.message}"
                )
            }
        }
    }

    fun openGallery() {
        // TODO: Implement gallery selection
        _uiState.value = _uiState.value.copy(
            error = "Gallery selection not implemented yet"
        )
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(
            scanResult = null,
            error = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        receiptScannerManager.cleanup()
    }
}

data class ReceiptScanUiState(
    val isCameraReady: Boolean = false,
    val isProcessing: Boolean = false,
    val scanResult: ReceiptScannerManager.ReceiptScanResult? = null,
    val error: String? = null
)