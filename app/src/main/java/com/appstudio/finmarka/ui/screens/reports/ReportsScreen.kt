package com.appstudio.finmarka.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.ui.screens.reports.components.ExportButtons
import com.appstudio.finmarka.ui.theme.ExpenseRed
import com.appstudio.finmarka.ui.theme.IncomeGreen
import com.appstudio.finmarka.ui.util.formatCurrency
import com.appstudio.finmarka.ui.viewmodel.ReportsViewModel

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val reportData = viewModel.getReportData()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(20.dp)
    ) {

        // ✅ Title
        Text(
            text = "Reports & Analytics",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ReportCard(
                modifier = Modifier.weight(1f),
                title = "Total Income",
                value = viewModel.formatWithPrefCurrency(state.totalIncome),
                color = IncomeGreen
            )

            ReportCard(
                modifier = Modifier.weight(1f),
                title = "Total Expense",
                value = viewModel.formatWithPrefCurrency(state.totalExpense),
                color = ExpenseRed
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ Chart Section
        Text(
            text = "Monthly Chart",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        ReportChart(
            income = state.totalIncome.toFloat(),
            expense = state.totalExpense.toFloat()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Export Buttons
        ExportButtons(
            context = context,
            totalIncome = state.totalIncome,
            totalExpense = state.totalExpense,
            reportData = reportData
        )

    }
}



@Composable
fun ReportCard(
    modifier: Modifier,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color
            )
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
    color: androidx.compose.ui.graphics.Color
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
