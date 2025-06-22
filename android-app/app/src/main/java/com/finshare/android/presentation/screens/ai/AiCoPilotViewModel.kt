package com.finshare.android.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finshare.android.domain.model.ChatMessage
import com.finshare.android.domain.repository.AiRepository
import com.finshare.android.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiCoPilotViewModel @Inject constructor(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiCoPilotUiState())
    val uiState: StateFlow<AiCoPilotUiState> = _uiState.asStateFlow()

    fun updateMessage(message: String) {
        _uiState.value = _uiState.value.copy(currentMessage = message)
    }

    fun sendMessage() {
        val currentMessage = _uiState.value.currentMessage.trim()
        if (currentMessage.isBlank()) return

        // Add user message
        val userMessage = ChatMessage(
            role = "user",
            content = currentMessage,
            timestamp = System.currentTimeMillis()
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            currentMessage = "",
            isLoading = true
        )

        // Send to AI service
        viewModelScope.launch {
            val result = aiRepository.chatWithCoPilot(
                message = currentMessage,
                conversationHistory = _uiState.value.messages.takeLast(10) // Last 10 messages for context
            )

            when (result) {
                is Resource.Success -> {
                    val aiMessage = ChatMessage(
                        role = "assistant",
                        content = result.data?.reply ?: "I'm sorry, I couldn't process that request.",
                        timestamp = System.currentTimeMillis()
                    )

                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + aiMessage,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    val errorMessage = ChatMessage(
                        role = "assistant",
                        content = "I'm experiencing some difficulties. Please try again later.",
                        timestamp = System.currentTimeMillis()
                    )

                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + errorMessage,
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun clearChat() {
        _uiState.value = AiCoPilotUiState()
    }
}

data class AiCoPilotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val currentMessage: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)