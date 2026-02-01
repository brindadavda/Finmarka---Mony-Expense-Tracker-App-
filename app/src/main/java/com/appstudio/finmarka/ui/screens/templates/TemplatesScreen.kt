package com.appstudio.finmarka.ui.screens.templates

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun TemplatesScreen() {
    ModuleScaffold(
        title = "Templates",
        subtitle = "Reuse frequent transactions with templates"
    ) {
        SummaryRow(
            "Templates" to "8",
            "Recurring" to "3",
            "Most Used" to "Rent"
        )
        SectionCard(
            title = "Template Actions",
            body = "Create templates for repeat transactions and schedule recurring entries."
        )
        BulletList(
            title = "Recent Templates",
            bullets = listOf(
                "Monthly Rent · Expense",
                "Salary · Income",
                "Gym Membership · Recurring"
            )
        )
    }
}
