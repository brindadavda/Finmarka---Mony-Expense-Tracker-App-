package com.appstudio.finmarka.ui.screens.reports

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.model.ReportItem
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.components.AppActionTopBar
import com.appstudio.finmarka.ui.theme.LocalSpacing
import com.appstudio.finmarka.ui.viewmodel.ReportsViewModel
import java.util.Calendar

private enum class ReportFilter(val title: String) { WEEK("Week"), MONTH("Month"), YEAR("Year") }
private data class ChartPoint(val label: String, val value: Int)

@Composable
fun ReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current
    val context = androidx.compose.ui.platform.LocalContext.current

    val filters = remember { listOf(ReportFilter.WEEK, ReportFilter.MONTH, ReportFilter.YEAR) }
    var selectedFilter by rememberSaveable { mutableStateOf(ReportFilter.WEEK) }

    val filteredTransactions = remember(state.transactions, selectedFilter) {
        filterTransactionsForExport(state.transactions, selectedFilter)
    }

    val totalIncome = filteredTransactions.filter { it.type.name == "INCOME" }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }

    val chartData = remember(state.transactions, selectedFilter) {
        val points = buildChartData(state.transactions, selectedFilter)
        if (filteredTransactions.isEmpty()) defaultChartData(selectedFilter) else points
    }

    val reportData = filteredTransactions.map {
        ReportItem(
            label = "${it.categoryName} (${it.type.name})",
            amount = it.amount.toFloat()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = spacing.xl, vertical = spacing.md)
    ) {
        AppActionTopBar(title = "Reports & Analytics", onNavigationClick = onNavigateBack)

        Spacer(modifier = Modifier.height(spacing.lg))

        FilterTabs(
            filters = filters,
            selected = selectedFilter,
            onSelect = { selectedFilter = it }
        )

        Crossfade(
            targetState = selectedFilter,
            animationSpec = tween(durationMillis = 300),
            modifier = Modifier.padding(top = 0.dp),
            label = "chartFilterAnimation"
        ) {
            CountBarChart(
                data = chartData,
                filter = it,
                showingDefault = filteredTransactions.isEmpty()
            )
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        ExportSection(
            context = context,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            reportData = reportData
        )
    }
}

@Composable
private fun FilterTabs(
    filters: List<ReportFilter>,
    selected: ReportFilter,
    onSelect: (ReportFilter) -> Unit
) {
    val spacing = LocalSpacing.current
    Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
        filters.forEach { filter ->
            val active = selected == filter
            Surface(
                onClick = { onSelect(filter) },
                shape = RoundedCornerShape(100.dp),
                color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                tonalElevation = if (active) 0.dp else 1.dp
            ) {
                Text(
                    text = filter.title,
                    modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.sm),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountBarChart(data: List<ChartPoint>, filter: ReportFilter, showingDefault: Boolean) {
    val spacing = LocalSpacing.current
    var selectedPoint by remember { mutableStateOf<ChartPoint?>(null) }
    var highlightedIndex by rememberSaveable { mutableIntStateOf(-1) }

    val ySteps = (0..45 step 5).toList()
    val yMax = 45

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text(
                text = "Overall Transaction Performance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val chartHeight = 220.dp
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.height(chartHeight),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    ySteps.reversed().forEach { y ->
                        Text(
                            text = y.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(spacing.sm))

                val minChartWidth = when (filter) {
                    ReportFilter.WEEK -> 0.dp
                    ReportFilter.MONTH -> 680.dp
                    ReportFilter.YEAR -> 760.dp
                }

                val chartModifier = if (filter == ReportFilter.WEEK) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier
                        .width(minChartWidth)
                        .horizontalScroll(rememberScrollState())
                }

                Column(modifier = chartModifier) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(chartHeight)
                    ) {
                        Column(
                            modifier = Modifier.matchParentSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            ySteps.reversed().forEach {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            data.forEachIndexed { index, point ->
                                ChartBar(
                                    point = point,
                                    yMax = yMax,
                                    isHighlighted = highlightedIndex == index,
                                    barWidth = 18.dp,
                                    onTap = { selectedPoint = point },
                                    onLongPress = { highlightedIndex = index }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.xs))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        data.forEach { point ->
                            Text(
                                text = point.label,
                                modifier = Modifier.width(18.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            if (showingDefault) {
                Text(
                    text = "No transactions yet — showing sample distribution",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (selectedPoint != null) {
        ModalBottomSheet(onDismissRequest = { selectedPoint = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.xl),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                Text(selectedPoint!!.label, style = MaterialTheme.typography.titleMedium)
                Text("Count: ${selectedPoint!!.value}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(spacing.md))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChartBar(
    point: ChartPoint,
    yMax: Int,
    isHighlighted: Boolean,
    barWidth: Dp,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val barRatio = if (yMax == 0) 0f else (point.value.toFloat() / yMax.toFloat()).coerceAtMost(1f)
    val animatedRatio by animateFloatAsState(
        targetValue = barRatio,
        animationSpec = tween(durationMillis = 300),
        label = "barAnimation"
    )

    val fillColor = when (point.value) {
        in 0..100 -> MaterialTheme.colorScheme.outlineVariant
        in 101..200 -> Color(0xFF9BC9FF)
        in 201..300 -> Color(0xFF4C8DFF)
        in 301..400 -> Color(0xFF5A60D8)
        else -> Color(0xFF7C4DFF)
    }

    Box(
        modifier = Modifier
            .height(220.dp)
            .width(barWidth)
            .combinedClickable(onClick = onTap, onLongClick = onLongPress),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((220.dp * animatedRatio).coerceAtLeast(0.dp))
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(if (isHighlighted) fillColor.copy(alpha = 0.8f) else fillColor)
        )
    }
}

@Composable
private fun ExportSection(
    context: android.content.Context,
    totalIncome: Double,
    totalExpense: Double,
    reportData: List<ReportItem>
) {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Text("Export", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            ExportActionCard(
                title = "CSV",
                icon = Icons.AutoMirrored.Filled.Article,
                onClick = { ExportHelper.exportCSV(context, reportData) },
                modifier = Modifier.weight(1f)
            )
            ExportActionCard(
                title = "Excel",
                icon = Icons.Default.GridOn,
                onClick = { ExportHelper.exportExcel(context, reportData) },
                modifier = Modifier.weight(1f)
            )
            ExportActionCard(
                title = "PDF",
                icon = Icons.Default.Description,
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
    }
}

@Composable
private fun ExportActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.sm, vertical = spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun filterTransactionsForExport(
    transactions: List<Transaction>,
    filter: ReportFilter
): List<Transaction> {
    val now = Calendar.getInstance()
    return transactions.filter { tx ->
        val time = Calendar.getInstance().apply { timeInMillis = tx.dateTime }
        when (filter) {
            ReportFilter.WEEK -> {
                val start = Calendar.getInstance().apply {
                    firstDayOfWeek = Calendar.MONDAY
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                time.timeInMillis in start.timeInMillis..now.timeInMillis
            }

            ReportFilter.MONTH ->
                time.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    time.get(Calendar.MONTH) == now.get(Calendar.MONTH)

            ReportFilter.YEAR -> time.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        }
    }
}

private fun buildChartData(transactions: List<Transaction>, filter: ReportFilter): List<ChartPoint> {
    val now = Calendar.getInstance()
    return when (filter) {
        ReportFilter.WEEK -> {
            val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            labels.mapIndexed { index, label ->
                val dayOfWeek = index + Calendar.MONDAY
                val count = transactions.count {
                    val c = Calendar.getInstance().apply { timeInMillis = it.dateTime }
                    c.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR) &&
                        c.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        c.get(Calendar.DAY_OF_WEEK) == if (dayOfWeek > Calendar.SATURDAY) Calendar.SUNDAY else dayOfWeek
                }
                ChartPoint(label, count)
            }
        }

        ReportFilter.MONTH -> {
            val labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            labels.mapIndexed { monthIndex, label ->
                val count = transactions.count {
                    val c = Calendar.getInstance().apply { timeInMillis = it.dateTime }
                    c.get(Calendar.YEAR) == now.get(Calendar.YEAR) && c.get(Calendar.MONTH) == monthIndex
                }
                ChartPoint(label, count)
            }
        }

        ReportFilter.YEAR -> {
            (2020..now.get(Calendar.YEAR)).map { year ->
                val count = transactions.count {
                    val c = Calendar.getInstance().apply { timeInMillis = it.dateTime }
                    c.get(Calendar.YEAR) == year
                }
                ChartPoint(year.toString().takeLast(2), count)
            }
        }
    }
}

private fun defaultChartData(filter: ReportFilter): List<ChartPoint> = when (filter) {
    ReportFilter.WEEK -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        .mapIndexed { idx, label -> ChartPoint(label, ((idx + 1) * 5).coerceAtMost(45)) }

    ReportFilter.MONTH -> listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        .mapIndexed { idx, label -> ChartPoint(label, ((idx % 9) + 1) * 5) }

    ReportFilter.YEAR -> {
        val nowYear = Calendar.getInstance().get(Calendar.YEAR)
        (2020..nowYear).mapIndexed { idx, year -> ChartPoint(year.toString().takeLast(2), ((idx % 9) + 1) * 5) }
    }
}
