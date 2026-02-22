package com.appstudio.finmarka.ui.screens.budget

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.KeyboardType
import com.appstudio.finmarka.ui.theme.FinmarkaTheme
import com.appstudio.finmarka.ui.theme.LocalSpacing
import kotlin.math.max

data class Budget(
    val name: String,
    val limit: Double,
    val spent: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen() {
    val spacing = LocalSpacing.current
    val budgets = remember {
        mutableStateListOf(
            Budget(name = "Food", limit = 450.0, spent = 295.0),
            Budget(name = "Transport", limit = 220.0, spent = 160.0),
            Budget(name = "Bills", limit = 600.0, spent = 640.0),
            Budget(name = "Shopping", limit = 300.0, spent = 185.0)
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newCategory by remember { mutableStateOf("") }
    var newLimit by remember { mutableStateOf("") }

    val totalBudget = budgets.sumOf { it.limit }
    val totalSpent = budgets.sumOf { it.spent }
    val remaining = totalBudget - totalSpent
    val totalProgress = if (totalBudget > 0.0) (totalSpent / totalBudget).toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Budgets",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
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
            contentPadding = PaddingValues(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            item {
                BudgetSummaryCard(
                    totalBudget = totalBudget,
                    totalSpent = totalSpent,
                    remaining = remaining,
                    progress = totalProgress
                )
            }

            items(items = budgets, key = { it.name }) { budget ->
                BudgetItemCard(budget = budget)
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Budget",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text(text = "Category") },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    OutlinedTextField(
                        value = newLimit,
                        onValueChange = { value ->
                            newLimit = value.filter { it.isDigit() || it == '.' }
                        },
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
                    onClick = {
                        val parsedLimit = newLimit.toDoubleOrNull()
                        if (newCategory.isNotBlank() && parsedLimit != null && parsedLimit > 0.0) {
                            budgets.add(
                                Budget(
                                    name = newCategory.trim(),
                                    limit = parsedLimit,
                                    spent = 0.0
                                )
                            )
                            newCategory = ""
                            newLimit = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        newCategory = ""
                        newLimit = ""
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun BudgetSummaryCard(
    totalBudget: Double,
    totalSpent: Double,
    remaining: Double,
    progress: Float
) {
    val spacing = LocalSpacing.current
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.xs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                Text(text = "Total Budget", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = currency(totalBudget),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(text = "Total Spent", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = currency(totalSpent),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(text = "Remaining", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = currency(remaining),
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
private fun BudgetItemCard(budget: Budget) {
    val spacing = LocalSpacing.current
    val remaining = budget.limit - budget.spent
    val rawProgress = if (budget.limit > 0.0) (budget.spent / budget.limit).toFloat() else 0f
    val normalizedProgress = rawProgress.coerceIn(0f, 1f)
    val isOverBudget = budget.spent > budget.limit

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text(text = budget.name, style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetMetric(label = "Limit", value = currency(budget.limit), modifier = Modifier.weight(1f))
                BudgetMetric(label = "Spent", value = currency(budget.spent), modifier = Modifier.weight(1f))
                BudgetMetric(
                    label = "Remaining",
                    value = currency(remaining),
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
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
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

private fun currency(amount: Double): String = "$" + String.format("%,.2f", amount)

@Preview(showBackground = true)
@Composable
private fun BudgetScreenPreviewLight() {
    FinmarkaTheme(darkTheme = false) {
        BudgetScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetScreenPreviewDark() {
    FinmarkaTheme(darkTheme = true) {
        BudgetScreen()
    }
}
