package com.appstudio.finmarka.ui.screens.addtransaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.model.TransactionStatus
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.ui.viewmodel.AddEditTransactionViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    onSaved: () -> Unit,
    onAddAccount: () -> Unit,
    viewModel: AddEditTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    if (state.saveSuccess) {
        viewModel.clearSaveSuccess()
        onSaved()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars))
            .padding(16.dp)
    ) {
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
            ).apply {
                datePicker.maxDate = Long.MAX_VALUE
            }
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
        Text(
            text = if (state.isEdit) "Edit Transaction" else "Add Transaction",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.amount,
            onValueChange = { viewModel.setAmount(it) },
            label = { Text("Amount") },
            isError = state.error != null,
            supportingText = state.error?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        val selectedAccountName = state.accounts.firstOrNull { it.id == state.accountId }?.name
            ?: state.accountName
        var accountExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = accountExpanded,
            onExpandedChange = { accountExpanded = !accountExpanded }
        ) {
            OutlinedTextField(
                value = if (selectedAccountName.isBlank()) "Select account" else selectedAccountName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Account") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = accountExpanded,
                onDismissRequest = { accountExpanded = false }
            ) {
                state.accounts.forEach { account ->
                    DropdownMenuItem(
                        text = { Text(account.name) },
                        onClick = {
                            viewModel.setAccountId(account.id)
                            accountExpanded = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Add account") },
                    onClick = {
                        accountExpanded = false
                        onAddAccount()
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.merchantName,
            onValueChange = { viewModel.setMerchantName(it) },
            label = { Text("Merchant / Source") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Type", style = MaterialTheme.typography.labelMedium,  color = MaterialTheme.colorScheme.onPrimaryContainer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.type == TransactionType.INCOME,
                    onClick = { viewModel.setType(TransactionType.INCOME) }
                )
                Text("Income",  color = MaterialTheme.colorScheme.onPrimaryContainer , modifier = Modifier.clickable { viewModel.setType(TransactionType.INCOME) })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.type == TransactionType.EXPENSE,
                    onClick = { viewModel.setType(TransactionType.EXPENSE) }
                )
                Text("Expense",  color = MaterialTheme.colorScheme.onPrimaryContainer , modifier = Modifier.clickable { viewModel.setType(TransactionType.EXPENSE) })
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        val filteredCategories = state.categories.filter { it.type == state.type }
        Text("Category", style = MaterialTheme.typography.labelMedium,  color = MaterialTheme.colorScheme.onPrimaryContainer)
        filteredCategories.forEach { cat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setCategoryId(cat.id) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = state.categoryId == cat.id,
                    onClick = { viewModel.setCategoryId(cat.id) }
                )
                Text(cat.name,  color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.tags.joinToString(),
            onValueChange = { input ->
                val tags = input.split(",").map { it.trim() }.filter { it.isNotBlank() }
                viewModel.setTags(tags)
            },
            label = { Text("Tags (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.attachments.joinToString(),
            onValueChange = { input ->
                val attachments = input.split(",").map { it.trim() }.filter { it.isNotBlank() }
                viewModel.setAttachments(attachments)
            },
            label = { Text("Attachments (URIs)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Date & time: ${com.appstudio.finmarka.ui.util.formatDateTimeTravel(state.dateTime)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { datePickerDialog.show() }) {
                Text("Select date")
            }
            OutlinedButton(onClick = { timePickerDialog.show() }) {
                Text("Select time")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Status", style = MaterialTheme.typography.labelMedium,  color = MaterialTheme.colorScheme.onPrimaryContainer)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TransactionStatus.entries.forEach { status ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.status == status,
                        onClick = { viewModel.setStatus(status) }
                    )
                    Text(status.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Recurring Transaction", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (state.isRecurring) "Enabled" else "Disabled")
                Switch(
                    checked = state.isRecurring,
                    onCheckedChange = { viewModel.toggleRecurring() }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.note,
            onValueChange = { viewModel.setNote(it) },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.save() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isEdit) "Update" else "Save")
        }
    }
}
