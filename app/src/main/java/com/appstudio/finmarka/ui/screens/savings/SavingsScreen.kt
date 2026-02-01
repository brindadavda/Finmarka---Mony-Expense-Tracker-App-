package com.appstudio.finmarka.ui.screens.savings

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun SavingsScreen() {
    ModuleScaffold(
        title = "Savings Goals",
        subtitle = "Plan, track, and top up your savings goals"
    ) {
        SummaryRow(
            "Goals" to "4",
            "Saved" to "$6,250",
            "This Month" to "$420"
        )
        SectionCard(
            title = "Goal Progress",
            body = "Allocate amounts from transactions, schedule auto-saves, and visualize progress."
        )
        BulletList(
            title = "Active Goals",
            bullets = listOf(
                "Emergency Fund · 62% · Due Dec 2024",
                "Vacation · 40% · Due Mar 2025",
                "New Laptop · 80% · Due Oct 2024"
            )
        )
    }
}
