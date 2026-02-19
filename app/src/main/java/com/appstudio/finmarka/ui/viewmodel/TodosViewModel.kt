package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appstudio.finmarka.data.local.dao.TodoDao
import com.appstudio.finmarka.data.local.entity.TodoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

enum class TodoFilter { ALL, BILLS, INVESTMENTS, SAVINGS }
enum class TodoGroup { OVERDUE, TODAY, UPCOMING, COMPLETED }
enum class TodoPriority { LOW, SOON, OVERDUE }

// UI state for Finance Todo screen
data class TodosUiState(
    val todos: List<TodoEntity> = emptyList(),
    val filter: TodoFilter = TodoFilter.ALL,
    val completedExpanded: Boolean = false,
    val message: String? = null
)

@HiltViewModel
// ViewModel keeps task CRUD + filter/group logic for the screen
class TodosViewModel @Inject constructor(
    private val todoDao: TodoDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodosUiState())
    val uiState: StateFlow<TodosUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            todoDao.getAllTodos().collectLatest { list ->
                _uiState.update { it.copy(todos = list) }
            }
        }
    }

    fun setFilter(filter: TodoFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun toggleCompletedExpanded() {
        _uiState.update { it.copy(completedExpanded = !it.completedExpanded) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun addTask(title: String, category: TodoFilter, amount: Double, dueDate: Long?) {
        if (title.isBlank()) {
            _uiState.update { it.copy(message = "Title is required") }
            return
        }
        viewModelScope.launch {
            todoDao.insert(
                TodoEntity(
                    title = title.trim(),
                    category = categoryLabel(category),
                    amount = amount,
                    priority = priorityForDueDate(dueDate).name,
                    dueDate = dueDate
                )
            )
            _uiState.update { it.copy(message = "Task added") }
        }
    }

    fun markPaid(todo: TodoEntity) {
        viewModelScope.launch {
            todoDao.update(todo.copy(isCompleted = true))
            _uiState.update { it.copy(message = "Marked paid") }
        }
    }

    fun snooze(todo: TodoEntity) {
        viewModelScope.launch {
            val nextDue = (todo.dueDate ?: System.currentTimeMillis()) + TimeUnit.DAYS.toMillis(1)
            todoDao.update(
                todo.copy(
                    dueDate = nextDue,
                    priority = priorityForDueDate(nextDue).name
                )
            )
            _uiState.update { it.copy(message = "Snoozed by 1 day") }
        }
    }

    fun editTask(todo: TodoEntity, title: String, amount: Double, dueDate: Long?) {
        if (title.isBlank()) {
            _uiState.update { it.copy(message = "Title is required") }
            return
        }
        viewModelScope.launch {
            todoDao.update(
                todo.copy(
                    title = title.trim(),
                    amount = amount,
                    dueDate = dueDate,
                    priority = priorityForDueDate(dueDate).name
                )
            )
            _uiState.update { it.copy(message = "Task updated") }
        }
    }

    fun filteredTodos(): List<TodoEntity> {
        val state = uiState.value
        return state.todos.filter { todo ->
            when (state.filter) {
                TodoFilter.ALL -> true
                TodoFilter.BILLS -> todo.category.equals("Bills", true)
                TodoFilter.INVESTMENTS -> todo.category.equals("Investments", true)
                TodoFilter.SAVINGS -> todo.category.equals("Savings", true)
            }
        }
    }

    fun groupOf(todo: TodoEntity, now: Long = System.currentTimeMillis()): TodoGroup {
        if (todo.isCompleted) return TodoGroup.COMPLETED
        val due = todo.dueDate ?: Long.MAX_VALUE
        val dayStart = now - (now % TimeUnit.DAYS.toMillis(1))
        val nextDay = dayStart + TimeUnit.DAYS.toMillis(1)

        return when {
            due < dayStart -> TodoGroup.OVERDUE
            due in dayStart until nextDay -> TodoGroup.TODAY
            else -> TodoGroup.UPCOMING
        }
    }

    private fun categoryLabel(filter: TodoFilter): String = when (filter) {
        TodoFilter.ALL, TodoFilter.BILLS -> "Bills"
        TodoFilter.INVESTMENTS -> "Investments"
        TodoFilter.SAVINGS -> "Savings"
    }

    private fun priorityForDueDate(dueDate: Long?): TodoPriority {
        val due = dueDate ?: return TodoPriority.LOW
        val now = System.currentTimeMillis()
        return when {
            due < now -> TodoPriority.OVERDUE
            due - now <= TimeUnit.DAYS.toMillis(2) -> TodoPriority.SOON
            else -> TodoPriority.LOW
        }
    }
}
