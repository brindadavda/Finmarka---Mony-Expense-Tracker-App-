package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.data.model.PaymentMode
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.data.repository.TransactionRepository
import com.appstudio.finmarka.data.repository.CategoryRepository
import com.appstudio.finmarka.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTransactionUiState(
    val amount: String = "",
    val currency: String = "USD",
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: Int = 0,
    val categories: List<Category> = emptyList(),
    val dateTime: Long = System.currentTimeMillis(),
    val note: String = "",
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val isEdit: Boolean = false,
    val transactionId: Int = 0,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val preferencesManager: PreferencesManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId: Int? = savedStateHandle.get<String>("transactionId")?.toIntOrNull()

    private val _uiState = MutableStateFlow(AddEditTransactionUiState())
    val uiState: StateFlow<AddEditTransactionUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(currency = preferencesManager.currencyCode) }
        viewModelScope.launch {
            categoryRepository.ensureDefaultCategories()
            categoryRepository.getCategoriesAsDomain().collect { list ->
                _uiState.update { it.copy(categories = list) }
            }
        }
        transactionId?.let { id ->
            viewModelScope.launch {
                transactionRepository.getTransactionById(id)?.let { t ->
                    _uiState.update {
                        it.copy(
                            isEdit = true,
                            transactionId = id,
                            amount = t.amount.toString(),
                            currency = t.currency,
                            type = t.type,
                            categoryId = t.categoryId,
                            dateTime = t.dateTime,
                            note = t.note ?: "",
                            paymentMode = t.paymentMode
                        )
                    }
                }
            }
        } ?: run {
            _uiState.update { it.copy(categoryId = 0) }
        }
    }

    fun setAmount(value: String) {
        _uiState.update { it.copy(amount = value, error = null) }
    }

    fun setCurrency(currency: String) {
        _uiState.update { it.copy(currency = currency) }
    }

    fun setType(type: TransactionType) {
        _uiState.update { it.copy(type = type, categoryId = 0) }
    }

    fun setCategoryId(id: Int) {
        _uiState.update { it.copy(categoryId = id) }
    }

    fun setDateTime(dateTime: Long) {
        _uiState.update { it.copy(dateTime = dateTime) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun setPaymentMode(mode: PaymentMode) {
        _uiState.update { it.copy(paymentMode = mode) }
    }

    fun save() {
        viewModelScope.launch {
            val state = _uiState.value
            val amount = state.amount.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                _uiState.update { it.copy(error = "Enter valid amount") }
                return@launch
            }
            if (state.categoryId == 0 && state.categories.isNotEmpty()) {
                _uiState.update { it.copy(error = "Select a category") }
                return@launch
            }
            val categoryId = state.categoryId.takeIf { it > 0 } ?: state.categories.firstOrNull()?.id ?: 0
            if (categoryId == 0) {
                _uiState.update { it.copy(error = "Select a category") }
                return@launch
            }
            val currency = state.currency.ifBlank { preferencesManager.currencyCode }
            val convertedAmount = amount // TODO: convert to base currency when multi-currency rates available
            try {
                if (state.isEdit) {
                    transactionRepository.updateTransaction(
                        id = state.transactionId,
                        amount = amount,
                        currency = currency,
                        convertedAmount = convertedAmount,
                        type = state.type,
                        categoryId = categoryId,
                        dateTime = state.dateTime,
                        note = state.note.ifBlank { null },
                        paymentMode = state.paymentMode
                    )
                } else {
                    transactionRepository.insertTransaction(
                        amount = amount,
                        currency = currency,
                        convertedAmount = convertedAmount,
                        type = state.type,
                        categoryId = categoryId,
                        dateTime = state.dateTime,
                        note = state.note.ifBlank { null },
                        paymentMode = state.paymentMode
                    )
                }
                _uiState.update { it.copy(saveSuccess = true, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to save") }
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}
