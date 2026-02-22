package com.appstudio.finmarka.ui.screens.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.appstudio.finmarka.data.model.ReportItem
import com.appstudio.finmarka.ui.theme.IncomeGreen
import com.appstudio.finmarka.ui.theme.LocalSpacing

@Composable
fun BarChart(data: List<ReportItem>) {
    val spacing = LocalSpacing.current
    val maxAmount = data.maxOfOrNull { it.amount } ?: 0f

    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        if (data.isEmpty()) {
            Text(
                text = "No transactions found for selected period",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        data.forEach {
            Text(
                text = "${it.label}: ${it.amount.toInt()}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.md)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (maxAmount == 0f) 0f else it.amount / maxAmount)
                        .height(spacing.md)
                        .background(IncomeGreen)
                )
            }
        }
    }
}
