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

enum class StatsFilter { TODAY, WEEK, MONTH, YEAR }

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

    val chartData = remember(state.transactions, selectedFilter) {
        buildChartData(state.transactions, selectedFilter)
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

        Text(text = "📊 Statistics Report", style = MaterialTheme.typography.titleMedium)
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
                val startOfWeek = (now.clone() as Calendar).apply {
                    firstDayOfWeek = Calendar.MONDAY
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val endOfWeek = (startOfWeek.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_MONTH, 7)
                }
                tx.timeInMillis >= startOfWeek.timeInMillis && tx.timeInMillis < endOfWeek.timeInMillis
            }

            StatsFilter.MONTH -> tx.get(Calendar.YEAR) == now.get(Calendar.YEAR)

            StatsFilter.YEAR -> tx.get(Calendar.YEAR) in 2020..now.get(Calendar.YEAR)
        }
    }
}



private fun buildChartData(
    transactions: List<Transaction>,
    filter: StatsFilter
): List<ReportItem> {
    val now = Calendar.getInstance()
    val grouped = mutableMapOf<String, Float>()

    val labels = when (filter) {
        StatsFilter.TODAY -> (0..23).map { hour -> String.format("%02d:00-%02d:00", hour, (hour + 1) % 24) }
        StatsFilter.WEEK -> listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        StatsFilter.MONTH -> listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        StatsFilter.YEAR -> (2020..now.get(Calendar.YEAR)).map { it.toString() }
    }

    transactions.forEach { transaction ->
        val calendar = Calendar.getInstance().apply { timeInMillis = transaction.dateTime }
        val key = when (filter) {
            StatsFilter.TODAY -> {
                if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                ) {
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    String.format("%02d:00-%02d:00", hour, (hour + 1) % 24)
                } else null
            }

            StatsFilter.WEEK -> {
                val startOfWeek = (now.clone() as Calendar).apply {
                    firstDayOfWeek = Calendar.MONDAY
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val endOfWeek = (startOfWeek.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 7) }
                if (calendar.timeInMillis in startOfWeek.timeInMillis until endOfWeek.timeInMillis) {
                    when (calendar.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> "Monday"
                        Calendar.TUESDAY -> "Tuesday"
                        Calendar.WEDNESDAY -> "Wednesday"
                        Calendar.THURSDAY -> "Thursday"
                        Calendar.FRIDAY -> "Friday"
                        Calendar.SATURDAY -> "Saturday"
                        else -> "Sunday"
                    }
                } else null
            }

            StatsFilter.MONTH -> {
                if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                    when (calendar.get(Calendar.MONTH)) {
                        Calendar.JANUARY -> "January"
                        Calendar.FEBRUARY -> "February"
                        Calendar.MARCH -> "March"
                        Calendar.APRIL -> "April"
                        Calendar.MAY -> "May"
                        Calendar.JUNE -> "June"
                        Calendar.JULY -> "July"
                        Calendar.AUGUST -> "August"
                        Calendar.SEPTEMBER -> "September"
                        Calendar.OCTOBER -> "October"
                        Calendar.NOVEMBER -> "November"
                        else -> "December"
                    }
                } else null
            }

            StatsFilter.YEAR -> {
                val y = calendar.get(Calendar.YEAR)
                if (y in 2020..now.get(Calendar.YEAR)) y.toString() else null
            }
        }

        if (key != null) {
            grouped[key] = (grouped[key] ?: 0f) + transaction.amount.toFloat()
        }
    }

    return labels.map { label -> ReportItem(label = label, amount = grouped[label] ?: 0f) }
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
