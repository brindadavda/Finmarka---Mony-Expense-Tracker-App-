package com.appstudio.finmarka.ui.screens.reports

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.appstudio.finmarka.data.model.ReportItem
import java.io.File
import java.io.FileWriter

object ExportHelper {

    // --------------------------------------------------
    // ✅ Export CSV + Auto Open
    // --------------------------------------------------
    fun exportCSV(context: Context, data: List<ReportItem>) {

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "Finmarka_Report.csv"
        )

        FileWriter(file).use { writer ->
            writer.append("Category,Amount\n")
            data.forEach {
                writer.append("${it.label},${it.amount}\n")
            }
        }

        Toast.makeText(context, "CSV Exported Successfully!", Toast.LENGTH_SHORT).show()

        openFile(context, file, "text/csv")
    }

    // --------------------------------------------------
    // ✅ Export Excel + Auto Open
    // --------------------------------------------------
    fun exportExcel(context: Context, data: List<ReportItem>) {

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "Finmarka_Report.xls"
        )

        FileWriter(file).use { writer ->
            writer.append("Category\tAmount\n")
            data.forEach {
                writer.append("${it.label}\t${it.amount}\n")
            }
        }

        Toast.makeText(context, "Excel Exported Successfully!", Toast.LENGTH_SHORT).show()

        openFile(context, file, "application/vnd.ms-excel")
    }

    // --------------------------------------------------
    // ✅ Export PDF + Auto Open
    // --------------------------------------------------
    fun exportPDF(
        context: Context,
        totalIncome: Double,
        totalExpense: Double,
        data: List<ReportItem>
    ) {

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "Finmarka_Report.pdf"
        )

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(600, 900, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()

        // ✅ Title
        paint.textSize = 22f
        canvas.drawText("Finmarka Financial Report", 50f, 80f, paint)

        // ✅ Summary
        paint.textSize = 18f
        canvas.drawText("Total Income: ₹$totalIncome", 50f, 140f, paint)
        canvas.drawText("Total Expense: ₹$totalExpense", 50f, 180f, paint)

        // ✅ Report Items Table
        paint.textSize = 16f
        var yPosition = 250f

        canvas.drawText("Category Breakdown:", 50f, yPosition, paint)

        yPosition += 40f

        data.forEach {
            canvas.drawText("${it.label} : ₹${it.amount}", 60f, yPosition, paint)
            yPosition += 30f
        }

        document.finishPage(page)

        // ✅ Save PDF
        file.outputStream().use {
            document.writeTo(it)
        }

        document.close()

        Toast.makeText(context, "PDF Exported Successfully!", Toast.LENGTH_SHORT).show()

        openFile(context, file, "application/pdf")
    }

    // --------------------------------------------------
    // ✅ Open File Automatically
    // --------------------------------------------------
    private fun openFile(context: Context, file: File, mimeType: String) {

        try {
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "No app found to open this file!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
