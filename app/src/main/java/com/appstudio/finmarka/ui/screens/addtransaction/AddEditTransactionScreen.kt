package com.appstudio.finmarka.ui.screens.addtransaction

import android.accounts.Account
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.local.entity.AccountEntity
import com.appstudio.finmarka.data.model.TransactionStatus
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Category
import com.appstudio.finmarka.ui.components.AppActionTopBar
import com.appstudio.finmarka.ui.components.AppCard
import com.appstudio.finmarka.ui.components.AppTextField
import com.appstudio.finmarka.ui.theme.FinMarkElevation
import com.appstudio.finmarka.ui.theme.LocalSpacing
import com.appstudio.finmarka.ui.util.formatDate
import com.appstudio.finmarka.ui.viewmodel.AddEditTransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddEditTransactionScreen(
    onSaved: () -> Unit,
    onAddAccount: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AddEditTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current

    if (state.saveSuccess) {
        viewModel.clearSaveSuccess()
        onSaved()
        return
    }

    var showCategorySelector by rememberSaveable { mutableStateOf(false) }
    var showAccountSelector by rememberSaveable { mutableStateOf(false) }
    var showStatusSelector by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = remember(state.dateTime) {
        Calendar.getInstance().apply { timeInMillis = state.dateTime }
    }

    val datePickerDialog = remember(state.dateTime) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val updated = Calendar.getInstance().apply {
                    timeInMillis = state.dateTime
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
                viewModel.setDateTime(updated.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePickerDialog = remember(state.dateTime) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val updated = Calendar.getInstance().apply {
                    timeInMillis = state.dateTime
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                viewModel.setDateTime(updated.timeInMillis)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
    }

    val selectedCategory = remember(state.categoryId, state.categories) {
        state.categories.firstOrNull { it.id == state.categoryId }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            AppActionTopBar(
                title = if (state.isEdit) "Edit Transaction" else "Add Transaction",
                actionText = if (state.isEdit) "Update" else "Save",
                onNavigationClick = onNavigateBack,
                onActionClick = { viewModel.save() },
                actionEnabled = state.isActionEnabled
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.lg, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            AmountSection(
                amount = state.amount,
                currency = state.currency,
                onAmountChange = viewModel::setAmount
            )

            TransactionTypeSegment(
                selectedType = state.type,
                onTypeSelected = viewModel::setType
            )

            CategorySelectorField(
                selectedCategory = selectedCategory,
                expanded = showCategorySelector,
                showError = state.error == "Select a category",
                onClick = { showCategorySelector = !showCategorySelector }
            )

            AnimatedVisibility(
                visible = showCategorySelector,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CategoryGrid(
                    categories = state.categories,
                    selectedCategoryId = state.categoryId,
                    onCategorySelected = {
                        viewModel.setCategoryId(it)
                        showCategorySelector = false
                    }
                )
            }

            AccountSelectorField(
                accounts = state.accounts,
                selectedAccountId = state.accountId,
                accountNameFallback = state.accountName,
                onAccountSelected = { viewModel.setAccountId(it) },
                onAddAccount = onAddAccount,
                expanded = showAccountSelector
            )

            StatusSelectorField(
                selectedStatus = state.status,
                expanded = showStatusSelector,
                onStatusSelected = { viewModel.setStatus(it) },
                onExpandedChange = { showStatusSelector = it }
            )

            DateTimeSelector(
                dateText = formatDate(state.dateTime),
                timeText = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(state.dateTime)),
                onDateClick = { datePickerDialog.show() },
                onTimeClick = { timePickerDialog.show() }
            )

            AppTextField(
                value = state.merchantName,
                onValueChange = viewModel::setMerchantName,
                label = "Merchant (Optional)",
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = state.note,
                onValueChange = viewModel::setNote,
                label = "Notes",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false
            )

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Make Recurring",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = state.isRecurring,
                        onCheckedChange = { viewModel.toggleRecurring() }
                    )
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.padding(spacing.xs))
        }
    }
}

@Composable
private fun AmountSection(
    amount: String,
    currency: String,
    onAmountChange: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    val currencySymbol = when (currency) {
        "INR" -> "₹"
        "EUR" -> "€"
        "GBP" -> "£"
        else -> "$"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Text(
                text = "Amount",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = FinMarkElevation.sm
        ) {
            BasicTextField(
                value = amount,
                onValueChange = onAmountChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MaterialTheme.typography.displaySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg, vertical = spacing.xl),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Text(
                            text = currencySymbol,
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (amount.isBlank()) {
                            Text(
                                text = "0.00",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            innerTextField()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun TransactionTypeSegment(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    val spacing = LocalSpacing.current
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            listOf(TransactionType.EXPENSE, TransactionType.INCOME, TransactionType.SAVINGS).forEach { type ->
                val selected = selectedType == type
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTypeSelected(type) },
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = if (selected) FinMarkElevation.md else FinMarkElevation.none,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = spacing.md),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySelectorField(
    selectedCategory: Category?,
    expanded: Boolean,
    showError: Boolean,
    onClick: () -> Unit
) {
    val titleColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Column(verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.xs)) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCategory?.name ?: "Select category",
                    style = MaterialTheme.typography.titleMedium,
                    color = titleColor
                )
                Icon(
                    imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse accounts" else "Expand accounts",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (showError) {
            Text(
                text = "Please select at least one category",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<Category>,
    selectedCategoryId: Int,
    onCategorySelected: (Int) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        categories.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                rowItems.forEach { category ->
                    val selected = selectedCategoryId == category.id
                    AppCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onCategorySelected(category.id) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (category.icon.isBlank()) "•" else category.icon,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(spacing.md)
                                )
                            }
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                        }
                    }
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DateTimeSelector(
    dateText: String,
    timeText: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        DateTimeChip(
            title = "Date",
            value = dateText,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.weight(1f),
            onClick = onDateClick
        )
        DateTimeChip(
            title = "Time",
            value = timeText,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.weight(1f),
            onClick = onTimeClick
        )
    }
}

@Composable
private fun DateTimeChip(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSelectorField(
    accounts: List<AccountEntity>,       // List of accounts from ViewModel
    selectedAccountId: Int?,             // Currently selected account ID
    accountNameFallback: String,         // Fallback if accountId not found
    onAccountSelected: (Int) -> Unit,
    expanded: Boolean,
    onAddAccount: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Get the name of the selected account
    val selectedAccountName = accounts.firstOrNull { it.id == selectedAccountId }?.name
        ?: accountNameFallback

    Column(verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.xs)) {
        Text(
            text = "Account",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded } // toggle dropdown
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedAccountName.ifBlank { "Select account" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse accounts" else "Expand accounts",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Show all accounts
                accounts.forEach { account ->
                    DropdownMenuItem(
                        text = { Text(account.name) },
                        onClick = {
                            onAccountSelected(account.id)
                            expanded = false
                        }
                    )
                }

                // Add "Add Account" option
                DropdownMenuItem(
                    text = { Text("Add Account") },
                    onClick = {
                        expanded = false
                        onAddAccount()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusSelectorField(
    selectedStatus: TransactionStatus?,            // Currently selected status
    expanded: Boolean,                             // Whether dropdown is open
    onStatusSelected: (TransactionStatus) -> Unit,
    onExpandedChange: (Boolean) -> Unit           // Toggle dropdown
) {
    val spacing = LocalSpacing.current

    // Display name for selected status
    val displayText = selectedStatus?.name?.lowercase()
        ?.replaceFirstChar { it.uppercase() } ?: "Select status"

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Text(
            text = "Status",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange(!expanded) }
        ) {
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!expanded) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse status" else "Expand status",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                TransactionStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = status.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            onStatusSelected(status)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}