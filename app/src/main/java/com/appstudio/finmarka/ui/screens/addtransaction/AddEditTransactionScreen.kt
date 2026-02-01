package com.appstudio.finmarka.ui.screens.addtransaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.model.PaymentMode
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.ui.viewmodel.AddEditTransactionViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    onSaved: () -> Unit,
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
            .verticalScroll(rememberScrollState())
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
        Text("Payment mode", style = MaterialTheme.typography.labelMedium,  color = MaterialTheme.colorScheme.onPrimaryContainer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaymentMode.entries.forEach { mode ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setPaymentMode(mode) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.paymentMode == mode)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = mode.displayName,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
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
