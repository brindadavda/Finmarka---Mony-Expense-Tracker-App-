package com.appstudio.finmarka.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ------------------------------------------------------
// ✅ Dark Theme Colors (Finance Dark Mode)
// ------------------------------------------------------

private val DarkColorScheme = darkColorScheme(
    primary = IncomeGreen,         // Main Green
    secondary = BalanceBlue,       // Buttons / Balance
    tertiary = ChartOrange,        // Highlight

    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,

    onBackground = Color.White,
    onSurface = Color.White,

    error = ExpenseRed,
    onError = Color.White
)


// ------------------------------------------------------
// ✅ Light Theme Colors (Finance Light Mode)
// ------------------------------------------------------

private val LightColorScheme = lightColorScheme(
    primary = IncomeGreen,         // Main Green
    secondary = BalanceBlue,       // Buttons / Balance
    tertiary = ExpenseRed,         // Expense Highlight

    background = BackgroundGray,
    surface = CardWhite,

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,

    onBackground = TextBlack,
    onSurface = TextBlack,

    error = ExpenseRed,
    onError = Color.White
)


// ------------------------------------------------------
// ✅ Main Theme Setup
// ------------------------------------------------------

@Composable
fun FinmarkaTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean = false, // 🔥 Turn OFF for custom finance colors
    content: @Composable () -> Unit
) {
    val useDarkTheme = darkTheme ?: isSystemInDarkTheme()

    val colorScheme = when {
        // Dynamic color (Android 12+) optional
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        // Dark Mode
        useDarkTheme -> DarkColorScheme

        // Light Mode
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
