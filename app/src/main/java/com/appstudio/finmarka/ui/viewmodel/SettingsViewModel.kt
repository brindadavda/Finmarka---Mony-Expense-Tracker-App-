package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.data.local.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _currency = MutableStateFlow(preferencesManager.currencyCode)
    val currency: StateFlow<String> = _currency.asStateFlow()

    val theme: StateFlow<Boolean?> = userPreferencesDataStore.themeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun setCurrency(newCurrency: String) {
        preferencesManager.currencyCode = newCurrency
        _currency.value = newCurrency
    }

    fun setTheme(newTheme: Boolean?) {
        viewModelScope.launch {
            userPreferencesDataStore.setTheme(newTheme)
        }
    }
}
