package com.appstudio.finmarka.ui.screens.reminders

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun BillRemindersScreen() {
    ModuleScaffold(
        title = "Bill Reminders",
        subtitle = "Never miss a bill with recurring reminders"
    ) {
        SummaryRow(
            "Upcoming" to "5",
            "Overdue" to "1",
            "Auto-pay" to "2"
        )
        SectionCard(
            title = "Reminder Settings",
            body = "Schedule bill alerts, link payment accounts, and set recurring cycles."
        )
        BulletList(
            title = "Next Bills",
            bullets = listOf(
                "Rent · 01 Sep · $900",
                "Internet · 05 Sep · $45",
                "Insurance · 12 Sep · $65"
            )
        )
    }
}
