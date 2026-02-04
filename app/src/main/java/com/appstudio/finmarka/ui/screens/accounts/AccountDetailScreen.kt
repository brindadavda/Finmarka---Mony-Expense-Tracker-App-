package com.appstudio.finmarka.ui.screens.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.model.TransactionStatus
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.util.formatCurrency
import com.appstudio.finmarka.ui.util.formatDateShort
import com.appstudio.finmarka.ui.viewmodel.AccountDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
    accountId: Int,
    onNavigateBack: () -> Unit,
    onAddTransaction: (Int) -> Unit,
    viewModel: AccountDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val account = state.account
    val transactions = state.transactions
    val accountCurrency = account?.currency ?: "INR"
    val currentBalance = account?.balance?.let { base ->
        base + transactions.filter { it.status == TransactionStatus.COMPLETED }
            .sumOf { transactionDelta(it) }
    } ?: 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddTransaction(accountId) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            account?.let { details ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(details.colorHex.toColor()),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = details.iconName.toIcon(),
                            contentDescription = details.name,
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(details.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(
                            formatCurrency(currentBalance, details.currency),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                if (transactions.isEmpty()) {
                    Text("No transactions yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        transactions.forEach { transaction ->
                            TransactionRow(
                                transaction = transaction,
                                currency = accountCurrency
                            )
                        }
                    }
                }
            } ?: Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    currency: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.merchantName ?: transaction.categoryName.ifBlank { "Transaction" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatDateShort(transaction.dateTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = transaction.status.displayLabel(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = transaction.displayAmount(currency),
                color = transaction.displayColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun TransactionStatus.displayLabel(): String {
    return name.lowercase().replaceFirstChar { it.uppercase() }
}

private fun transactionDelta(transaction: Transaction): Double {
    return when (transaction.type) {
        TransactionType.INCOME -> transaction.amount
        TransactionType.EXPENSE -> -transaction.amount
        TransactionType.TRANSFER -> 0.0
    }
}
