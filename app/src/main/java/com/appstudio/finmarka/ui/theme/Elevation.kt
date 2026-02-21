package com.appstudio.finmarka.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AppElevation(
    val none: Dp = 0.dp,
    val sm: Dp = 2.dp,
    val md: Dp = 6.dp,
    val lg: Dp = 10.dp
)

val FinMarkElevation = AppElevation()
