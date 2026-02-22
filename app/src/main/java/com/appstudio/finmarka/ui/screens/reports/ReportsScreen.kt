package com.appstudio.finmarka.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.model.ReportItem
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.screens.reports.components.ExportButtons
import com.appstudio.finmarka.ui.theme.ExpenseRed
import com.appstudio.finmarka.ui.theme.IncomeGreen
import com.appstudio.finmarka.ui.viewmodel.ReportsViewModel
import java.util.Calendar

enum class StatsFilter { TODAY, WEEK, MONTH, YEAR, ALL }

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
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

    val reportData = filteredTransactions.map { tx ->
        ReportItem(
            label = "${tx.categoryName} (${tx.type.name})",
            amount = tx.amount.toFloat()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars))
            .padding(20.dp)
    ) {
        Text(
            text = "Reports & Analytics",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Statistics", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(10.dp))

        ReportChart(
            income = totalIncome.toFloat(),
            expense = totalExpense.toFloat()
        )

        Spacer(modifier = Modifier.height(24.dp))

        ExportButtons(
            context = context,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            reportData = reportData
        )
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

@Composable
fun ReportCard(
    modifier: Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, color = color)
        }
    }
}

@Composable
fun ReportChart(income: Float, expense: Float) {
    val maxValue = maxOf(income, expense)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ChartBar("Income", income, maxValue, IncomeGreen)
        ChartBar("Expense", expense, maxValue, ExpenseRed)
    }
}

@Composable
fun ChartBar(
    label: String,
    value: Float,
    maxValue: Float,
    color: Color
) {
    Column {
        Text("$label: ${value.toInt()}")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val fillFraction = if (maxValue == 0f) 0f else value / maxValue
            Box(
                modifier = Modifier
                    .fillMaxWidth(fillFraction)
                    .height(18.dp)
                    .background(color)
            )
        }
    }
}
