package com.appstudio.finmarka.ui.screens.reports

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.model.ReportItem
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.components.ActionCard
import com.appstudio.finmarka.ui.components.AppActionTopBar
import com.appstudio.finmarka.ui.screens.reports.components.BarChart
import com.appstudio.finmarka.ui.theme.ExpenseRed
import com.appstudio.finmarka.ui.theme.IncomeGreen
import com.appstudio.finmarka.ui.theme.LocalSpacing
import com.appstudio.finmarka.ui.viewmodel.ReportsViewModel
import java.util.Calendar

enum class StatsFilter { TODAY, WEEK, MONTH, YEAR, ALL }

@Composable
fun ReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf(StatsFilter.MONTH) }

    val filteredTransactions = remember(state.transactions, selectedFilter) {
        filterTransactions(state.transactions, selectedFilter)
    }

    val totalIncome = filteredTransactions
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }
    val totalExpense = filteredTransactions
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }

    val reportData = remember(filteredTransactions) {
        filteredTransactions.map { tx ->
            ReportItem(
                label = tx.categoryName,
                amount = tx.amount.toFloat()
            )
        }
    }

    val chartData = remember(filteredTransactions, selectedFilter) {
        buildChartData(filteredTransactions, selectedFilter)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars))
            .padding(horizontal = spacing.lg)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        AppActionTopBar(title = "Reports", onNavigationClick = onNavigateBack)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            ReportCard(
                modifier = Modifier.weight(1f),
                title = "Total Income",
                value = viewModel.formatWithPrefCurrency(totalIncome),
                color = IncomeGreen
            )

            ReportCard(
                modifier = Modifier.weight(1f),
                title = "Total Expense",
                value = viewModel.formatWithPrefCurrency(totalExpense),
                color = ExpenseRed
            )
        }

        Text(
            text = "Filter by period",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
        ) {
            StatsFilter.values().forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Text(text = "Statistics", style = MaterialTheme.typography.titleMedium)
        BarChart(data = chartData)

        Text(text = "Export Reports", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            ActionCard(
                title = "CSV",
                icon = Icons.Default.FileDownload,
                onClick = { ExportHelper.exportCSV(context, reportData) },
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                title = "Excel",
                icon = Icons.Default.FileDownload,
                onClick = { ExportHelper.exportExcel(context, reportData) },
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                title = "PDF",
                icon = Icons.Default.PictureAsPdf,
                onClick = {
                    ExportHelper.exportPDF(
                        context = context,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        data = reportData
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(spacing.xl))
    }
}

private fun filterTransactions(transactions: List<Transaction>, filter: StatsFilter): List<Transaction> {
    val now = Calendar.getInstance()
    return transactions.filter { transaction ->
        val tx = Calendar.getInstance().apply { timeInMillis = transaction.dateTime }
        when (filter) {
            StatsFilter.TODAY ->
                tx.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    tx.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

            StatsFilter.WEEK -> {
                val diffDays = ((now.timeInMillis - tx.timeInMillis) / (1000L * 60L * 60L * 24L)).toInt()
                diffDays in 0..6
            }

            StatsFilter.MONTH ->
                tx.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    tx.get(Calendar.MONTH) == now.get(Calendar.MONTH)

            StatsFilter.YEAR -> tx.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            StatsFilter.ALL -> true
        }
    }
}


private fun buildChartData(
    transactions: List<Transaction>,
    filter: StatsFilter
): List<ReportItem> {
    val grouped = mutableMapOf<String, Float>()

    transactions.forEach { transaction ->
        val calendar = Calendar.getInstance().apply { timeInMillis = transaction.dateTime }
        val key = when (filter) {
            StatsFilter.TODAY -> String.format("%02d:00", calendar.get(Calendar.HOUR_OF_DAY))
            StatsFilter.WEEK -> calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, java.util.Locale.getDefault()) ?: "Day"
            StatsFilter.MONTH -> "W${(calendar.get(Calendar.DAY_OF_MONTH) - 1) / 7 + 1}"
            StatsFilter.YEAR -> calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault()) ?: "Month"
            StatsFilter.ALL -> calendar.get(Calendar.YEAR).toString()
        }
        grouped[key] = (grouped[key] ?: 0f) + transaction.amount.toFloat()
    }

    val orderedKeys = when (filter) {
        StatsFilter.TODAY -> grouped.keys.sorted()
        StatsFilter.WEEK -> listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").filter { it in grouped.keys }
        StatsFilter.MONTH -> grouped.keys.sortedBy { it.removePrefix("W").toIntOrNull() ?: 0 }
        StatsFilter.YEAR -> listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec").filter { it in grouped.keys }
        StatsFilter.ALL -> grouped.keys.sorted()
    }

    return orderedKeys.map { ReportItem(label = it, amount = grouped[it] ?: 0f) }
}

@Composable
fun ReportCard(
    modifier: Modifier,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(spacing.md)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(text = value, style = MaterialTheme.typography.titleLarge, color = color)
        }
    }
}
