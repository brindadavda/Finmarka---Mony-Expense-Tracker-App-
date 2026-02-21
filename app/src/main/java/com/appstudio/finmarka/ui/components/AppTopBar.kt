package com.appstudio.finmarka.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null
) {
    TopAppBar(
        title = {
            if (subtitle == null) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            } else {
                androidx.compose.foundation.layout.Column {
                    Text(text = title, style = MaterialTheme.typography.titleLarge)
                    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
