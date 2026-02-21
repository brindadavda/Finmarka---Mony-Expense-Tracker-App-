package com.appstudio.finmarka.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun FinMarkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSpacing provides AppSpacing()) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = FinMarkTypography,
            shapes = FinMarkShapes,
            content = content
        )
    }
}

@Composable
@Suppress("UNUSED_PARAMETER")
fun FinmarkaTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val useDarkTheme = darkTheme ?: isSystemInDarkTheme()
    FinMarkTheme(darkTheme = useDarkTheme, content = content)
}
