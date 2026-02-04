package com.appstudio.finmarka.ui.screens.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.local.entity.AccountEntity
import com.appstudio.finmarka.ui.util.formatCurrency
import com.appstudio.finmarka.ui.viewmodel.AccountsViewModel

@Composable
fun AccountDetailScreen(
    accountId: Int,
    onNavigateBack: () -> Unit,
    onQuickAction: (Int) -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    var account by remember { mutableStateOf<AccountEntity?>(null) }

    LaunchedEffect(accountId) {
        account = viewModel.getAccountById(accountId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            account?.let { details ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(details.colorHex.toColor()),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = details.iconName.toIcon(),
                            contentDescription = details.name,
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(details.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(
                            formatCurrency(details.balance, details.currency),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onQuickAction(details.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Income Transaction")
                    }
                    Button(
                        onClick = { onQuickAction(details.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("- Expense Transaction")
                    }
                    Button(
                        onClick = { onQuickAction(details.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Transfer In")
                    }
                    Button(
                        onClick = { onQuickAction(details.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Transfer Out")
                    }
                }
            } ?: Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
