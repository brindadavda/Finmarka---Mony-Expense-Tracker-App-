package com.appstudio.finmarka.ui.screens.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.R
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.components.AppCard
import com.appstudio.finmarka.ui.components.ListItem
import com.appstudio.finmarka.ui.theme.LocalSpacing
import com.appstudio.finmarka.ui.util.formatDateTimeTravel
import com.appstudio.finmarka.ui.viewmodel.DashboardViewModel
import com.appstudio.finmarka.ui.viewmodel.TransactionsViewModel
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onTransactionClick: (Int) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state by viewModel.uiState.collectAsState()
    val zone = ZoneId.systemDefault()
    val cutoff = LocalDate.now().minusDays(3).atStartOfDay(zone).toInstant().toEpochMilli()
    val recentItems = state.recentTransactions.filter { it.dateTime >= cutoff }.take(4)
    val savingsValue = viewModel.formatWithPrefCurrency(state.totalIncome - state.totalExpense)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = spacing.lg, end = spacing.lg, top = spacing.lg, bottom = spacing.xxxl + spacing.xxxl),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Welcome back", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = androidx.compose.ui.res.stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Net Savings", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(spacing.sm))
                    Text(text = savingsValue, style = MaterialTheme.typography.headlineMedium)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                    ActionCard("Transactions", Icons.Default.List, onNavigateToTransactions, Modifier.weight(1f))
                    ActionCard("Reports", Icons.Default.BarChart, onNavigateToReports, Modifier.weight(1f))
                    ActionCard("Budget", Icons.Default.PieChart, onNavigateToBudget, Modifier.weight(1f))
                }
            }
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToTransactions)
                )
            }
            if (recentItems.isEmpty()) {
                item { AppCard(modifier = Modifier.fillMaxWidth()) { Text("No transactions yet", style = MaterialTheme.typography.bodyLarge) } }
            } else {
                items(recentItems) { transaction ->
                    TransactionItem(transaction = transaction, onClick = { onTransactionClick(transaction.id) })
                }
            }
        }

        FloatingActionButton(
            onClick = onNavigateToAddTransaction,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(spacing.xl),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = MaterialTheme.shapes.large
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add transaction")
        }
    }
}

@Composable
private fun ActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier.clickable(onClick = onClick)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.sm)) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(LocalSpacing.current.xl))
            Text(text = title, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    ListItem(
        title = transaction.categoryName,
        subtitle = formatDateTimeTravel(transaction.dateTime),
        trailing = transaction.displayAmount(viewModel.getCurrencyCode),
        onClick = onClick
    )
}
