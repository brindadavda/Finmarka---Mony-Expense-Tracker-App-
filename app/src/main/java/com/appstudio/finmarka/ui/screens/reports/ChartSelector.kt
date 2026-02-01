//package com.appstudio.finmarka.ui.screens.reports
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.semantics.SemanticsProperties.Text
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun ChartSelector(
//    selectedChart: ChartType,
//    onChartSelected: (ChartType) -> Unit
//) {
//    LazyColumn(
//        modifier = Modifier.height(70.dp),
//        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
//    ) {
//        item {
//            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//
//                ChartType.entries.forEach { chart ->
//                    OutlinedButton(
//                        onClick = { onChartSelected(chart) },
//                        colors = ButtonDefaults.outlinedButtonColors(
//                            containerColor =
//                                if (chart == selectedChart)
//                                    MaterialTheme.colorScheme.primary
//                                else Color.Transparent
//                        )
//                    ) {
//                        Text(chart.name)
//                    }
//                }
//            }
//        }
//    }
//}
