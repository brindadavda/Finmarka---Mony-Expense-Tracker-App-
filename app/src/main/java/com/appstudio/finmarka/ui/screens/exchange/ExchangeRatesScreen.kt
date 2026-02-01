package com.appstudio.finmarka.ui.screens.exchange

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun ExchangeRatesScreen() {
    ModuleScaffold(
        title = "Exchange Rates",
        subtitle = "Multi-currency support with offline historical rates"
    ) {
        SummaryRow(
            "Base" to "USD",
            "Tracked" to "6",
            "Updated" to "Today"
        )
        SectionCard(
            title = "Currency Conversion",
            body = "Convert transactions with cached rates and maintain a history for reports."
        )
        BulletList(
            title = "Latest Rates",
            bullets = listOf(
                "EUR · 0.92",
                "GBP · 0.78",
                "INR · 83.10",
                "JPY · 145.50"
            )
        )
    }
}
