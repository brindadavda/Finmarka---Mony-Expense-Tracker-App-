package com.appstudio.finmarka.ui.screens.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import kotlin.math.ln
import kotlin.math.pow

@Composable
fun CalculatorsScreen() {
    var principal by rememberSaveable { mutableStateOf("") }
    var annualRate by rememberSaveable { mutableStateOf("") }
    var tenureYears by rememberSaveable { mutableStateOf("") }

    var savingsMonthly by rememberSaveable { mutableStateOf("") }
    var savingsRate by rememberSaveable { mutableStateOf("") }
    var savingsYears by rememberSaveable { mutableStateOf("") }

    var loanAmount by rememberSaveable { mutableStateOf("") }
    var loanRate by rememberSaveable { mutableStateOf("") }
    var loanMonthlyPayment by rememberSaveable { mutableStateOf("") }

    val emiResult = remember(principal, annualRate, tenureYears) {
        val p = principal.toDoubleOrNull() ?: return@remember "-"
        val r = (annualRate.toDoubleOrNull() ?: return@remember "-") / 12 / 100
        val n = (tenureYears.toDoubleOrNull() ?: return@remember "-") * 12
        if (p <= 0 || n <= 0) return@remember "-"

        val emi = if (r == 0.0) {
            p / n
        } else {
            p * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
        }
        "%.2f".format(emi)
    }

    val savingsResult = remember(savingsMonthly, savingsRate, savingsYears) {
        val monthly = savingsMonthly.toDoubleOrNull() ?: return@remember "-"
        val rate = (savingsRate.toDoubleOrNull() ?: return@remember "-") / 12 / 100
        val months = (savingsYears.toDoubleOrNull() ?: return@remember "-") * 12
        if (monthly <= 0 || months <= 0) return@remember "-"

        val futureValue = if (rate == 0.0) {
            monthly * months
        } else {
            monthly * (((1 + rate).pow(months) - 1) / rate)
        }
        "%.2f".format(futureValue)
    }

    val loanPayoffResult = remember(loanAmount, loanRate, loanMonthlyPayment) {
        val amount = loanAmount.toDoubleOrNull() ?: return@remember "-"
        val r = (loanRate.toDoubleOrNull() ?: return@remember "-") / 12 / 100
        val payment = loanMonthlyPayment.toDoubleOrNull() ?: return@remember "-"
        if (amount <= 0 || payment <= 0) return@remember "-"

        val months = if (r == 0.0) {
            amount / payment
        } else {
            if (payment <= amount * r) return@remember "Payment too low"
            -ln(1 - (amount * r / payment)) / ln(1 + r)
        }

        "~${months.toInt()} months"
    }

    ModuleScaffold(
        title = "Calculators",
        subtitle = "EMI, savings growth, and loan payoff calculators"
    ) {
        Text("EMI Calculator", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = principal, onValueChange = { principal = it }, label = { Text("Loan amount") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = annualRate, onValueChange = { annualRate = it }, label = { Text("Annual interest %") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = tenureYears, onValueChange = { tenureYears = it }, label = { Text("Tenure (years)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        ResultCard(label = "Estimated Monthly EMI", value = emiResult)

        Text("Savings Calculator", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = savingsMonthly, onValueChange = { savingsMonthly = it }, label = { Text("Monthly savings") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = savingsRate, onValueChange = { savingsRate = it }, label = { Text("Annual return %") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = savingsYears, onValueChange = { savingsYears = it }, label = { Text("Years") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        ResultCard(label = "Estimated Future Value", value = savingsResult)

        Text("Loan Payoff Calculator", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = loanAmount, onValueChange = { loanAmount = it }, label = { Text("Current loan balance") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = loanRate, onValueChange = { loanRate = it }, label = { Text("Annual interest %") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = loanMonthlyPayment, onValueChange = { loanMonthlyPayment = it }, label = { Text("Monthly payment") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        ResultCard(label = "Estimated Payoff Time", value = loanPayoffResult)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                principal = ""
                annualRate = ""
                tenureYears = ""
                savingsMonthly = ""
                savingsRate = ""
                savingsYears = ""
                loanAmount = ""
                loanRate = ""
                loanMonthlyPayment = ""
            }) {
                Text("Clear all")
            }
        }
    }
}

@Composable
private fun ResultCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label)
            Text(text = value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
