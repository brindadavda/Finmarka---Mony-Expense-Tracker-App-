package com.appstudio.finmarka.ui.screens.tasks

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun TodosScreen() {
    ModuleScaffold(
        title = "Todos",
        subtitle = "Track financial tasks and due dates"
    ) {
        SummaryRow(
            "Active" to "6",
            "Completed" to "12",
            "Overdue" to "1"
        )
        SectionCard(
            title = "Task Planner",
            body = "Create tasks for bill payments, debt reviews, and budgeting checkpoints."
        )
        BulletList(
            title = "Upcoming Tasks",
            bullets = listOf(
                "Review budget categories · Due today",
                "Update insurance documents · Due 20 Aug",
                "Prepare tax receipts · Due 31 Aug"
            )
        )
    }
}
