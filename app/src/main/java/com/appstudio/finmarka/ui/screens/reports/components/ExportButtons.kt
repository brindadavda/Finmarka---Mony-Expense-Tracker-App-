package com.appstudio.finmarka.ui.screens.reports.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.appstudio.finmarka.data.model.ReportItem
import com.appstudio.finmarka.ui.screens.reports.ExportHelper

@Composable
fun ExportButtons(
    context: Context,
    totalIncome: Double,
    totalExpense: Double,
    reportData: List<ReportItem>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ✅ CSV Export
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                ExportHelper.exportCSV(context, reportData)
            }
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("CSV")
        }

        // ✅ Excel Export
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                ExportHelper.exportExcel(context, reportData)
            }
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Excel")
        }

        // ✅ PDF Export
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                ExportHelper.exportPDF(
                    context = context,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    data = reportData
                )
            }
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("PDF")
        }
    }
}
