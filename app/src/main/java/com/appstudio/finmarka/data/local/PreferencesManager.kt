package com.appstudio.finmarka.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var appLockEnabled: Boolean
        get() = prefs.getBoolean(KEY_APP_LOCK_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_APP_LOCK_ENABLED, value).apply()

    var pinHash: String?
        get() = prefs.getString(KEY_PIN_HASH, null)
        set(value) = prefs.edit().putString(KEY_PIN_HASH, value).apply()

    var biometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var currencyCode: String
        get() = prefs.getString(KEY_CURRENCY, "USD") ?: "USD"
        set(value) = prefs.edit().putString(KEY_CURRENCY, value).apply()

    var darkTheme: Boolean?
        get() = when (prefs.getInt(KEY_THEME, 0)) {
            1 -> true
            2 -> false
            else -> null // system
        }
        set(value) {
            val intVal = when (value) {
                true -> 1
                false -> 2
                null -> 0
            }
            prefs.edit().putInt(KEY_THEME, intVal).apply()
        }

    var fontSizeScale: Float
        get() = prefs.getFloat(KEY_FONT_SCALE, 1f).coerceIn(0.8f, 1.4f)
        set(value) = prefs.edit().putFloat(KEY_FONT_SCALE, value.coerceIn(0.8f, 1.4f)).apply()

    companion object {
        private const val PREFS_NAME = "finmarka_prefs"
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_THEME = "theme"
        private const val KEY_FONT_SCALE = "font_scale"
    }
}
