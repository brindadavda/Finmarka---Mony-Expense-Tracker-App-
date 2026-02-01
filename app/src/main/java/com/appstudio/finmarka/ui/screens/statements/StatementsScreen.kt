package com.appstudio.finmarka.ui.screens.statements

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun StatementsScreen() {
    ModuleScaffold(
        title = "Statements",
        subtitle = "Filter, export, and share account statements"
    ) {
        SummaryRow(
            "Accounts" to "6",
            "This Month" to "$3,280",
            "Exports" to "2"
        )
        SectionCard(
            title = "Statement Controls",
            body = "Select account, date range, and export to PDF/CSV with share options."
        )
        BulletList(
            title = "Recent Exports",
            bullets = listOf(
                "Main Bank · PDF · 01 Aug",
                "Cash Wallet · CSV · 29 Jul"
            )
        )
    }
}
