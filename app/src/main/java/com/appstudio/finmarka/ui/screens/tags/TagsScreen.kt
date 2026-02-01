package com.appstudio.finmarka.ui.screens.tags

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun TagsScreen() {
    ModuleScaffold(
        title = "Tags",
        subtitle = "Organize transactions by people, purpose, and projects"
    ) {
        SummaryRow(
            "Tags" to "14",
            "Favorites" to "4",
            "Used" to "86%"
        )
        SectionCard(
            title = "Tagging",
            body = "Apply multiple tags per transaction, filter reports, and share with friends."
        )
        BulletList(
            title = "Popular Tags",
            bullets = listOf(
                "Family",
                "Business",
                "Travel",
                "Friends",
                "Health"
            )
        )
    }
}
