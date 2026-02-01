package com.appstudio.finmarka.ui.screens.merchants

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun MerchantsScreen() {
    ModuleScaffold(
        title = "Merchants & Sources",
        subtitle = "Track payees, shops, and income sources"
    ) {
        SummaryRow(
            "Merchants" to "18",
            "Sources" to "6",
            "Linked" to "12"
        )
        SectionCard(
            title = "Merchant Insights",
            body = "View merchant-wise spend trends and assign default categories or tags."
        )
        BulletList(
            title = "Recent Merchants",
            bullets = listOf(
                "SuperMart · $84 last week",
                "Fuel Station · $45 yesterday",
                "Freelance Client · $1,200 income"
            )
        )
    }
}
