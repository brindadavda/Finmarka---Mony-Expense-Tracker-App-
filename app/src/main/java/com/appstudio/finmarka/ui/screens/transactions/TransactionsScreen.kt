package com.appstudio.finmarka.ui.screens.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.theme.ExpenseRed
import com.appstudio.finmarka.ui.util.formatDateTimeTravel
import com.appstudio.finmarka.data.model.TransactionStatus
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.ui.viewmodel.TransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onTransactionClick: (Int) -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var categoryExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "All",
        "Recurring",
        "Reimbursement",
        "Templates",
        "Excluded",
        "Pending"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars))
    ) {
        Text(
            text = "All Transection",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.categories.find { it.id == state.filterCategoryId }?.name ?: "All categories",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    label = { Text("Category") }
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("All") }, onClick = {
                        viewModel.setFilter(-1, state.filterType)
                        categoryExpanded = false
                    })
                    state.categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                viewModel.setFilter(cat.id, state.filterType)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.filterType ?: "All",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    label = { Text("Type") }
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("All") }, onClick = {
                        viewModel.setFilter(state.filterCategoryId, null)
                        typeExpanded = false
                    })
                    DropdownMenuItem(text = { Text("INCOME") }, onClick = {
                        viewModel.setFilter(state.filterCategoryId, "INCOME")
                        typeExpanded = false
                    })
                    DropdownMenuItem(text = { Text("EXPENSE") }, onClick = {
                        viewModel.setFilter(state.filterCategoryId, "EXPENSE")
                        typeExpanded = false
                    })
                }
            }
        }
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val filteredByTab = state.transactions.filter { transaction ->
                when (tabs[selectedTab]) {
                    "Recurring" -> transaction.isRecurring
                    "Reimbursement" -> transaction.isReimbursement
                    "Templates" -> transaction.isTemplate
                    "Excluded" -> transaction.isExcluded || transaction.status == TransactionStatus.EXCLUDED
                    "Pending" -> transaction.status == TransactionStatus.PENDING
                    else -> true
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredByTab, key = { it.id }) { t ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                                viewModel.deleteTransaction(t.id)
                                true
                            } else {
                                false
                            }
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed)
                            }
                        }
                    ) {
                        TransactionRow(
                            transaction = t,
                            onClick = { onTransactionClick(t.id) },
                            onDelete = { viewModel.deleteTransaction(t.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (transaction.type == TransactionType.INCOME) Icons.Default.SouthWest else Icons.Default.NorthEast,
                contentDescription = null,
                tint = transaction.displayColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = transaction.categoryName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDateTimeTravel(transaction.dateTime) + (transaction.note?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val tags = buildList {
                        add(transaction.status.name.lowercase().replaceFirstChar { it.uppercase() })
                        if (transaction.isRecurring) add("Recurring")
                        if (transaction.isReimbursement) add("Reimbursement")
                        if (transaction.isTemplate) add("Template")
                        if (transaction.isExcluded) add("Excluded")
                    }
                    tags.take(3).forEach { tag ->
                        AssistChip(
                            onClick = { },
                            label = { Text(tag) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
            Text(
                text = transaction.displayAmount(viewModel.getCurrencyCode),
                style = MaterialTheme.typography.titleMedium,
                color = transaction.displayColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed)
            }
        }
    }
}
