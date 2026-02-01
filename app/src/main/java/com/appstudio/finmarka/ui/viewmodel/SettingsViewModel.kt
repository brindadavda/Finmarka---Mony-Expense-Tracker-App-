package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.appstudio.finmarka.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _currency = MutableStateFlow(preferencesManager.currencyCode)
    val currency: StateFlow<String> = _currency.asStateFlow()

    private val _theme = MutableStateFlow(preferencesManager.darkTheme)
    val theme: StateFlow<Boolean?> = _theme.asStateFlow()

    fun setCurrency(newCurrency: String) {
        preferencesManager.currencyCode = newCurrency
        _currency.value = newCurrency
    }

    fun setTheme(newTheme: Boolean?) {
        preferencesManager.darkTheme = newTheme
        _theme.value = newTheme
    }
}
