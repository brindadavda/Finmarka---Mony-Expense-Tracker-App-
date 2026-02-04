package com.appstudio.finmarka.ui.screens.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.local.entity.AccountEntity
import com.appstudio.finmarka.ui.viewmodel.AccountsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountScreen(
    accountId: Int?,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf("") }
    var balance by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }
    var pinned by rememberSaveable { mutableStateOf(false) }
    var excluded by rememberSaveable { mutableStateOf(false) }
    var isCreditCard by rememberSaveable { mutableStateOf(false) }
    var creditLimit by rememberSaveable { mutableStateOf("") }
    var billingDay by rememberSaveable { mutableStateOf("") }
    var gracePeriod by rememberSaveable { mutableStateOf("") }
    var billAlerts by rememberSaveable { mutableStateOf(false) }
    var currency by rememberSaveable { mutableStateOf("INR") }
    var selectedColor by rememberSaveable { mutableStateOf(accountColorOptions.first().hex) }
    var selectedIcon by rememberSaveable { mutableStateOf(accountIconOptions.first().iconName) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(accountId) {
        if (accountId != null) {
            viewModel.getAccountById(accountId)?.let { account ->
                name = account.name
                balance = account.balance.toString()
                details = account.details.orEmpty()
                pinned = account.pinned
                excluded = account.excluded
                isCreditCard = account.isCreditCard
                creditLimit = account.creditLimit?.toString().orEmpty()
                billingDay = account.billingDay?.toString().orEmpty()
                gracePeriod = account.gracePeriod?.toString().orEmpty()
                billAlerts = account.billAlerts
                currency = account.currency
                selectedColor = account.colorHex
                selectedIcon = account.iconName
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (accountId == null) "Add Account" else "Edit Account") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (name.isBlank()) return@FloatingActionButton
                    if (isCreditCard && creditLimit.isBlank()) return@FloatingActionButton
                    val account = AccountEntity(
                        id = accountId ?: 0,
                        name = name.trim(),
                        balance = balance.toDoubleOrNull() ?: 0.0,
                        details = details.trim().ifBlank { null },
                        currency = currency,
                        colorHex = selectedColor,
                        iconName = selectedIcon,
                        pinned = pinned,
                        excluded = excluded,
                        isCreditCard = isCreditCard,
                        creditLimit = creditLimit.toDoubleOrNull(),
                        billingDay = billingDay.toIntOrNull(),
                        gracePeriod = gracePeriod.toIntOrNull(),
                        billAlerts = billAlerts
                    )
                    coroutineScope.launch {
                        if (accountId == null) {
                            viewModel.addAccount(account)
                        } else {
                            viewModel.updateAccount(account)
                        }
                        onSave()
                    }
                },
                containerColor = Color(0xFFE53935)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Available Balance") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Details (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = pinned, onCheckedChange = { pinned = it })
                Text("Pin to top")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = excluded, onCheckedChange = { excluded = it })
                Text("Exclude from summaries")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isCreditCard, onCheckedChange = { isCreditCard = it })
                Text("Credit Card")
            }
            if (isCreditCard) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = creditLimit,
                    onValueChange = { creditLimit = it },
                    label = { Text("Credit Limit") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = billingDay,
                    onValueChange = { billingDay = it },
                    label = { Text("Billing Day (1-31)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = gracePeriod,
                    onValueChange = { gracePeriod = it },
                    label = { Text("Grace Period (days)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = billAlerts, onCheckedChange = { billAlerts = it })
                    Text("Bill Payment Alerts")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Pick a color", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                accountColorOptions.forEach { option ->
                    val isSelected = option.hex == selectedColor
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { selectedColor = option.hex },
                        shape = CircleShape,
                        color = option.hex.toColor(),
                        tonalElevation = if (isSelected) 4.dp else 0.dp,
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {}
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Pick an icon", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                accountIconOptions.forEach { option ->
                    val isSelected = option.iconName == selectedIcon
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { selectedIcon = option.iconName },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            imageVector = option.imageVector,
                            contentDescription = option.name,
                            modifier = Modifier.padding(10.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Currency", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .wrapContentHeight(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("INR", "USD", "EUR").forEach { option ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                currency = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
