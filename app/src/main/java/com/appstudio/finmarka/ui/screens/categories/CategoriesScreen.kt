package com.appstudio.finmarka.ui.screens.categories

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun CategoriesScreen() {
    ModuleScaffold(
        title = "Categories",
        subtitle = "Separate income and expense categories with icons"
    ) {
        SummaryRow(
            "Income" to "8",
            "Expense" to "24",
            "Custom" to "12"
        )
        SectionCard(
            title = "Category Manager",
            body = "Create, reorder, and color-code categories to simplify budgeting and reporting."
        )
        BulletList(
            title = "Top Categories",
            bullets = listOf(
                "Salary · Income",
                "Groceries · Expense",
                "Transport · Expense",
                "Subscriptions · Expense"
            )
        )
    }
}
