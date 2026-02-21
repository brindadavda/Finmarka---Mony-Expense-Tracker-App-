package com.appstudio.finmarka.ui.components

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@androidx.compose.runtime.Composable
fun FinmarkaBottomBar(
    currentDestination: NavDestination?,
    items: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    BottomNavBar(
        currentDestination = currentDestination,
        items = items,
        onNavigate = onNavigate
    )
}
