package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.appstudio.finmarka.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ✅ StateFlow for theme
    private val _darkTheme =
        MutableStateFlow(preferencesManager.darkTheme)

    val darkTheme: StateFlow<Boolean?> = _darkTheme

    // ✅ Update Theme
    fun setTheme(value: Boolean?) {
        preferencesManager.darkTheme = value
        _darkTheme.value = value
    }
}
