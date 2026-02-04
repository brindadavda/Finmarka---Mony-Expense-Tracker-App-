package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.entity.AccountEntity
import com.appstudio.finmarka.data.repository.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val accounts: StateFlow<List<AccountEntity>> = accountsRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filteredAccounts: StateFlow<List<AccountEntity>> = combine(accounts, searchQuery) { list, query ->
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            list
        } else {
            list.filter { account ->
                account.name.contains(trimmed, ignoreCase = true) ||
                    (account.details?.contains(trimmed, ignoreCase = true) == true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            accountsRepository.ensureDefaultAccount()
        }
    }

    fun updateSearchQuery(value: String) {
        _searchQuery.update { value }
    }

    fun addAccount(account: AccountEntity) {
        viewModelScope.launch {
            accountsRepository.insertAccount(account)
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            accountsRepository.updateAccount(account)
        }
    }

    suspend fun getAccountById(id: Int): AccountEntity? {
        return accountsRepository.getAccountById(id)
    }
}
