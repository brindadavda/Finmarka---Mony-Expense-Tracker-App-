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
    val colors = if (darkTheme) DarkColors else LightColors

    CompositionLocalProvider(
        LocalSpacing provides AppSpacing(),
        LocalElevation provides AppElevation()
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = FinMarkTypography,
            shapes = FinMarkShapes,
            content = content
        )
    }
}

@Composable
fun FinmarkaTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FinMarkTheme(darkTheme = darkTheme ?: isSystemInDarkTheme(), content = content)
}
