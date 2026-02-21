package com.appstudio.finmarka.ui.screens.settings

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.R
import com.appstudio.finmarka.ui.viewmodel.SettingsViewModel

data class SettingsModuleItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val route: String
)

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToCategories: () -> Unit,
    onNavigateToBackupRestore: () -> Unit,
    onNavigateToAppLock: () -> Unit,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currency by viewModel.currency.collectAsState()
    val theme by viewModel.theme.collectAsState()

    val version = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (_: PackageManager.NameNotFoundException) {
        "1.0.0"
    }

    val themeValue = when (theme) {
        null -> "System"
        true -> "Dark"
        false -> "Light"
    }

    val currencies = listOf("USD", "INR", "EUR", "GBP")
    val movedModules = listOf(
        SettingsModuleItem(Icons.Default.AccountBalanceWallet, "Accounts", "Manage cash, bank, and wallet accounts", "accounts"),
        SettingsModuleItem(Icons.Default.CreditCard, "Budget", "Plan monthly spending and alerts", "budget"),
        SettingsModuleItem(Icons.Default.Person, "Merchants", "Sources, payees, and vendors", "merchants"),
        SettingsModuleItem(Icons.Default.Wallet, "Assets", "Track assets and net worth", "assets"),
        SettingsModuleItem(Icons.Default.Payments, "Loans", "Loan schedules and balances", "loans"),
        SettingsModuleItem(Icons.Default.EventNote, "Bill Reminders", "Upcoming and recurring bills", "bill_reminders"),
        SettingsModuleItem(Icons.Default.NoteAlt, "Notes", "Attach notes to finance items", "notes"),
        SettingsModuleItem(Icons.Default.Task, "Todos", "Tasks and due reminders", "todos"),
        SettingsModuleItem(Icons.Default.Style, "Tags", "Friends and business tags", "tags"),
        SettingsModuleItem(Icons.Default.ReceiptLong, "Statements", "Export and share statements", "statements"),
        SettingsModuleItem(Icons.Default.CalendarToday, "Calendar", "Daily expenses calendar", "calendar"),
        SettingsModuleItem(Icons.Default.Loop, "Exchange Rates", "Multi-currency conversions", "exchange_rates"),
        SettingsModuleItem(Icons.Default.TipsAndUpdates, "Calculators", "EMI, loan and savings tools", "calculators"),
        SettingsModuleItem(Icons.Outlined.Backup, "Templates", "Reusable transaction templates", "templates")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A2C4B)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "App icon",
                            modifier = Modifier.size(46.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        Text("Managing your finances smartly", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
                        Text("${stringResource(R.string.app_name)} v$version", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        SettingPanel(title = "Theme", icon = Icons.Outlined.LightMode) {
            ThemeSelectionRow(selectedTheme = themeValue) { selected ->
                viewModel.setTheme(
                    when (selected) {
                        "System" -> null
                        "Dark" -> true
                        "Light" -> false
                        else -> null
                    }
                )
            }
        }

        SettingPanel(title = "Currency", icon = Icons.Outlined.Language) {
            CurrencyDropdown(
                selectedCurrency = currency,
                currencies = currencies,
                onCurrencySelected = viewModel::setCurrency
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                SettingsActionRow(Icons.Outlined.Category, "Categories", "Manage your income and expense groups", onNavigateToCategories)
                SettingsActionRow(Icons.Outlined.Backup, "Backup & Restore", "Export or import your financial data", onNavigateToBackupRestore)
                SettingsActionRow(Icons.Outlined.Lock, "App Lock", "Secure app with PIN and biometrics", onNavigateToAppLock)
                SettingsActionRow(Icons.Outlined.SettingsSuggest, "About Us", "Learn more about Finmarka", onNavigateToAboutUs)
                SettingsActionRow(Icons.Outlined.Policy, "Privacy Policy", "How your local data is handled", onNavigateToPrivacyPolicy)
                SettingsActionRow(Icons.Outlined.Gavel, "Terms & Conditions", "Usage terms and policies", onNavigateToTerms)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "More modules (moved from More tab)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                )
                movedModules.forEach { item ->
                    SettingsActionRow(item.icon, item.title, item.subtitle) {
                        onNavigateToRoute(item.route)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingPanel(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
            content()
        }
    }
}

@Composable
private fun ThemeSelectionRow(selectedTheme: String, onThemeSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ThemeOptionButton("Light", Icons.Outlined.LightMode, selectedTheme == "Light", { onThemeSelected("Light") }, Modifier.weight(1f))
        ThemeOptionButton("Dark", Icons.Outlined.DarkMode, selectedTheme == "Dark", { onThemeSelected("Dark") }, Modifier.weight(1f))
        ThemeOptionButton("System", Icons.Outlined.Computer, selectedTheme == "System", { onThemeSelected("System") }, Modifier.weight(1f))
    }
}

@Composable
private fun ThemeOptionButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = contentColor)
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun SettingsActionRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(imageVector = Icons.Outlined.ArrowForwardIos, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
    }
}

@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    currencies: List<String>,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = currencyLabel(selectedCurrency), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currencyLabel(currency)) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun currencyLabel(code: String): String {
    return when (code) {
        "USD" -> "$ US Dollar (USD)"
        "INR" -> "₹ Indian Rupee (INR)"
        "EUR" -> "€ Euro (EUR)"
        "GBP" -> "£ British Pound (GBP)"
        else -> code
    }
}
