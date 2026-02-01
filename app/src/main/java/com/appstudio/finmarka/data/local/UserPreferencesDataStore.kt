package com.appstudio.finmarka.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "finmarka_datastore")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.dataStore

    val themeFlow: Flow<Boolean?> = dataStore.data.map { prefs ->
        when (prefs[KEY_THEME] ?: 0) {
            1 -> true
            2 -> false
            else -> null
        }
    }

    val currencyFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_CURRENCY] ?: "USD"
    }

    suspend fun setTheme(value: Boolean?) {
        dataStore.edit { prefs ->
            prefs[KEY_THEME] = when (value) {
                true -> 1
                false -> 2
                null -> 0
            }
        }
    }

    suspend fun setCurrency(value: String) {
        dataStore.edit { prefs ->
            prefs[KEY_CURRENCY] = value
        }
    }

    companion object {
        private val KEY_THEME = intPreferencesKey("theme")
        private val KEY_CURRENCY = stringPreferencesKey("currency")
    }
}
