package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.data.model.PaymentMode
import com.appstudio.finmarka.data.model.TransactionStatus
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.data.repository.TransactionRepository
import com.appstudio.finmarka.data.repository.CategoryRepository
import com.appstudio.finmarka.data.repository.AccountsRepository
import com.appstudio.finmarka.data.local.entity.AccountEntity
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
    val accounts: List<AccountEntity> = emptyList(),
    val accountName: String = "",
    val accountId: Int? = null,
    val merchantName: String = "",
    val tags: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val dateTime: Long = System.currentTimeMillis(),
    val note: String = "",
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val isRecurring: Boolean = false,
    val isEdit: Boolean = false,
    val transactionId: Int = 0,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountsRepository: AccountsRepository,
    private val preferencesManager: PreferencesManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId: Int? = savedStateHandle.get<String>("transactionId")?.toIntOrNull()
    private val accountIdArg: Int? = savedStateHandle.get<String>("accountId")?.toIntOrNull()

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
        viewModelScope.launch {
            accountsRepository.getAllAccounts().collect { list ->
                _uiState.update { current ->
                    val resolvedName = current.accountId?.let { selectedId ->
                        list.firstOrNull { it.id == selectedId }?.name.orEmpty()
                    }.orEmpty()
                    current.copy(
                        accounts = list,
                        accountName = if (resolvedName.isNotBlank()) resolvedName else current.accountName
                    )
                }
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
                            type = t.type,
                            categoryId = t.categoryId,
                            accountName = t.accountId?.let { "Account #$it" } ?: "",
                            accountId = t.accountId,
                            merchantName = t.merchantName.orEmpty(),
                            dateTime = t.dateTime,
                            note = t.note ?: "",
                            paymentMode = t.paymentMode,
                            status = t.status,
                            isRecurring = t.isRecurring,
                            attachments = t.attachmentUris?.split(",")?.filter { it.isNotBlank() }.orEmpty()
                        )
                    }
                }
            }
        } ?: run {
            _uiState.update {
                it.copy(
                    categoryId = 0,
                    accountId = accountIdArg,
                    accountName = ""
                )
            }
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

    fun setAccountName(name: String) {
        _uiState.update { it.copy(accountName = name) }
    }

    fun setAccountId(id: Int?) {
        val accountName = id?.let { selectedId ->
            _uiState.value.accounts.firstOrNull { it.id == selectedId }?.name.orEmpty()
        }.orEmpty()
        _uiState.update { it.copy(accountId = id, accountName = accountName) }
    }

    fun setMerchantName(name: String) {
        _uiState.update { it.copy(merchantName = name) }
    }

    fun setTags(tags: List<String>) {
        _uiState.update { it.copy(tags = tags) }
    }

    fun setAttachments(attachments: List<String>) {
        _uiState.update { it.copy(attachments = attachments) }
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

    fun setStatus(status: TransactionStatus) {
        _uiState.update { it.copy(status = status) }
    }

    fun toggleRecurring() {
        _uiState.update { it.copy(isRecurring = !it.isRecurring) }
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
            val currency =  preferencesManager.currencyCode
            val convertedAmount = amount // TODO: convert to base currency when multi-currency rates available
            val attachmentUris = state.attachments.joinToString(",").ifBlank { null }
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
                        paymentMode = state.paymentMode,
                        accountId = state.accountId,
                        merchantName = state.merchantName.ifBlank { null },
                        status = state.status,
                        isRecurring = state.isRecurring,
                        attachmentUris = attachmentUris
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
                        paymentMode = state.paymentMode,
                        accountId = state.accountId,
                        merchantName = state.merchantName.ifBlank { null },
                        status = state.status,
                        isRecurring = state.isRecurring,
                        attachmentUris = attachmentUris
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
