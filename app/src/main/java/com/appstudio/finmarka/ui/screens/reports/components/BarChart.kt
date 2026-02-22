package com.appstudio.finmarka.ui.screens.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.appstudio.finmarka.data.model.ReportItem
import com.appstudio.finmarka.ui.theme.IncomeGreen
import com.appstudio.finmarka.ui.theme.LocalSpacing

@Composable
fun BarChart(data: List<ReportItem>) {
    val spacing = LocalSpacing.current
    val maxAmount = data.maxOfOrNull { it.amount } ?: 0f

    if (data.isEmpty()) {
        Text(
            text = "No transactions found for selected period",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = spacing.sm, vertical = spacing.md)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.xxxl * 3),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { item ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .width(spacing.lg)
                                .height((spacing.xxxl * 3) * if (maxAmount == 0f) 0f else (item.amount / maxAmount))
                                .clip(MaterialTheme.shapes.small)
                                .background(IncomeGreen)
                        )
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
