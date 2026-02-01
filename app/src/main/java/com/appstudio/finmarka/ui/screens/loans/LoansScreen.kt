package com.appstudio.finmarka.ui.screens.loans

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun LoansScreen() {
    ModuleScaffold(
        title = "Loans",
        subtitle = "Manage loan balances, EMIs, and due dates"
    ) {
        SummaryRow(
            "Active Loans" to "2",
            "Outstanding" to "$14,800",
            "Next Due" to "$420"
        )
        SectionCard(
            title = "Loan Schedule",
            body = "Configure EMI schedules, interest rates, and payoff projections."
        )
        BulletList(
            title = "Upcoming Payments",
            bullets = listOf(
                "Car Loan · Due 12 Aug · $320",
                "Personal Loan · Due 25 Aug · $100"
            )
        )
    }
}
