package com.appstudio.finmarka.ui.screens.calculators

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun CalculatorsScreen() {
    ModuleScaffold(
        title = "Calculators",
        subtitle = "Quick financial tools for planning and splitting"
    ) {
        SummaryRow(
            "Tools" to "4",
            "Favorites" to "2",
            "Last Used" to "Today"
        )
        SectionCard(
            title = "Available Calculators",
            body = "EMI, loan payoff, savings growth, and expense split calculators are ready."
        )
        BulletList(
            title = "Calculator Shortcuts",
            bullets = listOf(
                "EMI Calculator · Estimate monthly payment",
                "Loan Payoff · Compare payoff schedules",
                "Savings Growth · Forecast goal reach",
                "Expense Split · Split bills by people"
            )
        )
    }
}
