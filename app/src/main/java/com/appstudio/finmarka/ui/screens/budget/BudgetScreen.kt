package com.appstudio.finmarka.ui.screens.budget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.domain.model.Category
import com.appstudio.finmarka.ui.components.AppActionTopBar
import com.appstudio.finmarka.ui.components.AppCard
import com.appstudio.finmarka.ui.screens.dashboard.MetricPill
import com.appstudio.finmarka.ui.theme.DashboardAccentEnd
import com.appstudio.finmarka.ui.theme.DashboardAccentStart
import com.appstudio.finmarka.ui.theme.DashboardBalanceEnd
import com.appstudio.finmarka.ui.theme.DashboardBalanceStart
import com.appstudio.finmarka.ui.theme.DashboardTextOnGradient
import com.appstudio.finmarka.ui.theme.FinmarkaTheme
import com.appstudio.finmarka.ui.theme.LocalSpacing
import com.appstudio.finmarka.ui.util.formatCurrency
import com.appstudio.finmarka.ui.viewmodel.BudgetItemUi
import com.appstudio.finmarka.ui.viewmodel.BudgetViewModel
import kotlin.math.max

@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state by viewModel.uiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var newLimit by remember { mutableStateOf("") }

    var editBudgetTarget by remember { mutableStateOf<BudgetItemUi?>(null) }
    var editLimitValue by remember { mutableStateOf("") }
    var deleteBudgetTarget by remember { mutableStateOf<BudgetItemUi?>(null) }

    val totalBudget = state.budgets.sumOf { it.limit }
    val totalSpent = state.budgets.sumOf { it.spent }
    val remaining = totalBudget - totalSpent

    val accentBrush = Brush.linearGradient(
        colors = listOf(
            DashboardAccentStart,
            DashboardAccentEnd
        )
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = spacing.xl, vertical = spacing.lg)
            ) {
                AppActionTopBar(title = "Budgets", onNavigationClick = onNavigateBack)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = DashboardTextOnGradient
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add budget"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = spacing.xl, vertical = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            item {
                BudgetMetricsRow(
                    totalBudget = totalBudget,
                    totalSpent = totalSpent,
                    remaining = remaining,
                    currencyCode = viewModel.currencyCode
                )
            }

            item {
                BudgetPieChartCard(
                    budgets = state.budgets,
                    currencyCode = viewModel.currencyCode
                )
            }

            items(state.budgets, key = { it.budgetId }) { budget ->
                BudgetItemCard(
                    budget = budget,
                    currencyCode = viewModel.currencyCode,
                    onEditClick = {
                        editBudgetTarget = budget
                        editLimitValue = budget.limit.toString()
                    },
                    onDeleteClick = {
                        deleteBudgetTarget = budget
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AddBudgetDialog(
            categories = state.availableCategories,
            selectedCategory = selectedCategory,
            newLimit = newLimit,
            onCategorySelected = { selectedCategory = it },
            onLimitChanged = { value -> newLimit = value.filter { char -> char.isDigit() || char == '.' } },
            onDismiss = {
                showAddDialog = false
                selectedCategory = null
                newLimit = ""
            },
            onSave = {
                val parsedLimit = newLimit.toDoubleOrNull()
                val category = selectedCategory
                if (category != null && parsedLimit != null && parsedLimit > 0.0) {
                    viewModel.addBudget(categoryId = category.id, limitAmount = parsedLimit)
                    showAddDialog = false
                    selectedCategory = null
                    newLimit = ""
                }
            }
        )
    }

    editBudgetTarget?.let { budget ->
        EditBudgetDialog(
            budgetName = budget.name,
            limitValue = editLimitValue,
            onLimitChanged = { value -> editLimitValue = value.filter { it.isDigit() || it == '.' } },
            onDismiss = {
                editBudgetTarget = null
                editLimitValue = ""
            },
            onSave = {
                val updated = editLimitValue.toDoubleOrNull()
                if (updated != null && updated > 0.0) {
                    viewModel.updateBudgetAmount(budgetId = budget.budgetId, newLimitAmount = updated)
                    editBudgetTarget = null
                    editLimitValue = ""
                }
            }
        )
    }

    deleteBudgetTarget?.let { budget ->
        AlertDialog(
            onDismissRequest = { deleteBudgetTarget = null },
            title = { Text(text = "Delete Budget") },
            text = { Text(text = "Delete budget for ${budget.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBudget(budget.budgetId)
                    deleteBudgetTarget = null
                }) {
                    Text(text = "Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteBudgetTarget = null }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun BudgetMetricsRow(
    totalBudget: Double,
    totalSpent: Double,
    remaining: Double,
    currencyCode: String
) {
    val spacing = LocalSpacing.current

    val accentBrush = Brush.linearGradient(
        colors = listOf(
            DashboardAccentStart,
            DashboardAccentEnd
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f))
    ) {
        Box(
            modifier = Modifier
                .background(accentBrush)
                .padding(spacing.lg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                MetricPill(
                    Modifier.weight(1f),
                    title = "Budget",
                    value = formatCurrency(totalBudget, currencyCode)
                )
                MetricPill(
                    Modifier.weight(1f),
                    title = "Spent",
                    value = formatCurrency(totalSpent, currencyCode)
                )
                MetricPill(
                    Modifier.weight(1f),
                    title = "Remaining",
                    value = formatCurrency(remaining, currencyCode)
                )
            }
        }
    }
}

@Composable
private fun BudgetPieChartCard(
    budgets: List<BudgetItemUi>,
    currencyCode: String
) {
    val spacing = LocalSpacing.current
    val chartColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.error
    )

    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            Text(text = "Spending by Category", style = MaterialTheme.typography.titleMedium)

            if (budgets.isEmpty() || budgets.sumOf { it.spent } <= 0.0) {
                Text(
                    text = "No spending data for chart.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val totalSpent = budgets.sumOf { it.spent }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(spacing.xxxl + spacing.xxxl + spacing.xxxl)) {
                        var startAngle = -90f
                        val strokeWidthPx = (spacing.md + spacing.sm).toPx()

                        budgets.forEachIndexed { index, budget ->
                            val sweep = ((budget.spent / totalSpent) * 360f).toFloat()
                            val color = chartColors[index % chartColors.size]

                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = androidx.compose.ui.geometry.Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                                size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
                                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                            )
                            startAngle += sweep
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        budgets.forEachIndexed { index, budget ->
                            val percent = ((budget.spent / totalSpent) * 100).toInt()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(spacing.md)
                                        .background(chartColors[index % chartColors.size], shape = RoundedCornerShape(spacing.xs))
                                )
                                Text(
                                    text = "${budget.name}: ${formatCurrency(budget.spent, currencyCode)} ($percent%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBudgetDialog(
    categories: List<Category>,
    selectedCategory: Category?,
    newLimit: String,
    onCategorySelected: (Category) -> Unit,
    onLimitChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val spacing = LocalSpacing.current
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Budget", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        value = selectedCategory?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = { Text(text = "Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category.name) },
                                onClick = {
                                    onCategorySelected(category)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = newLimit,
                    onValueChange = onLimitChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(text = "Budget limit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = selectedCategory != null && newLimit.toDoubleOrNull() != null) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Cancel") }
        }
    )
}

@Composable
private fun EditBudgetDialog(
    budgetName: String,
    limitValue: String,
    onLimitChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit $budgetName Budget") },
        text = {
            OutlinedTextField(
                value = limitValue,
                onValueChange = onLimitChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(text = "Budget limit") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = limitValue.toDoubleOrNull() != null) {
                Text(text = "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
private fun BudgetItemCard(
    budget: BudgetItemUi,
    currencyCode: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val remaining = budget.limit - budget.spent
    val rawProgress = if (budget.limit > 0.0) (budget.spent / budget.limit).toFloat() else 0f
    val normalizedProgress = rawProgress.coerceIn(0f, 1f)
    val isOverBudget = budget.spent > budget.limit

    AppCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = budget.name, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    TextButton(onClick = onEditClick) { Text(text = "Edit") }
                    TextButton(onClick = onDeleteClick) { Text(text = "Delete", color = MaterialTheme.colorScheme.error) }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetMetric(label = "Limit", value = formatCurrency(budget.limit, currencyCode), modifier = Modifier.weight(1f))
                BudgetMetric(label = "Spent", value = formatCurrency(budget.spent, currencyCode), modifier = Modifier.weight(1f))
                BudgetMetric(
                    label = "Remaining",
                    value = formatCurrency(remaining, currencyCode),
                    modifier = Modifier.weight(1f),
                    valueColor = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { normalizedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.sm),
                color = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "${max(0, (rawProgress * 100).toInt())}% used",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BudgetMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start
) {
    val spacing = LocalSpacing.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = textAlign)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = valueColor, textAlign = textAlign, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetScreenPreviewLight() {
    FinmarkaTheme(darkTheme = false) {
        BudgetMetricsRow(totalBudget = 1200.0, totalSpent = 780.0, remaining = 420.0, currencyCode = "USD")
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetScreenPreviewDark() {
    FinmarkaTheme(darkTheme = true) {
        BudgetPieChartCard(
            budgets = listOf(
                BudgetItemUi(budgetId = 1, categoryId = 1, name = "Food", limit = 400.0, spent = 300.0),
                BudgetItemUi(budgetId = 2, categoryId = 2, name = "Bills", limit = 500.0, spent = 620.0)
            ),
            currencyCode = "USD"
        )
    }
}
