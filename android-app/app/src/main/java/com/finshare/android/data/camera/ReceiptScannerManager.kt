package com.finshare.android.data.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class ReceiptScannerManager @Inject constructor(
    private val context: Context
) {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var imageCapture: ImageCapture? = null

    data class ReceiptScanResult(
        val merchantName: String?,
        val totalAmount: Double?,
        val date: String?,
        val items: List<ReceiptItem>,
        val confidence: Float
    )

    data class ReceiptItem(
        val name: String,
        val price: Double?
    )

    suspend fun setupCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ): Boolean = suspendCancellableCoroutine { continuation ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                
                continuation.resume(true)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    suspend fun captureAndScanReceipt(): ReceiptScanResult = suspendCancellableCoroutine { continuation ->
        val imageCapture = this.imageCapture ?: run {
            continuation.resumeWithException(IllegalStateException("Camera not initialized"))
            return@suspendCancellableCoroutine
        }
        
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(createImageFile()).build()
        
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val imageUri = output.savedUri ?: run {
                        continuation.resumeWithException(Exception("Failed to save image"))
                        return
                    }
                    
                    // Process the image with ML Kit
                    processReceiptImage(imageUri) { result ->
                        continuation.resume(result)
                    }
                }
                
                override fun onError(exception: ImageCaptureException) {
                    continuation.resumeWithException(exception)
                }
            }
        )
    }

    suspend fun scanReceiptFromUri(imageUri: Uri): ReceiptScanResult = suspendCancellableCoroutine { continuation ->
        processReceiptImage(imageUri) { result ->
            continuation.resume(result)
        }
    }

    private fun processReceiptImage(imageUri: Uri, callback: (ReceiptScanResult) -> Unit) {
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val result = parseReceiptText(visionText.text)
                    callback(result)
                }
                .addOnFailureListener { e ->
                    callback(
                        ReceiptScanResult(
                            merchantName = null,
                            totalAmount = null,
                            date = null,
                            items = emptyList(),
                            confidence = 0f
                        )
                    )
                }
        } catch (e: Exception) {
            callback(
                ReceiptScanResult(
                    merchantName = null,
                    totalAmount = null,
                    date = null,
                    items = emptyList(),
                    confidence = 0f
                )
            )
        }
    }

    private fun parseReceiptText(text: String): ReceiptScanResult {
        val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        
        var merchantName: String? = null
        var totalAmount: Double? = null
        var date: String? = null
        val items = mutableListOf<ReceiptItem>()
        var confidence = 0f
        
        // Simple parsing logic - can be enhanced with more sophisticated algorithms
        for (i, line in lines.withIndex()) {
            // Try to find merchant name (usually first few lines)
            if (i < 3 && merchantName == null && line.length > 3 && !line.contains(Regex("[0-9.]"))) {
                merchantName = line
                confidence += 0.2f
            }
            
            // Try to find total amount
            if (line.contains(Regex("total|sum|amount", RegexOption.IGNORE_CASE))) {
                val amountMatch = Regex("[0-9]+\\.?[0-9]*").find(line)
                if (amountMatch != null) {
                    totalAmount = amountMatch.value.toDoubleOrNull()
                    confidence += 0.4f
                }
            }
            
            // Try to find date
            if (date == null) {
                val dateMatch = Regex("\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}").find(line)
                if (dateMatch != null) {
                    date = dateMatch.value
                    confidence += 0.2f
                }
            }
            
            // Try to find items with prices
            val priceMatch = Regex("([A-Za-z\\s]+)\\s+([0-9]+\\.?[0-9]*)").find(line)
            if (priceMatch != null) {
                val itemName = priceMatch.groupValues[1].trim()
                val itemPrice = priceMatch.groupValues[2].toDoubleOrNull()
                if (itemName.isNotEmpty() && itemPrice != null) {
                    items.add(ReceiptItem(itemName, itemPrice))
                    confidence += 0.1f
                }
            }
        }
        
        return ReceiptScanResult(
            merchantName = merchantName,
            totalAmount = totalAmount,
            date = date,
            items = items,
            confidence = confidence.coerceAtMost(1f)
        )
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "RECEIPT_${timeStamp}"
        val storageDir = File(context.getExternalFilesDir(null), "receipts")
        
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        
        return File(storageDir, "${imageFileName}.jpg")
    }

    fun cleanup() {
        // Clean up resources if needed
    }
}