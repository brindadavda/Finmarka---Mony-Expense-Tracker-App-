package com.appstudio.finmarka.ui.screens.notes

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun NotesScreen() {
    ModuleScaffold(
        title = "Notes",
        subtitle = "Store finance notes, warranties, and references"
    ) {
        SummaryRow(
            "Notes" to "12",
            "Pinned" to "3",
            "Attachments" to "5"
        )
        SectionCard(
            title = "Note Organizer",
            body = "Attach notes to transactions, add tags, and search across documents."
        )
        BulletList(
            title = "Recent Notes",
            bullets = listOf(
                "Laptop warranty details",
                "Insurance renewal checklist",
                "Travel budget notes"
            )
        )
    }
}
