package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.data.local.dao.BudgetDao
import com.appstudio.finmarka.data.local.entity.BudgetEntity
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.data.repository.TransactionRepository
import com.appstudio.finmarka.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class BudgetItemUi(
    val categoryId: Int,
    val name: String,
    val limit: Double,
    val spent: Double
)

data class BudgetUiState(
    val isLoading: Boolean = true,
    val budgets: List<BudgetItemUi> = emptyList(),
    val availableCategories: List<Category> = emptyList()
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetDao: BudgetDao,
    private val transactionRepository: TransactionRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState

    val currencyCode: String
        get() = preferencesManager.currencyCode

    init {
        observeBudgetData()
    }

    fun addBudget(categoryId: Int, limitAmount: Double) {
        if (limitAmount <= 0.0) return

        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            val existing = budgetDao.getBudgetForCategory(categoryId, month, year)

            if (existing != null) {
                budgetDao.update(existing.copy(limitAmount = limitAmount))
            } else {
                budgetDao.insert(
                    BudgetEntity(
                        categoryId = categoryId,
                        limitAmount = limitAmount,
                        month = month,
                        year = year
                    )
                )
            }
        }
    }

    private fun observeBudgetData() {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        viewModelScope.launch {
            combine(
                budgetDao.getBudgetsForMonth(month, year),
                transactionRepository.getAllTransactions(),
                transactionRepository.getAllCategories()
            ) { budgets, transactions, categories ->
                val expenseCategories = categories.filter { it.type == TransactionType.EXPENSE }
                val spentByCategory = transactions
                    .asSequence()
                    .filter { it.type == TransactionType.EXPENSE }
                    .filter { transaction ->
                        calendar.timeInMillis = transaction.dateTime
                        val txMonth = calendar.get(Calendar.MONTH) + 1
                        val txYear = calendar.get(Calendar.YEAR)
                        txMonth == month && txYear == year
                    }
                    .groupBy { it.categoryId }
                    .mapValues { (_, list) -> list.sumOf { it.convertedAmount } }

                mapUiState(
                    budgets = budgets,
                    expenseCategories = expenseCategories,
                    spentByCategory = spentByCategory
                )
            }.collect { ui ->
                _uiState.value = ui
            }
        }
    }

    private fun mapUiState(
        budgets: List<BudgetEntity>,
        expenseCategories: List<Category>,
        spentByCategory: Map<Int, Double>
    ): BudgetUiState {
        val budgetItems = budgets
            .map { budget ->
                BudgetItemUi(
                    categoryId = budget.categoryId,
                    name = expenseCategories.find { it.id == budget.categoryId }?.name ?: "Unknown",
                    limit = budget.limitAmount,
                    spent = spentByCategory[budget.categoryId] ?: 0.0
                )
            }
            .sortedBy { it.name }

        val usedCategoryIds = budgetItems.map { it.categoryId }.toSet()

        return BudgetUiState(
            isLoading = false,
            budgets = budgetItems,
            availableCategories = expenseCategories
                .filterNot { it.id in usedCategoryIds }
                .sortedBy { it.name }
        )
    }
}
