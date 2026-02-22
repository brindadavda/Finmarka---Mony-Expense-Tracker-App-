package com.appstudio.finmarka.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.appstudio.finmarka.ui.theme.DashboardAccentStart
import com.appstudio.finmarka.ui.theme.DashboardCardSurface
import com.appstudio.finmarka.ui.theme.DashboardCardSurfaceAlt
import com.appstudio.finmarka.ui.theme.DashboardTextOnGradient
import com.appstudio.finmarka.ui.theme.LocalSpacing

@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.lg + spacing.sm))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(spacing.lg + spacing.sm),
        colors = CardDefaults.cardColors(containerColor = DashboardCardSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.xxxl + spacing.sm)
                    .clip(CircleShape)
                    .background(DashboardCardSurfaceAlt),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = DashboardAccentStart)
            }
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = DashboardTextOnGradient)
        }
    }
}
