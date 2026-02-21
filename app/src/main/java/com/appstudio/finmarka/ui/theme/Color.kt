package com.appstudio.finmarka.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF2D7DFA)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFDCE9FF)
val OnPrimaryContainer = Color(0xFF001B3F)
val Secondary = Color(0xFF24B06B)
val Background = Color(0xFFF6F8FC)
val Surface = Color(0xFFFFFFFF)
val SurfaceVariant = Color(0xFFEFF3FA)
val OnSurface = Color(0xFF141B2D)
val OnSurfaceVariant = Color(0xFF5F6B84)
val Error = Color(0xFFE35D6A)
val Outline = Color(0xFFD3DAEA)

val DarkPrimary = Color(0xFFA8C7FF)
val DarkOnPrimary = Color(0xFF003066)
val DarkPrimaryContainer = Color(0xFF00458F)
val DarkOnPrimaryContainer = Color(0xFFDCE9FF)
val DarkSecondary = Color(0xFF7BDDAE)
val DarkBackground = Color(0xFF0B1220)
val DarkSurface = Color(0xFF111A2A)
val DarkSurfaceVariant = Color(0xFF1B2638)
val DarkOnSurface = Color(0xFFE5EBF8)
val DarkOnSurfaceVariant = Color(0xFFB3C0D7)
val DarkError = Color(0xFFFFB4B8)
val DarkOutline = Color(0xFF3A475F)

internal val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error,
    outline = Outline
)

internal val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkError,
    outline = DarkOutline
)

// Backward compatible aliases
val FinGreen = Secondary
val FinBlue = Primary
val FinNavy = OnPrimaryContainer
val FinMint = PrimaryContainer
val FinCardLight = Surface
val FinBackgroundLight = Background
val FinBackgroundDark = DarkBackground
val FinSurfaceDark = DarkSurface
val FinTextDark = OnSurface
val FinTextMuted = OnSurfaceVariant
val ExpenseRed = Error
val IncomeGreen = Secondary
val BalanceBlue = Primary
val BackgroundGray = Background
val CardWhite = Surface
val TextBlack = OnSurface
val TextGray = OnSurfaceVariant
val ChartOrange = Color(0xFFFFAA33)
