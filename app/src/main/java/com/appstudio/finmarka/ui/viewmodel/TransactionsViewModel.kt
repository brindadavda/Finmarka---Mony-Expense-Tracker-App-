package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.domain.model.Category
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.data.repository.TransactionRepository
import com.appstudio.finmarka.ui.util.formatCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ------------------------------------------------------
// ✅ UI STATE
// ------------------------------------------------------

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filterCategoryId: Int = -1,
    val filterType: String? = null
)

// ------------------------------------------------------
// ✅ VIEWMODEL
// ------------------------------------------------------

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState

    init {
        loadCategories()
        loadTransactions()
    }

    // ------------------------------------------------------
    // ✅ LOAD TRANSACTIONS (Flow Collect)
    // ------------------------------------------------------

    private fun loadTransactions() {
        viewModelScope.launch {
            repository.getAllTransactions().collectLatest { list ->
                _uiState.update {
                    it.copy(
                        transactions = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    // ------------------------------------------------------
    // ✅ LOAD CATEGORIES
    // ------------------------------------------------------

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collectLatest { list ->
                _uiState.update {
                    it.copy(categories = list)
                }
            }
        }
    }

    // ------------------------------------------------------
    // ✅ FILTER
    // ------------------------------------------------------

    fun setFilter(categoryId: Int, type: String?) {
        _uiState.update {
            it.copy(
                filterCategoryId = categoryId,
                filterType = type
            )
        }

        applyFilter()
    }

    private fun applyFilter() {
        viewModelScope.launch {
            repository.filterTransactions(
                startDate = 0L,
                endDate = System.currentTimeMillis(),
                categoryId = _uiState.value.filterCategoryId,
                type = _uiState.value.filterType
            ).collectLatest { filteredList ->
                _uiState.update {
                    it.copy(
                        transactions = filteredList,
                        isLoading = false
                    )
                }
            }
        }
    }

    // ------------------------------------------------------
    // ✅ DELETE
    // ------------------------------------------------------

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    val getCurrencyCode: String
        get() = preferencesManager.currencyCode

}
