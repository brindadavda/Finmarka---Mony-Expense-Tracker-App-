package com.appstudio.finmarka.ui.screens.backup

import androidx.compose.runtime.Composable
import com.appstudio.finmarka.ui.screens.common.BulletList
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.screens.common.SectionCard
import com.appstudio.finmarka.ui.screens.common.SummaryRow

@Composable
fun BackupRestoreScreen() {
    ModuleScaffold(
        title = "Backup & Restore",
        subtitle = "Offline-first backups with encryption and export"
    ) {
        SummaryRow(
            "Backups" to "3",
            "Last Backup" to "Yesterday",
            "Size" to "8.4 MB"
        )
        SectionCard(
            title = "Backup Actions",
            body = "Create local backups, restore previous versions, and export archives."
        )
        BulletList(
            title = "Backup Tips",
            bullets = listOf(
                "Store backups in private storage",
                "Encrypt exports before sharing",
                "Schedule weekly backups"
            )
        )
    }
}
