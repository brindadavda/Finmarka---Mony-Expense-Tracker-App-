package com.appstudio.finmarka.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

fun formatCurrency(amount: Double, currencyCode: String = "USD"): String {
    val symbol = when (currencyCode) {
        "INR" -> "₹"
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        else -> currencyCode
    }
    return "$symbol ${String.format(Locale.US, "%.2f", amount)}"
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
}

fun formatDateShort(timestamp: Long): String {
    return SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
}

fun formatDateTime(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
}

/** Travel expense format: 30 Jan 2025 · 3:15 AM */
fun formatDateTimeTravel(timestamp: Long): String {
    val date = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(timestamp))
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
    return "$date · $time"
}
