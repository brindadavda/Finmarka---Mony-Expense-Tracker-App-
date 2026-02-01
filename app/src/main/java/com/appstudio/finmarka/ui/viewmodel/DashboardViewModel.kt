package com.appstudio.finmarka.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.data.repository.TransactionRepository
import com.appstudio.finmarka.ui.util.formatCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class DashboardUiState(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val todaySpending: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val zone = ZoneId.systemDefault()
            val now = YearMonth.now()
            val startOfMonth = now.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val endOfMonth = now.atEndOfMonth().atTime(23, 59, 59, 999_999_999).atZone(zone).toInstant().toEpochMilli()
            val today = LocalDate.now()
            val startOfToday = today.atStartOfDay(zone).toInstant().toEpochMilli()
            val endOfToday = today.atTime(23, 59, 59, 999_999_999).atZone(zone).toInstant().toEpochMilli()
            val income = transactionRepository.getTotalIncome(startOfMonth, endOfMonth)
            val expense = transactionRepository.getTotalExpense(startOfMonth, endOfMonth)
            val todaySpending = transactionRepository.getTotalExpense(startOfToday, endOfToday)
            transactionRepository.getRecentTransactions(10).collect { recent ->
                _uiState.update {
                    it.copy(
                        totalIncome = income,
                        totalExpense = expense,
                        totalBalance = income - expense,
                        todaySpending = todaySpending,
                        recentTransactions = recent,
                        isLoading = false
                    )
                }
            }
        }
    }

    // Helper to get currency from preferences
    fun formatWithPrefCurrency(amount: Double): String {
        val currency = preferencesManager.currencyCode
        return formatCurrency(amount, currency)
    }
}
