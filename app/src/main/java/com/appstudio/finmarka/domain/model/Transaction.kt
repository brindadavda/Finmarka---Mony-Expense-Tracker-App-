package com.appstudio.finmarka.domain.model

import android.icu.util.Currency
import androidx.compose.ui.graphics.Color
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.data.model.PaymentMode
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.ui.theme.ExpenseRed
import com.appstudio.finmarka.ui.theme.IncomeGreen
import com.appstudio.finmarka.ui.util.formatCurrency

data class Transaction(
    val id: Int,
    val amount: Double,
    val convertedAmount: Double,
    val type: TransactionType,
    val categoryId: Int,
    val categoryName: String = "",
    val dateTime: Long,
    val note: String?,
    val paymentMode: PaymentMode,
    val createdTimestamp: Long
) {
    // Formatted display amount with + / - sign
    fun displayAmount(currencyCode: String): String {
        val sign = if (type == TransactionType.INCOME) "+" else "-"
        return "$sign ${formatCurrency(convertedAmount, currencyCode)}"
    }

    // Color based on transaction type
    val displayColor: Color
        get() = if (type == TransactionType.INCOME) IncomeGreen else ExpenseRed
}
