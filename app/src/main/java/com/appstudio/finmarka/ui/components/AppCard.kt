package com.appstudio.finmarka.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.appstudio.finmarka.ui.theme.LocalElevation
import com.appstudio.finmarka.ui.theme.LocalSpacing

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(LocalSpacing.current.lg),
    content: @Composable ColumnScope.() -> Unit
) {
    val elevation = LocalElevation.current
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.sm)
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}
