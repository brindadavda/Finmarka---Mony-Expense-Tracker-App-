package com.appstudio.finmarka.ui.screens.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold

private data class MoreItem(
    val title: String,
    val description: String,
    val route: String,
    val icon: ImageVector,
    val useRootNav: Boolean = false
)

@Composable
fun MoreScreen(
    onNavigate: (String) -> Unit,
    onNavigateInner: (String) -> Unit
) {
    val sections = listOf(
        "Core Modules" to listOf(
            MoreItem("Accounts", "Manage cash, bank, and wallet accounts", "accounts", Icons.Default.AccountBalanceWallet),
            MoreItem("Budget", "Plan monthly spending and alerts", "budget", Icons.Default.CreditCard),
            MoreItem("Categories", "Income & expense categories", "categories", Icons.Default.FormatListBulleted),
            MoreItem("Merchants", "Sources, payees, and vendors", "merchants", Icons.Default.Person),
            MoreItem("Assets", "Track assets & net worth", "assets", Icons.Default.Wallet),
            MoreItem("Savings", "Goals and progress tracking", "savings", Icons.Default.AttachMoney),
            MoreItem("Loans", "Loan schedules & balances", "loans", Icons.Default.Payments),
            MoreItem("Lend / Borrow", "People-wise balances", "lend_borrow", Icons.Default.VolunteerActivism)
        ),
        "Planning & Tracking" to listOf(
            MoreItem("Bill Reminders", "Upcoming and recurring bills", "bill_reminders", Icons.Default.EventNote),
            MoreItem("Notes", "Attach notes to finance items", "notes", Icons.Default.NoteAlt),
            MoreItem("Todos", "Tasks & due reminders", "todos", Icons.Default.Task),
            MoreItem("Warranties", "Expiry tracking with receipts", "warranties", Icons.Default.Shield),
            MoreItem("Tags", "Friends, family, business tags", "tags", Icons.Default.Style),
            MoreItem("Statements", "Export and share account statements", "statements", Icons.Default.ReceiptLong),
            MoreItem("Calendar", "Daily expenses calendar", "calendar", Icons.Default.CalendarToday)
        ),
        "Tools & Data" to listOf(
            MoreItem("Exchange Rates", "Multi-currency conversions", "exchange_rates", Icons.Default.Loop),
            MoreItem("Calculators", "EMI, loan, savings tools", "calculators", Icons.Default.TipsAndUpdates),
            MoreItem("Templates", "Reusable transaction templates", "templates", Icons.Default.FilePresent),
            MoreItem("Backup & Restore", "Offline backup and restore", "backup_restore", Icons.Default.Backup, useRootNav = true)
        )
    )

    ModuleScaffold(title = "More", subtitle = "Manage all finance modules and tools") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            sections.forEach { (title, items) ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            onClick = {
                                if (item.useRootNav) {
                                    onNavigate(item.route)
                                } else {
                                    onNavigateInner(item.route)
                                }
                            }
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
