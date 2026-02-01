package com.appstudio.finmarka.ui.screens.warranties

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun WarrantiesScreen() {
    ModuleScaffold(
        title = "Warranties",
        subtitle = "Monitor warranty expiries with reminders"
    ) {
        SummaryRow(
            "Active" to "7",
            "Expiring Soon" to "2",
            "Attachments" to "6"
        )
        SectionCard(
            title = "Warranty Manager",
            body = "Store invoices, set expiry reminders, and link warranties to assets."
        )
        BulletList(
            title = "Upcoming Expiry",
            bullets = listOf(
                "Phone · Expires 12 Sep · 28 days",
                "Headphones · Expires 30 Sep · 46 days"
            )
        )
    }
}
