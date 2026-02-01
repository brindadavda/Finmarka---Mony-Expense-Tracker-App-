package com.appstudio.finmarka.ui.screens.assets

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun AssetsScreen() {
    ModuleScaffold(
        title = "Assets",
        subtitle = "Track net worth across physical and digital assets"
    ) {
        SummaryRow(
            "Total Assets" to "$128,500",
            "Growth" to "+4.2%",
            "Items" to "10"
        )
        SectionCard(
            title = "Asset Summary",
            body = "Add assets with valuation updates and depreciation reminders."
        )
        BulletList(
            title = "Tracked Assets",
            bullets = listOf(
                "Car · $18,000 · Updated this month",
                "Laptop · $1,250 · Warranty linked",
                "Investments · $96,300 · Monthly update"
            )
        )
    }
}
