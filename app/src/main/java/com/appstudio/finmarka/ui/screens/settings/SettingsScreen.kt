package com.appstudio.finmarka.ui.screens.settings

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.R
import com.appstudio.finmarka.ui.viewmodel.SettingsViewModel
import androidx.compose.material3.ExposedDropdownMenuBox


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToCategories: () -> Unit,
    onNavigateToBackupRestore: () -> Unit,
    onNavigateToAppLock: () -> Unit,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTerms: () -> Unit
) {
    val context = LocalContext.current
    val currency by viewModel.currency.collectAsState()
    val theme by viewModel.theme.collectAsState()

    val currencies = listOf("USD", "INR", "EUR", "GBP")
    val themes = listOf("System", "Dark", "Light")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- App Icon & Version ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Icon",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            val version = try {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                "1.0.0"
            }
            Text("Finmarka v$version", style = MaterialTheme.typography.bodyMedium)
        }

        Divider()

        Text(
            text = "Font size, theme, and currency preferences are configurable here.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        // --- Currency selector ---
        CurrencyDropdown(
            selectedCurrency = currency,
            currencies = currencies,
            onCurrencySelected = { viewModel.setCurrency(it) }
        )

        ThemeDropdown(
            selectedTheme = when(theme) {
                null -> "System"
                true -> "Dark"
                false -> "Light"
            },
            themes = themes,
            onThemeSelected = { selected ->
                viewModel.setTheme(
                    when(selected) {
                        "System" -> null
                        "Dark" -> true
                        "Light" -> false
                        else -> null
                    }
                )
            }
        )

        Divider()

        // --- Navigation buttons ---
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onNavigateToBackupRestore,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Backup & Restore") }

            OutlinedButton(
                onClick = onNavigateToAppLock,
                modifier = Modifier.fillMaxWidth()
            ) { Text("App Lock") }
        }

        Divider()

        // --- Other options ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onNavigateToAboutUs,
                modifier = Modifier.fillMaxWidth()
            ) { Text("About Us") }

            OutlinedButton(
                onClick = onNavigateToPrivacyPolicy,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Privacy Policy") }

            OutlinedButton(
                onClick = onNavigateToTerms,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Terms & Conditions") }
        }

        Spacer(modifier = Modifier.weight(1f))

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDropdown(
    selectedTheme: String,
    themes: List<String>,
    onThemeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedTheme,
            onValueChange = {},
            readOnly = true,
            label = { Text("Theme") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            themes.forEach { theme ->
                DropdownMenuItem(
                    text = { Text(theme) },
                    onClick = {
                        onThemeSelected(theme)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    currencies: List<String>,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text("Currency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor() // anchors the dropdown correctly
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}
