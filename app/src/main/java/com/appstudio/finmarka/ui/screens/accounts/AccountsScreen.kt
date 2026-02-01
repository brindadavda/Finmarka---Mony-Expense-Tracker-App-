package com.appstudio.finmarka.ui.screens.accounts

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun AccountsScreen() {
    ModuleScaffold(
        title = "Accounts",
        subtitle = "Track balances across cash, bank, cards, and wallets"
    ) {
        SummaryRow(
            "Total Balance" to "$18,420",
            "Accounts" to "6",
            "Linked" to "3"
        )
        SectionCard(
            title = "Account Actions",
            body = "Add, edit, or archive accounts. View account-wise statements with filters and exports."
        )
        BulletList(
            title = "Account Highlights",
            bullets = listOf(
                "Main Bank · $9,800 · Last updated today",
                "Cash Wallet · $420 · Quick add enabled",
                "Credit Card · $2,150 due · Statement ready"
            )
        )
    }
}
