package com.appstudio.finmarka.ui.screens.calendar

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun CalendarViewScreen() {
    ModuleScaffold(
        title = "Calendar View",
        subtitle = "Track daily expenses with a monthly calendar"
    ) {
        SummaryRow(
            "Today" to "$68",
            "This Week" to "$420",
            "This Month" to "$1,850"
        )
        SectionCard(
            title = "Calendar",
            body = "Tap any date to view the transaction list and daily totals."
        )
        BulletList(
            title = "Daily Highlights",
            bullets = listOf(
                "Aug 10 · $52 · Groceries, Transport",
                "Aug 11 · $88 · Dining, Fuel",
                "Aug 12 · $40 · Utilities"
            )
        )
    }
}
