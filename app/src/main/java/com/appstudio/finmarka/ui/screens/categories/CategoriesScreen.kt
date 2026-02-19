package com.appstudio.finmarka.ui.screens.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import com.appstudio.finmarka.ui.viewmodel.CategoriesViewModel

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ModuleScaffold(
        title = "Categories",
        subtitle = "System categories are fixed. You can add and delete your custom categories."
    ) {
        OutlinedTextField(
            value = state.newCategoryName,
            onValueChange = viewModel::setNewCategoryName,
            label = { Text("Category name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.newCategoryIcon,
            onValueChange = viewModel::setNewCategoryIcon,
            label = { Text("Icon (emoji)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.newCategoryType == TransactionType.EXPENSE,
                    onClick = { viewModel.setNewCategoryType(TransactionType.EXPENSE) }
                )
                Text("Expense")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.newCategoryType == TransactionType.INCOME,
                    onClick = { viewModel.setNewCategoryType(TransactionType.INCOME) }
                )
                Text("Income")
            }
            Button(onClick = viewModel::addCategory) {
                Text("Add")
            }
        }

        state.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Text("All Categories", style = MaterialTheme.typography.titleMedium)

        state.categories.forEach { category ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${category.icon} ${category.name} (${category.type.name.lowercase().replaceFirstChar { c -> c.uppercase() }})"
                    )
                    if (category.isSystem) {
                        Text("System", style = MaterialTheme.typography.labelSmall)
                    } else {
                        IconButton(onClick = { viewModel.deleteCategory(category.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete category")
                        }
                    }
                }
            }
        }
    }
}
