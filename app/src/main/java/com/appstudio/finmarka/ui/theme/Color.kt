package com.appstudio.finmarka.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val PrimaryLight = Color(0xFF0C7A43)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFC9F2D8)
private val SecondaryLight = Color(0xFF2A5DA8)
private val BackgroundLight = Color(0xFFF7F9FC)
private val SurfaceLight = Color(0xFFFFFFFF)
private val SurfaceVariantLight = Color(0xFFEBF0F5)
private val OnSurfaceLight = Color(0xFF1A1F2A)
private val OnSurfaceVariantLight = Color(0xFF596273)
private val ErrorLight = Color(0xFFBA1A1A)
private val OutlineLight = Color(0xFFBBC3CF)

private val PrimaryDark = Color(0xFF78DFA8)
private val OnPrimaryDark = Color(0xFF00391D)
private val PrimaryContainerDark = Color(0xFF00522D)
private val SecondaryDark = Color(0xFFACC7FF)
private val BackgroundDark = Color(0xFF0F141D)
private val SurfaceDark = Color(0xFF131A24)
private val SurfaceVariantDark = Color(0xFF243042)
private val OnSurfaceDark = Color(0xFFE5EAF3)
private val OnSurfaceVariantDark = Color(0xFFBDC7D8)
private val ErrorDark = Color(0xFFFFB4AB)
private val OutlineDark = Color(0xFF8C96A8)

val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    secondary = SecondaryLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorLight,
    outline = OutlineLight
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    secondary = SecondaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    outline = OutlineDark
)


val IncomeGreen = Color(0xFF1FA463)
val ExpenseRed = Color(0xFFD64D63)

val DashboardAccentStart = Color(0xFF1CD8A6)
val DashboardAccentEnd = Color(0xFF3E8BFF)
val DashboardBackgroundTop = Color(0xFF1F2D47)
val DashboardBackgroundBottom = Color(0xFF08121F)
val DashboardBalanceStart = Color(0xFF24C39E)
val DashboardBalanceEnd = Color(0xFF3E7DE8)
val DashboardCardSurface = Color(0xFF1A2340)
val DashboardCardSurfaceAlt = Color(0xFF1E3A56)
val DashboardCardSurfaceMuted = Color(0xFF223554)
val DashboardTextMuted = Color(0xFF9CB0CC)
val DashboardTextOnDark = Color(0xFFDDE7F8)
val DashboardTextOnGradient = Color(0xFFEAF6FF)
