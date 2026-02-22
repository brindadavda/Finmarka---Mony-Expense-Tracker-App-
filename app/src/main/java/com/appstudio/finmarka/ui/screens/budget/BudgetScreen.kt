package com.appstudio.finmarka.ui.screens.budget

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.domain.model.Category
import com.appstudio.finmarka.ui.components.AppActionTopBar
import com.appstudio.finmarka.ui.components.AppCard
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

    val totalBudget = state.budgets.sumOf { it.limit }
    val totalSpent = state.budgets.sumOf { it.spent }
    val remaining = totalBudget - totalSpent
    val totalProgress = if (totalBudget > 0.0) (totalSpent / totalBudget).toFloat() else 0f

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = spacing.xl, vertical = spacing.lg)
            ) {
                AppActionTopBar(
                    title = "Budgets",
                    onNavigationClick = onNavigateBack
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DashboardTextOnGradient,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                androidx.compose.material3.Icon(
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
                BudgetBarChartCard(
                    budgets = state.budgets,
                    currencyCode = viewModel.currencyCode
                )
            }

            item {
                BudgetMetricsRow(
                    totalBudget = totalBudget,
                    totalSpent = totalSpent,
                    remaining = remaining,
                    currencyCode = viewModel.currencyCode
                )
            }

            item {
                BudgetSummaryCard(
                    totalBudget = totalBudget,
                    totalSpent = totalSpent,
                    remaining = remaining,
                    progress = totalProgress,
                    currencyCode = viewModel.currencyCode
                )
            }

            items(state.budgets, key = { it.categoryId }) { budget ->
                BudgetItemCard(
                    budget = budget,
                    currencyCode = viewModel.currencyCode
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
}

@Composable
private fun BudgetBarChartCard(
    budgets: List<BudgetItemUi>,
    currencyCode: String
) {
    val spacing = LocalSpacing.current
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            Text(
                text = "Budget Chart",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (budgets.isEmpty()) {
                Text(
                    text = "No budget categories yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val maxBudget = budgets.maxOf { it.limit }.takeIf { it > 0.0 } ?: 1.0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                    verticalAlignment = Alignment.Bottom
                ) {
                    budgets.forEach { budget ->
                        val barProgress = (budget.spent / maxBudget).toFloat().coerceIn(0f, 1f)
                        val barColor = if (budget.spent > budget.limit) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
                            Text(
                                text = formatCurrency(budget.spent, currencyCode),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Box(
                                modifier = Modifier
                                    .height(spacing.xxxl + spacing.xxxl + spacing.lg)
                                    .width(spacing.xxl)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(spacing.sm)
                                    ),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((spacing.xxxl + spacing.xxxl + spacing.lg) * barProgress)
                                        .background(color = barColor, shape = RoundedCornerShape(spacing.sm))
                                )
                            }
                            Text(
                                text = budget.name,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        MetricPill(
            modifier = Modifier.weight(1f),
            title = "Budget",
            value = formatCurrency(totalBudget, currencyCode)
        )
        MetricPill(
            modifier = Modifier.weight(1f),
            title = "Spent",
            value = formatCurrency(totalSpent, currencyCode)
        )
        MetricPill(
            modifier = Modifier.weight(1f),
            title = "Remaining",
            value = formatCurrency(remaining, currencyCode)
        )
    }
}

@Composable
private fun MetricPill(
    modifier: Modifier,
    title: String,
    value: String,
    tint: Color = DashboardTextOnGradient
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = tint)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.xs)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
        title = {
            Text(
                text = "Add Budget",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        value = selectedCategory?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = { Text(text = "Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
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
            TextButton(
                onClick = onSave,
                enabled = selectedCategory != null && newLimit.toDoubleOrNull() != null
            ) {
                Text(text = "Save")
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
private fun BudgetSummaryCard(
    totalBudget: Double,
    totalSpent: Double,
    remaining: Double,
    progress: Float,
    currencyCode: String
) {
    val spacing = LocalSpacing.current
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                Text(text = "Total Budget", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = formatCurrency(totalBudget, currencyCode),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(text = "Total Spent", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = formatCurrency(totalSpent, currencyCode),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(text = "Remaining", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = formatCurrency(remaining, currencyCode),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier.size(spacing.xxxl + spacing.xxxl),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = spacing.sm
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BudgetItemCard(
    budget: BudgetItemUi,
    currencyCode: String
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
            Text(text = budget.name, style = MaterialTheme.typography.titleMedium)

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
                    valueColor = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.xs)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = textAlign
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetScreenPreviewLight() {
    FinmarkaTheme(darkTheme = false) {
        BudgetMetricsRow(
            totalBudget = 1200.0,
            totalSpent = 780.0,
            remaining = 420.0,
            currencyCode = "USD"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetScreenPreviewDark() {
    FinmarkaTheme(darkTheme = true) {
        BudgetBarChartCard(
            budgets = listOf(
                BudgetItemUi(categoryId = 1, name = "Food", limit = 400.0, spent = 300.0),
                BudgetItemUi(categoryId = 2, name = "Bills", limit = 500.0, spent = 620.0)
            ),
            currencyCode = "USD"
        )
    }
}
