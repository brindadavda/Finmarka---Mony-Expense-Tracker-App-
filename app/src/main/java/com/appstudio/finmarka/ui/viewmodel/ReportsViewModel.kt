package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.data.model.ReportItem
import com.appstudio.finmarka.data.repository.TransactionRepository
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.util.formatCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportsUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val transactions: List<Transaction> = emptyList()
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val incomeFlow = transactionRepository.getTotalIncomeFlow()
            val expenseFlow = transactionRepository.getTotalExpenseFlow()
            val transactionsFlow = transactionRepository.getAllTransactions()

            combine(incomeFlow, expenseFlow, transactionsFlow) { income, expense, transactions ->
                ReportsUiState(
                    totalIncome = income,
                    totalExpense = expense,
                    transactions = transactions
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // Convert transactions to ReportItem for charts/exports
    fun getReportData(): List<ReportItem> {
        return _uiState.value.transactions.map { tx ->
            ReportItem(
                label = "${tx.categoryName} (${tx.type.name})",
                amount = tx.amount.toFloat()
            )
        }
    }

    fun formatWithPrefCurrency(amount: Double): String {
        val currency = preferencesManager.currencyCode
        return formatCurrency(amount, currency)
    }
}
