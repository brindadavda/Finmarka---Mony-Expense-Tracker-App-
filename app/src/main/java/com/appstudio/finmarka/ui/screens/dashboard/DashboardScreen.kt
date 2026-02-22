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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.R
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.ui.theme.DashboardAccentEnd
import com.appstudio.finmarka.ui.theme.DashboardAccentStart
import com.appstudio.finmarka.ui.theme.DashboardBalanceEnd
import com.appstudio.finmarka.ui.theme.DashboardBalanceStart
import com.appstudio.finmarka.ui.theme.DashboardCardSurface
import com.appstudio.finmarka.ui.theme.DashboardCardSurfaceAlt
import com.appstudio.finmarka.ui.theme.DashboardCardSurfaceMuted
import com.appstudio.finmarka.ui.theme.DashboardTextMuted
import com.appstudio.finmarka.ui.theme.DashboardTextOnDark
import com.appstudio.finmarka.ui.theme.DashboardTextOnGradient
import com.appstudio.finmarka.ui.theme.FinMarkElevation
import com.appstudio.finmarka.ui.theme.LocalSpacing
import com.appstudio.finmarka.ui.components.transactionListItems
import com.appstudio.finmarka.ui.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

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
            DashboardAccentStart,
            DashboardAccentEnd
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                            color = DashboardTextMuted
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
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
                                tint = DashboardTextOnGradient
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f))
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        DashboardBalanceStart,
                                        DashboardBalanceEnd
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Total Balance",
                                style = MaterialTheme.typography.titleLarge,
                                color = DashboardTextOnGradient
                            )

                            if (state.isLoading) {
                                CircularProgressIndicator(color = DashboardTextOnGradient)
                            } else {
                                Text(
                                    text = viewModel.formatWithPrefCurrency(state.totalBalance),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = DashboardTextOnGradient,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MetricPill(
                                    modifier = Modifier.weight(1f),
                                    title = "Income",
                                    value = viewModel.formatWithPrefCurrency(state.totalIncome)
                                )
                                MetricPill(
                                    modifier = Modifier.weight(1f),
                                    title = "Expense",
                                    value = viewModel.formatWithPrefCurrency(state.totalExpense)
                                )
                                MetricPill(
                                    modifier = Modifier.weight(1f),
                                    title = "Savings",
                                    value = savingsValue
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
                RecentTransactionsHeader(onNavigateToTransactions = onNavigateToTransactions)
            }

            if (recentItems.isEmpty()) {
                item { EmptyTransactionsCard() }
            } else {
                transactionListItems(
                    transactions = recentItems,
                    currencyCode = viewModel.getCurrencyCode,
                    onTransactionClick = onTransactionClick,
                    showSections = false,
                    enableDelete = false
                )
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
                tint = DashboardTextOnGradient,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}



@Composable
private fun RecentTransactionsHeader(onNavigateToTransactions: () -> Unit) {
    val spacing = LocalSpacing.current
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onNavigateToTransactions)
                .padding(horizontal = spacing.sm, vertical = spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
        ) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
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
        colors = CardDefaults.cardColors(containerColor = DashboardCardSurface)
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
                    .background(DashboardCardSurfaceAlt),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = DashboardAccentStart)
            }
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = DashboardTextOnGradient)
        }
    }
}

@Composable
private fun EmptyTransactionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardCardSurface)
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
                    .background(DashboardCardSurfaceMuted),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = DashboardTextMuted,
                    modifier = Modifier.size(34.dp)
                )
            }
            Text(text = "No transactions yet", style = MaterialTheme.typography.titleMedium, color = DashboardTextOnDark)
            Text(
                text = "Start tracking your finances by adding your first transaction.",
                style = MaterialTheme.typography.bodyLarge,
                color = DashboardTextMuted
            )
        }
    }
}

@Composable
fun MetricPill(
    modifier: Modifier,
    title: String,
    value: String,
    tint: Color = DashboardTextOnGradient
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


