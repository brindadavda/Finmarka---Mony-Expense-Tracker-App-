package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.entity.AccountEntity
import com.appstudio.finmarka.data.repository.AccountsRepository
import com.appstudio.finmarka.data.repository.TransactionRepository
import com.appstudio.finmarka.domain.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountDetailUiState(
    val account: AccountEntity? = null,
    val transactions: List<Transaction> = emptyList()
)

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    private val transactionRepository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val accountId: Int = savedStateHandle.get<String>("accountId")?.toIntOrNull() ?: 0

    private val _uiState = MutableStateFlow(AccountDetailUiState())
    val uiState: StateFlow<AccountDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                accountsRepository.getAllAccounts(),
                transactionRepository.getAllTransactions()
            ) { accounts, transactions ->
                val account = accounts.firstOrNull { it.id == accountId }
                val filtered = transactions.filter { it.accountId == accountId }
                AccountDetailUiState(account = account, transactions = filtered)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
