package com.appstudio.finmarka.ui.screens.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.model.TransactionStatus
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.theme.DashboardAccentEnd
import com.appstudio.finmarka.ui.theme.DashboardAccentStart
import com.appstudio.finmarka.ui.theme.DashboardCardSurface
import com.appstudio.finmarka.ui.theme.FinMarkElevation
import com.appstudio.finmarka.ui.theme.LocalSpacing
import com.appstudio.finmarka.ui.util.formatDate
import com.appstudio.finmarka.ui.util.formatDateTimeTravel
import com.appstudio.finmarka.ui.viewmodel.TransactionsViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private enum class TransactionsTab(val title: String) {
    ALL("All"),
    RECURRING("Recurring"),
    REIMBURSEMENT("Reimbursement"),
    TEMPLATES("Templates"),
    EXCLUDED("Excluded"),
    PENDING("Pending")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onTransactionClick: (Int) -> Unit,
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
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = spacing.lg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = spacing.md, bottom = spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "All Transactions",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { showFilterSheet = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

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
                Icon(imageVector = Icons.Default.FilterList, contentDescription = null)
                Spacer(modifier = Modifier.size(spacing.xs))
                Text("Filter")
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

@Composable
private fun TransactionsList(
    transactions: List<Transaction>,
    currencyCode: String,
    onTransactionClick: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    val spacing = LocalSpacing.current
    val groupedTransactions = remember(transactions) {
        transactions
            .sortedByDescending { it.dateTime }
            .groupBy { Instant.ofEpochMilli(it.dateTime).atZone(ZoneId.systemDefault()).toLocalDate() }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        groupedTransactions.forEach { (date, dailyTransactions) ->
            item(key = "header-${date}") {
                DateHeader(date = date)
            }
            items(dailyTransactions, key = { transaction -> transaction.id }) { transaction ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                            onDelete(transaction.id)
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
                                .padding(horizontal = spacing.md),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                ) {
                    TransactionRow(
                        transaction = transaction,
                        currencyCode = currencyCode,
                        onClick = { onTransactionClick(transaction.id) },
                        onDelete = { onDelete(transaction.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    val today = LocalDate.now()
    val title = when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> formatDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = LocalSpacing.current.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formatDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    currencyCode: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val spacing = LocalSpacing.current
    val cardBrush = Brush.horizontalGradient(
        colors = listOf(
            DashboardCardSurface.copy(alpha = 0.55f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = FinMarkElevation.sm),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            width = spacing.xs / 2,
            brush = Brush.horizontalGradient(listOf(DashboardAccentStart.copy(alpha = 0.45f), DashboardAccentEnd.copy(alpha = 0.45f)))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush)
                .padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.xxxl)
                    .background(
                        color = transaction.displayColor.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type == TransactionType.INCOME) Icons.Default.SouthWest else Icons.Default.NorthEast,
                    contentDescription = null,
                    tint = transaction.displayColor
                )
            }
            Spacer(modifier = Modifier.size(spacing.md))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = transaction.categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.size(spacing.sm))
                    Text(
                        text = transaction.displayAmount(currencyCode),
                        style = MaterialTheme.typography.titleLarge,
                        color = transaction.displayColor
                    )
                }
                Text(
                    text = "${transaction.paymentMode.name.lowercase().replaceFirstChar { it.titlecase() }} · ${formatDateTimeTravel(transaction.dateTime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    transaction.tagsForUi().take(2).forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag) },
                            shape = MaterialTheme.shapes.small,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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

private fun Transaction.tagsForUi(): List<String> = buildList {
    add(status.name.lowercase().replaceFirstChar { it.titlecase() })
    if (isRecurring) add("Recurring")
    if (isReimbursement) add("Reimbursement")
    if (isTemplate) add("Template")
    if (isExcluded) add("Excluded")
}
