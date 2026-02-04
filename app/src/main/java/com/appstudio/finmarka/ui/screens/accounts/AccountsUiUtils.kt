package com.appstudio.finmarka.ui.screens.accounts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.graphics.toColorInt

fun String.toColor(): Color = try {
    Color(this.toColorInt())
} catch (e: IllegalArgumentException) {
    Color(0xFF4CAF50)
}

fun String.toIcon(): ImageVector {
    return when (this.lowercase()) {
        "wallet" -> Icons.Default.AccountBalanceWallet
        "bank" -> Icons.Default.AccountBalance
        "card" -> Icons.Default.CreditCard
        "cash" -> Icons.Default.AttachMoney
        else -> Icons.Default.AccountBalanceWallet
    }
}

data class AccountColorOption(val name: String, val hex: String)

val accountColorOptions = listOf(
    AccountColorOption("Green", "#4CAF50"),
    AccountColorOption("Blue", "#2196F3"),
    AccountColorOption("Purple", "#9C27B0"),
    AccountColorOption("Orange", "#FF9800"),
    AccountColorOption("Red", "#F44336")
)

data class AccountIconOption(val name: String, val iconName: String, val imageVector: ImageVector)

val accountIconOptions = listOf(
    AccountIconOption("Wallet", "wallet", Icons.Default.AccountBalanceWallet),
    AccountIconOption("Bank", "bank", Icons.Default.AccountBalance),
    AccountIconOption("Card", "card", Icons.Default.CreditCard),
    AccountIconOption("Cash", "cash", Icons.Default.AttachMoney)
)
