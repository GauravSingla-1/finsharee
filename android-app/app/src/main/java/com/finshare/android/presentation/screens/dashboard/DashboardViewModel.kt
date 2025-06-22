package com.finshare.android.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finshare.android.domain.model.Group
import com.finshare.android.domain.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Dashboard screen implementing MVVM pattern
 * Manages UI state and business logic
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val groups = groupRepository.getUserGroups()
                val totalBalance = calculateTotalBalance(groups)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    groups = groups,
                    totalBalance = totalBalance,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    private fun calculateTotalBalance(groups: List<Group>): String {
        // Mock calculation - in real implementation, would fetch from balance service
        return "0.00"
    }

    fun refresh() {
        loadDashboardData()
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val groups: List<Group> = emptyList(),
    val totalBalance: String = "0.00",
    val error: String? = null
)