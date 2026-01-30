package com.appstudio.finmarka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.ui.screens.addtransaction.AddEditTransactionScreen
import com.appstudio.finmarka.ui.screens.budget.BudgetScreen
import com.appstudio.finmarka.ui.screens.dashboard.DashboardScreen
import com.appstudio.finmarka.ui.screens.lock.LockScreen
import com.appstudio.finmarka.ui.screens.reports.ReportsScreen
import com.appstudio.finmarka.ui.screens.settings.SettingsScreen
import com.appstudio.finmarka.ui.screens.splash.SplashScreen
import com.appstudio.finmarka.ui.screens.transactions.TransactionsScreen
import com.appstudio.finmarka.ui.theme.FinmarkaTheme
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinmarkaTheme {
                val navController = rememberNavController()
                val showLock = preferencesManager.appLockEnabled && preferencesManager.pinHash != null
                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier
                ) {
                    composable("splash") {
                        SplashScreen(
                            onNavigateToLock = { navController.navigate("lock") { popUpTo("splash") { inclusive = true } } },
                            onNavigateToMain = { navController.navigate("main") { popUpTo("splash") { inclusive = true } } },
                            shouldShowLock = showLock
                        )
                    }
                    composable("lock") {
                        LockScreen(
                            onUnlocked = { navController.navigate("main") { popUpTo("lock") { inclusive = true } } },
                            pinHash = preferencesManager.pinHash,
                            biometricEnabled = preferencesManager.biometricEnabled,
                            onPinVerified = { pin ->
                                val hash = pin.sha256()
                                preferencesManager.pinHash?.let { it == hash }
                                    ?: false
                            }
                        )
                    }
                    composable("main") {
                        MainScreen(
                            navController = navController,
                            preferencesManager = preferencesManager
                        )
                    }
                    composable("add_transaction") {
                        AddEditTransactionScreen(
                            onSaved = { navController.popBackStack() }
                        )
                    }
                    composable("edit_transaction/{transactionId}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("transactionId")?.toIntOrNull() ?: 0
                        AddEditTransactionScreen(
                            onSaved = { navController.popBackStack() }
                        )
                    }
                    composable("budget") {
                        BudgetScreen()
                    }
                    composable("categories") {
                        Text("Categories management - placeholder")
                    }
                    composable("backup_restore") {
                        Text("Backup & Restore - placeholder")
                    }
                }
            }
        }
    }

    private fun String.sha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

@Composable
private fun MainScreen(
    navController: NavHostController,
    preferencesManager: PreferencesManager
) {
    val navControllerInner = rememberNavController()
    val navBackStackEntry by navControllerInner.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in listOf("dashboard", "transactions", "add", "reports", "settings")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    listOf(
                        Triple("dashboard", Icons.Default.Home, "Dashboard"),
                        Triple("transactions", Icons.Default.List, "Transactions"),
                        Triple("add", Icons.Default.Add, "Add"),
                        Triple("reports", Icons.Default.BarChart, "Reports"),
                        Triple("settings", Icons.Default.Settings, "Settings")
                    ).forEach { (route, icon, label) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = {
                                navControllerInner.navigate(route) {
                                    popUpTo(navControllerInner.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navControllerInner,
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    onNavigateToTransactions = { navControllerInner.navigate("transactions") },
                    onNavigateToAddTransaction = { navController.navigate("add_transaction") },
                    onTransactionClick = { id -> navController.navigate("edit_transaction/$id") }
                )
            }
            composable("transactions") {
                TransactionsScreen(
                    onTransactionClick = { id -> navController.navigate("edit_transaction/$id") }
                )
            }
            composable("add") {
                AddEditTransactionScreen(
                    onSaved = { navControllerInner.navigate("dashboard") }
                )
            }
            composable("reports") {
                ReportsScreen()
            }
            composable("settings") {
                SettingsScreen(
                    onNavigateToCategories = { navController.navigate("categories") },
                    onNavigateToBackupRestore = { navController.navigate("backup_restore") },
                    onNavigateToAppLock = { }
                )
            }
        }
    }
}
