package com.appstudio.finmarka.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.theme.DashboardAccentEnd
import com.appstudio.finmarka.ui.theme.DashboardAccentStart
import com.appstudio.finmarka.ui.theme.DashboardCardSurface
import com.appstudio.finmarka.ui.theme.FinMarkElevation
import com.appstudio.finmarka.ui.theme.LocalSpacing
import com.appstudio.finmarka.ui.util.formatDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun LazyListScope.transactionListItems(
    transactions: List<Transaction>,
    currencyCode: String,
    onTransactionClick: (Int) -> Unit,
    showSections: Boolean,
    enableDelete: Boolean,
    onDeleteConfirmed: (Int) -> Unit = {}
) {
    val sortedTransactions = transactions.sortedByDescending { it.dateTime }

    if (showSections) {
        val groupedTransactions = sortedTransactions.groupBy {
            Instant.ofEpochMilli(it.dateTime).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        groupedTransactions.forEach { (date, dailyTransactions) ->
            item(key = "header-$date") {
                TransactionDateHeader(date = date)
            }
            items(dailyTransactions, key = { it.id }) { transaction ->
                TransactionListRow(
                    transaction = transaction,
                    currencyCode = currencyCode,
                    onClick = { onTransactionClick(transaction.id) },
                    enableDelete = enableDelete,
                    onDeleteConfirmed = { onDeleteConfirmed(transaction.id) }
                )
            }
        }
    } else {
        items(sortedTransactions, key = { it.id }) { transaction ->
            TransactionListRow(
                transaction = transaction,
                currencyCode = currencyCode,
                onClick = { onTransactionClick(transaction.id) },
                enableDelete = enableDelete,
                onDeleteConfirmed = { onDeleteConfirmed(transaction.id) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TransactionDateHeader(date: LocalDate) {
    val today = LocalDate.now()
    val title = when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> formatDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = LocalSpacing.current.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formatDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TransactionListRow(
    transaction: Transaction,
    currencyCode: String,
    onClick: () -> Unit,
    enableDelete: Boolean,
    onDeleteConfirmed: () -> Unit
) {
    val spacing = LocalSpacing.current
    val cardBrush = Brush.horizontalGradient(
        colors = listOf(
            DashboardCardSurface.copy(alpha = 0.55f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )
    )
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirmed()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = FinMarkElevation.sm),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            width = spacing.xs / 2,
            brush = Brush.horizontalGradient(listOf(DashboardAccentStart.copy(alpha = 0.45f), DashboardAccentEnd.copy(alpha = 0.45f)))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush)
                .padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.xxxl)
                    .background(
                        color = transaction.displayColor.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type == TransactionType.INCOME) Icons.Default.SouthWest else Icons.Default.NorthEast,
                    contentDescription = null,
                    tint = transaction.displayColor
                )
            }
            Spacer(modifier = Modifier.size(spacing.md))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = transaction.categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.size(spacing.sm))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = transaction.displayAmount(currencyCode),
                            style = MaterialTheme.typography.titleLarge,
                            color = transaction.displayColor
                        )
                        Text(
                            text = transaction.dateTime.toTransactionListTime(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    transaction.tagsForUi().take(2).forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag) },
                            shape = MaterialTheme.shapes.small,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            if (enableDelete) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun Transaction.tagsForUi(): List<String> = buildList {
    add(status.name.lowercase().replaceFirstChar { it.titlecase() })
    if (isRecurring) add("Recurring")
    if (isReimbursement) add("Reimbursement")
    if (isTemplate) add("Template")
    if (isExcluded) add("Excluded")
}


private fun Long.toTransactionListTime(): String =
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(this))
