package com.appstudio.finmarka.ui.screens.loans

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun LendBorrowScreen() {
    ModuleScaffold(
        title = "Lend & Borrow",
        subtitle = "Track money lent or borrowed with reminders"
    ) {
        SummaryRow(
            "Lent" to "$1,200",
            "Borrowed" to "$450",
            "People" to "5"
        )
        SectionCard(
            title = "People-wise Tracking",
            body = "Assign balances to contacts, set repayment reminders, and record partial payments."
        )
        BulletList(
            title = "Recent Activity",
            bullets = listOf(
                "Alex · Lent $120 · Due 18 Aug",
                "Priya · Borrowed $200 · Due 30 Aug",
                "Sam · Lent $80 · Cleared"
            )
        )
    }
}
