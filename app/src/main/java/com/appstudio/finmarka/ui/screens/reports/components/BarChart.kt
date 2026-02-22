package com.appstudio.finmarka.ui.screens.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.appstudio.finmarka.data.model.ReportItem
import com.appstudio.finmarka.ui.theme.IncomeGreen

@Composable
fun BarChart(data: List<ReportItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text("📊 Bar Chart", style = MaterialTheme.typography.titleMedium)

        data.forEach {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(it.amount / 100f)
                        .height(18.dp)
                        .background(IncomeGreen)
                )
            }

            Text("${it.label}: ${it.amount}")
        }
    }
}
