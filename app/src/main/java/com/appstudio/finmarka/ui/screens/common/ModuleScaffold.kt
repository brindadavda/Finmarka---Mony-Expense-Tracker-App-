package com.appstudio.finmarka.ui.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.appstudio.finmarka.ui.components.AppCard
import com.appstudio.finmarka.ui.components.AppTopBar
import com.appstudio.finmarka.ui.theme.LocalSpacing

@Composable
fun ModuleScaffold(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        AppTopBar(title = title, subtitle = subtitle)
        content()
    }
}

@Composable
fun SummaryRow(vararg items: Pair<String, String>) {
    val spacing = LocalSpacing.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
        items.forEach { (label, value) ->
            AppCard(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(spacing.xs))
                Text(text = value, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun SectionCard(title: String, body: String) {
    val spacing = LocalSpacing.current
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(spacing.sm))
        Text(text = body, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun BulletList(title: String, bullets: List<String>) {
    val spacing = LocalSpacing.current
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(spacing.sm))
        bullets.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("•", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(spacing.sm))
                Text(text = item, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
