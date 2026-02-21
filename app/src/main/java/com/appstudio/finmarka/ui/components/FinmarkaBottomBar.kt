package com.appstudio.finmarka.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

@Composable
fun FinmarkaBottomBar(
    currentDestination: NavDestination?,
    items: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    val selectedRoute = items.firstOrNull { item ->
        currentDestination?.hierarchy?.any { destination -> destination.route == item.route } == true
    }?.route ?: items.firstOrNull()?.route.orEmpty()

    BottomNavBar(
        selectedRoute = selectedRoute,
        items = items,
        onNavigate = onNavigate
    )
}
