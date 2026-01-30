package com.appstudio.finmarka.data.local

import com.appstudio.finmarka.data.local.entity.CategoryEntity
import com.appstudio.finmarka.data.model.TransactionType

object DefaultCategories {
    // Expense: Food, Transport, Shopping, Bills, Entertainment, Health, Others
    private val expenseCategories = listOf(
        Triple("Food", "restaurant", "#4CAF50"),
        Triple("Transport", "directions_car", "#2196F3"),
        Triple("Shopping", "shopping_cart", "#FF9800"),
        Triple("Bills", "receipt_long", "#607D8B"),
        Triple("Entertainment", "movie", "#E91E63"),
        Triple("Health", "local_hospital", "#F44336"),
        Triple("Others", "category", "#795548")
    )
    // Income: Salary, Business, Others
    private val incomeCategories = listOf(
        Triple("Salary", "payments", "#4CAF50"),
        Triple("Business", "business_center", "#2196F3"),
        Triple("Others", "savings", "#9C27B0")
    )

    fun getDefaultCategories(): List<CategoryEntity> {
        val list = mutableListOf<CategoryEntity>()
        expenseCategories.forEach { (name, icon, color) ->
            list.add(
                CategoryEntity(
                    name = name,
                    type = TransactionType.EXPENSE.name,
                    icon = icon,
                    color = color
                )
            )
        }
        incomeCategories.forEach { (name, icon, color) ->
            list.add(
                CategoryEntity(
                    name = name,
                    type = TransactionType.INCOME.name,
                    icon = icon,
                    color = color
                )
            )
        }
        return list
    }
}
