package com.appstudio.finmarka.ui.screens.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.R
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.theme.ExpenseRed
import com.appstudio.finmarka.ui.theme.IncomeGreen
import com.appstudio.finmarka.ui.util.formatDateTimeTravel
import com.appstudio.finmarka.ui.viewmodel.DashboardViewModel
import com.appstudio.finmarka.ui.viewmodel.TransactionsViewModel
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onTransactionClick: (Int) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val zone = ZoneId.systemDefault()
    val cutoff = LocalDate.now().minusDays(3).atStartOfDay(zone).toInstant().toEpochMilli()
    val recentItems = state.recentTransactions.filter { it.dateTime >= cutoff }.take(4)
    val savingsValue = viewModel.formatWithPrefCurrency(state.totalIncome - state.totalExpense)

    val accentBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1CD8A6),
            Color(0xFF3E8BFF)
        )
    )
    val screenBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1F2D47),
            Color(0xFF08121F)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome back",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF9CB0CC)
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(accentBrush),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Open settings",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF24C39E),
                                        Color(0xFF3E7DE8)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Total Balance",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFFEAF6FF)
                            )

                            if (state.isLoading) {
                                CircularProgressIndicator(color = Color(0xFFEAF6FF))
                            } else {
                                Text(
                                    text = viewModel.formatWithPrefCurrency(state.totalBalance),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color(0xFFEAF6FF),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MetricPill(
                                    modifier = Modifier.weight(1f),
                                    title = "Income",
                                    value = viewModel.formatWithPrefCurrency(state.totalIncome),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                MetricPill(
                                    modifier = Modifier.weight(1f),
                                    title = "Expense",
                                    value = viewModel.formatWithPrefCurrency(state.totalExpense),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                MetricPill(
                                    modifier = Modifier.weight(1f),
                                    title = "Savings",
                                    value = savingsValue,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCard("Transactions", Icons.Default.List, onNavigateToTransactions, Modifier.weight(1f))
                    ActionCard("Report", Icons.Default.BarChart, onNavigateToReports, Modifier.weight(1f))
                    ActionCard("Budget", Icons.Default.PieChart, onNavigateToBudget, Modifier.weight(1f))
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF8DB3FF),
                        modifier = Modifier
                            .clickable(onClick = onNavigateToTransactions)
                            .padding(4.dp)
                    )
                    Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF8DB3FF))
                }
            }

            if (recentItems.isEmpty()) {
                item { EmptyTransactionsCard() }
            } else {
                items(recentItems) { transaction ->
                    TransactionItem(transaction = transaction, onClick = { onTransactionClick(transaction.id) })
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(accentBrush)
                .clickable(onClick = onNavigateToAddTransaction),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add transaction",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun ActionCard(title: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2340))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E3A56)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Color(0xFF1CD8A6))
            }
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = Color.White)
        }
    }
}

@Composable
private fun EmptyTransactionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2340))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF223554)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF9CB0CC),
                    modifier = Modifier.size(34.dp)
                )
            }
            Text(text = "No transactions yet", style = MaterialTheme.typography.titleMedium, color = Color(0xFFDDE7F8))
            Text(
                text = "Start tracking your finances by adding your first transaction.",
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 34.sp),
                color = Color(0xFF9CB0CC)
            )
        }
    }
}

@Composable
private fun MetricPill(
    modifier: Modifier,
    title: String,
    value: String,
    tint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.18f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = tint)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.labelSmall, color = tint, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2340)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.categoryName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDateTimeTravel(transaction.dateTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CB0CC)
                )
            }
            Text(
                text = transaction.displayAmount(viewModel.getCurrencyCode),
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
            )
        }
    }
}
