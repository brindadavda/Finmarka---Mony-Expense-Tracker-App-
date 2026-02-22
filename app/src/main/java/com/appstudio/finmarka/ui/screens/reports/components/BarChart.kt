package com.appstudio.finmarka.ui.screens.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.appstudio.finmarka.data.model.ReportItem
import com.appstudio.finmarka.ui.screens.reports.StatsFilter
import com.appstudio.finmarka.ui.theme.LocalSpacing
import kotlin.math.ceil

@Composable
fun BarChart(
    data: List<ReportItem>,
    filter: StatsFilter,
    highlightLabel: String? = null
) {
    val spacing = LocalSpacing.current

    if (data.isEmpty()) {
        Text(
            text = "No transactions found for selected period",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val maxData = data.maxOfOrNull { it.amount } ?: 0f
    val axisMax = if (maxData <= 0f) 100 else (ceil(maxData / 100f).toInt() * 100)
    val axisSteps = (axisMax downTo 0 step 100)
    val gridColor = Color(0xFFE5E7EB)
    val axisTextColor = Color(0xFF374151)
    val chartHeight = 220.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        Text(
            text = "Count",
            style = MaterialTheme.typography.labelLarge,
            color = axisTextColor,
            fontWeight = FontWeight.SemiBold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .drawBehind {
                    val lineCount = axisSteps.count() - 1
                    val stepHeight = if (lineCount <= 0) 0f else size.height / lineCount
                    val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    repeat(lineCount + 1) { i ->
                        val y = i * stepHeight
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            pathEffect = dash,
                            strokeWidth = 2f
                        )
                    }
                }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                axisSteps.forEach {
                    Text(
                        text = it.toString().padStart(3, ' '),
                        style = MaterialTheme.typography.labelSmall,
                        color = axisTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = spacing.xl),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { item ->
                    val fraction = (item.amount / axisMax).coerceIn(0f, 1f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        val barColor = colorForValue(filter, fraction, item.label, highlightLabel)
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(chartHeight * fraction)
                                .clip(MaterialTheme.shapes.small)
                                .background(barColor)
                                .border(1.dp, barColor.copy(alpha = 0.75f), MaterialTheme.shapes.small)
                        )
                        Text(
                            text = shortLabel(item.label),
                            style = MaterialTheme.typography.labelSmall,
                            color = axisTextColor,
                            fontWeight = if (highlightLabel == item.label) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = spacing.xs)
                        )
                    }
                }
            }
        }
    }
}

private fun shortLabel(label: String): String {
    return when {
        label.length <= 3 -> label
        label.contains('-') -> label.substringBefore('-')
        else -> label.take(3)
    }
}

private fun colorForValue(filter: StatsFilter, fraction: Float, label: String, highlightLabel: String?): Color {
    return when (filter) {
        StatsFilter.WEEK -> when {
            fraction >= 0.85f -> Color(0xFF16A34A)
            fraction >= 0.70f -> Color(0xFF7C3AED)
            fraction >= 0.55f -> Color(0xFF2563EB)
            fraction >= 0.40f -> Color(0xFF3B82F6)
            fraction >= 0.25f -> Color(0xFF60A5FA)
            fraction >= 0.12f -> Color(0xFF93C5FD)
            else -> Color(0xFFCBD5E1)
        }
        StatsFilter.MONTH -> if (label == highlightLabel) Color(0xFF2563EB) else Color(0xFF60A5FA)
        StatsFilter.YEAR -> if (label == highlightLabel) Color(0xFF2563EB) else Color(0xFF94A3B8)
    }
}
