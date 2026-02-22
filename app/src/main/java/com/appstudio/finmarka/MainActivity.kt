package com.appstudio.finmarka

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.ui.screens.addtransaction.AddEditTransactionScreen
import com.appstudio.finmarka.ui.screens.budget.BudgetScreen
import com.appstudio.finmarka.ui.screens.calendar.CalendarViewScreen
import com.appstudio.finmarka.ui.screens.calculators.CalculatorsScreen
import com.appstudio.finmarka.ui.screens.dashboard.DashboardScreen
import com.appstudio.finmarka.ui.screens.exchange.ExchangeRatesScreen
import com.appstudio.finmarka.ui.screens.lock.LockScreen
import com.appstudio.finmarka.ui.screens.notes.AddEditNoteScreen
import com.appstudio.finmarka.ui.screens.notes.NotesListScreen
import com.appstudio.finmarka.ui.screens.reminders.BillRemindersScreen
import com.appstudio.finmarka.ui.screens.reports.ReportsScreen
import com.appstudio.finmarka.ui.screens.settings.SettingsScreen
import com.appstudio.finmarka.ui.screens.splash.SplashScreen
import com.appstudio.finmarka.ui.screens.statements.StatementsScreen
import com.appstudio.finmarka.ui.screens.tags.TagsScreen
import com.appstudio.finmarka.ui.screens.tasks.TodosScreen
import com.appstudio.finmarka.ui.screens.transactions.TransactionsScreen
import com.appstudio.finmarka.ui.screens.warranties.WarrantiesScreen
import com.appstudio.finmarka.ui.screens.assets.AssetsScreen
import com.appstudio.finmarka.ui.screens.accounts.AccountDetailScreen
import com.appstudio.finmarka.ui.screens.accounts.AccountsListScreen
import com.appstudio.finmarka.ui.screens.accounts.AddEditAccountScreen
import com.appstudio.finmarka.ui.screens.categories.CategoriesScreen
import com.appstudio.finmarka.ui.screens.loans.LendBorrowScreen
import com.appstudio.finmarka.ui.screens.loans.LoansScreen
import com.appstudio.finmarka.ui.screens.merchants.MerchantsScreen
import com.appstudio.finmarka.ui.screens.savings.SavingsScreen
import com.appstudio.finmarka.ui.screens.templates.TemplatesScreen
import com.appstudio.finmarka.ui.screens.backup.BackupRestoreScreen
import com.appstudio.finmarka.ui.theme.FinmarkaTheme
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest
import javax.inject.Inject
import com.appstudio.finmarka.ui.viewmodel.ThemeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // ✅ Theme ViewModel
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val darkTheme by themeViewModel.darkTheme.collectAsState()

            // ✅ Apply Theme from Preferences
            FinmarkaTheme(darkTheme = darkTheme) {
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
                            onSaved = { navController.popBackStack() },
                            onAddAccount = { navController.navigate("accounts") },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("add_transaction?accountId={accountId}") {
                        AddEditTransactionScreen(
                            onSaved = { navController.popBackStack() },
                            onAddAccount = { navController.navigate("accounts") },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("edit_transaction/{transactionId}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("transactionId")?.toIntOrNull() ?: 0
                        AddEditTransactionScreen(
                            onSaved = { navController.popBackStack() },
                            onAddAccount = { navController.navigate("accounts") },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("backup_restore") {
                        BackupRestoreScreen()
                    }
                    composable("templates") {
                        TemplatesScreen()
                    }
                    composable("accounts") {
                        AccountsListScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onAddAccount = { navController.navigate("add_account") },
                            onAccountSelected = { id -> navController.navigate("account_detail/$id") },
                            onEditAccount = { id -> navController.navigate("edit_account/$id") }
                        )
                    }
                    composable("add_account") {
                        AddEditAccountScreen(
                            accountId = null,
                            onNavigateBack = { navController.popBackStack() },
                            onSave = { navController.popBackStack() }
                        )
                    }
                    composable("edit_account/{accountId}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("accountId")?.toIntOrNull()
                        AddEditAccountScreen(
                            accountId = id,
                            onNavigateBack = { navController.popBackStack() },
                            onSave = { navController.popBackStack() }
                        )
                    }
                    composable("account_detail/{accountId}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("accountId")?.toIntOrNull() ?: 0
                        AccountDetailScreen(
                            accountId = id,
                            onNavigateBack = { navController.popBackStack() },
                            onAddTransaction = { accountId -> navController.navigate("add_transaction?accountId=$accountId") }
                        )
                    }
                    composable("categories") {
                        CategoriesScreen()
                    }
                    composable("merchants") {
                        MerchantsScreen()
                    }
                    composable("assets") {
                        AssetsScreen()
                    }
                    composable("savings") {
                        SavingsScreen()
                    }
                    composable("loans") {
                        LoansScreen()
                    }
                    composable("lend_borrow") {
                        LendBorrowScreen()
                    }
                    composable("bill_reminders") {
                        BillRemindersScreen()
                    }
                    composable("notes") {
                        NotesListScreen(
                            onAddNote = { navController.navigate("notes_add") },
                            onEditNote = { id -> navController.navigate("notes_edit/$id") }
                        )
                    }
                    composable("notes_add") {
                        AddEditNoteScreen(
                            noteId = null,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("notes_edit/{noteId}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                        AddEditNoteScreen(
                            noteId = id,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("todos") {
                        TodosScreen()
                    }
                    composable("warranties") {
                        WarrantiesScreen()
                    }
                    composable("tags") {
                        TagsScreen()
                    }
                    composable("statements") {
                        StatementsScreen()
                    }
                    composable("calendar") {
                        CalendarViewScreen()
                    }
                    composable("exchange_rates") {
                        ExchangeRatesScreen()
                    }
                    composable("calculators") {
                        CalculatorsScreen()
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MainScreen(
    navController: NavHostController,
    preferencesManager: PreferencesManager
) {
    val navControllerInner = rememberNavController()

    NavHost(
        navController = navControllerInner,
        startDestination = "home",
        modifier = Modifier
    ) {
        composable("home") {
            DashboardScreen(
                onNavigateToTransactions = { navControllerInner.navigate("transactions") },
                onNavigateToAddTransaction = { navController.navigate("add_transaction") },
                onNavigateToReports = { navControllerInner.navigate("reports") },
                onNavigateToBudget = { navControllerInner.navigate("budget") },
                onNavigateToSettings = { navControllerInner.navigate("settings") },
                onTransactionClick = { id -> navController.navigate("edit_transaction/$id") }
            )
        }
        composable("transactions") {
            TransactionsScreen(
                onTransactionClick = { id -> navController.navigate("edit_transaction/$id") },
                onNavigateBack = { navControllerInner.popBackStack() }
            )
        }
        composable("reports") {
            ReportsScreen()
        }
        composable("settings") {
            SettingsScreen(
                onNavigateToCategories = { navController.navigate("categories") },
                onNavigateToBackupRestore = { navController.navigate("backup_restore") },
                onNavigateToAppLock = { navController.navigate("app_lock") },
                onNavigateToAboutUs = { navController.navigate("about_us") },
                onNavigateToPrivacyPolicy = { navController.navigate("privacy_policy") },
                onNavigateToTerms = { navController.navigate("terms") },
                onNavigateToRoute = { route -> navControllerInner.navigate(route) },
                onNavigateBack = { navControllerInner.popBackStack() }
            )
        }
        composable("budget") { BudgetScreen() }
        composable("templates") { TemplatesScreen() }
        composable("accounts") {
            AccountsListScreen(
                onNavigateBack = { navControllerInner.popBackStack() },
                onAddAccount = { navControllerInner.navigate("add_account") },
                onAccountSelected = { id -> navControllerInner.navigate("account_detail/$id") },
                onEditAccount = { id -> navControllerInner.navigate("edit_account/$id") }
            )
        }
        composable("add_account") {
            AddEditAccountScreen(
                accountId = null,
                onNavigateBack = { navControllerInner.popBackStack() },
                onSave = { navControllerInner.popBackStack() }
            )
        }
        composable("edit_account/{accountId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("accountId")?.toIntOrNull()
            AddEditAccountScreen(
                accountId = id,
                onNavigateBack = { navControllerInner.popBackStack() },
                onSave = { navControllerInner.popBackStack() }
            )
        }
        composable("account_detail/{accountId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("accountId")?.toIntOrNull() ?: 0
            AccountDetailScreen(
                accountId = id,
                onNavigateBack = { navControllerInner.popBackStack() },
                onAddTransaction = { accountId -> navController.navigate("add_transaction?accountId=$accountId") }
            )
        }
        composable("categories") { CategoriesScreen() }
        composable("merchants") { MerchantsScreen() }
        composable("assets") { AssetsScreen() }
        composable("savings") { SavingsScreen() }
        composable("loans") { LoansScreen() }
        composable("lend_borrow") { LendBorrowScreen() }
        composable("bill_reminders") { BillRemindersScreen() }
        composable("notes") {
            NotesListScreen(
                onAddNote = { navControllerInner.navigate("notes_add") },
                onEditNote = { id -> navControllerInner.navigate("notes_edit/$id") }
            )
        }
        composable("notes_add") {
            AddEditNoteScreen(
                noteId = null,
                onNavigateBack = { navControllerInner.popBackStack() }
            )
        }
        composable("notes_edit/{noteId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            AddEditNoteScreen(
                noteId = id,
                onNavigateBack = { navControllerInner.popBackStack() }
            )
        }
        composable("todos") { TodosScreen() }
        composable("warranties") { WarrantiesScreen() }
        composable("tags") { TagsScreen() }
        composable("statements") { StatementsScreen() }
        composable("calendar") { CalendarViewScreen() }
        composable("exchange_rates") { ExchangeRatesScreen() }
        composable("calculators") { CalculatorsScreen() }
        composable("backup_restore") { BackupRestoreScreen() }
    }
}

