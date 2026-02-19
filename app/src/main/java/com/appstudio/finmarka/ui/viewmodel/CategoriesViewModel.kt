package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.data.repository.CategoryRepository
import com.appstudio.finmarka.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val newCategoryName: String = "",
    val newCategoryIcon: String = "🏷️",
    val newCategoryType: TransactionType = TransactionType.EXPENSE,
    val error: String? = null
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.ensureDefaultCategories()
            categoryRepository.getCategoriesAsDomain().collectLatest { list ->
                _uiState.update { it.copy(categories = list) }
            }
        }
    }

    fun setNewCategoryName(name: String) {
        _uiState.update { it.copy(newCategoryName = name, error = null) }
    }

    fun setNewCategoryIcon(icon: String) {
        _uiState.update { it.copy(newCategoryIcon = icon.ifBlank { "🏷️" }, error = null) }
    }

    fun setNewCategoryType(type: TransactionType) {
        _uiState.update { it.copy(newCategoryType = type, error = null) }
    }

    fun addCategory() {
        val state = _uiState.value
        if (state.newCategoryName.isBlank()) {
            _uiState.update { it.copy(error = "Category name is required") }
            return
        }

        viewModelScope.launch {
            categoryRepository.addCategory(
                name = state.newCategoryName.trim(),
                type = state.newCategoryType,
                icon = state.newCategoryIcon.trim().ifBlank { "🏷️" }
            )
            _uiState.update {
                it.copy(
                    newCategoryName = "",
                    newCategoryIcon = "🏷️",
                    error = null
                )
            }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(id, null)
        }
    }
}
