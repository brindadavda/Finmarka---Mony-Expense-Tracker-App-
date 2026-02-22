package com.appstudio.finmarka.ui.screens.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.model.TransactionStatus
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.components.AppActionTopBar
import com.appstudio.finmarka.ui.components.transactionListItems
import com.appstudio.finmarka.ui.theme.LocalSpacing
import com.appstudio.finmarka.ui.viewmodel.TransactionsViewModel

private enum class TransactionsTab(val title: String) {
    ALL("All"),
    RECURRING("Recurring"),
    REIMBURSEMENT("Reimbursement"),
    TEMPLATES("Templates"),
    EXCLUDED("Excluded"),
    PENDING("Pending")
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onTransactionClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current

    var selectedTab by remember { mutableStateOf(TransactionsTab.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredTransactions = remember(state.transactions, selectedTab, searchQuery) {
        state.transactions
            .asSequence()
            .filter { it.matchesTab(selectedTab) }
            .filter { transaction ->
                val query = searchQuery.trim()
                query.isBlank() || transaction.matchesSearch(query)
            }
            .toList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = spacing.xl, vertical = spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        AppActionTopBar(
            title = "All Transactions",
            onNavigationClick = onNavigateBack
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = { Text("Search transactions...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Button(
                onClick = { showFilterSheet = true },
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.md),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.size(spacing.xs))
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            items(TransactionsTab.entries) { tab ->
                FilterChip(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    label = { Text(tab.title, style = MaterialTheme.typography.labelLarge) },
                    shape = MaterialTheme.shapes.medium,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            TransactionsList(
                transactions = filteredTransactions,
                currencyCode = viewModel.getCurrencyCode,
                onTransactionClick = onTransactionClick,
                onDelete = viewModel::deleteTransaction
            )
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            selectedCategoryId = state.filterCategoryId,
            selectedType = state.filterType,
            categories = state.categories.map { it.id to it.name },
            onDismiss = { showFilterSheet = false },
            onApplyFilter = { categoryId, type ->
                viewModel.setFilter(categoryId, type)
                showFilterSheet = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TransactionsList(
    transactions: List<Transaction>,
    currencyCode: String,
    onTransactionClick: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        transactionListItems(
            transactions = transactions,
            currencyCode = currencyCode,
            onTransactionClick = onTransactionClick,
            showSections = true,
            enableDelete = true,
            onDeleteConfirmed = onDelete
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    selectedCategoryId: Int,
    selectedType: String?,
    categories: List<Pair<Int, String>>,
    onDismiss: () -> Unit,
    onApplyFilter: (Int, String?) -> Unit
) {
    val spacing = LocalSpacing.current
    var tempCategoryId by remember(selectedCategoryId) { mutableStateOf(selectedCategoryId) }
    var tempType by remember(selectedType) { mutableStateOf(selectedType) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Filter Transactions", style = MaterialTheme.typography.headlineSmall)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text("Category", style = MaterialTheme.typography.titleMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                item {
                    FilterChip(
                        selected = tempCategoryId == -1,
                        onClick = { tempCategoryId = -1 },
                        label = { Text("All") }
                    )
                }
                items(categories) { (id, name) ->
                    FilterChip(
                        selected = tempCategoryId == id,
                        onClick = { tempCategoryId = id },
                        label = { Text(name) }
                    )
                }
            }

            Text("Type", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                FilterChip(
                    selected = tempType == null,
                    onClick = { tempType = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = tempType == TransactionType.INCOME.name,
                    onClick = { tempType = TransactionType.INCOME.name },
                    label = { Text("Income") }
                )
                FilterChip(
                    selected = tempType == TransactionType.EXPENSE.name,
                    onClick = { tempType = TransactionType.EXPENSE.name },
                    label = { Text("Expense") }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.sm, bottom = spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = { onApplyFilter(tempCategoryId, tempType) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

private fun Transaction.matchesSearch(query: String): Boolean {
    val normalizedQuery = query.lowercase()
    return categoryName.lowercase().contains(normalizedQuery) ||
        note.orEmpty().lowercase().contains(normalizedQuery) ||
        paymentMode.name.lowercase().contains(normalizedQuery)
}

private fun Transaction.matchesTab(tab: TransactionsTab): Boolean {
    return when (tab) {
        TransactionsTab.ALL -> true
        TransactionsTab.RECURRING -> isRecurring
        TransactionsTab.REIMBURSEMENT -> isReimbursement
        TransactionsTab.TEMPLATES -> isTemplate
        TransactionsTab.EXCLUDED -> isExcluded || status == TransactionStatus.EXCLUDED
        TransactionsTab.PENDING -> status == TransactionStatus.PENDING
    }
}

