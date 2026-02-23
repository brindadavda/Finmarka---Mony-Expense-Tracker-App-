package com.appstudio.finmarka.ui.viewmodel

import com.appstudio.finmarka.data.local.dao.TodoDao
import com.appstudio.finmarka.data.local.entity.TodoEntity
import com.appstudio.finmarka.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class TodosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val todosFlow = MutableStateFlow<List<TodoEntity>>(emptyList())
    private val todoDao: TodoDao = mock()

    private fun createViewModel(): TodosViewModel {
        whenever(todoDao.getAllTodos()).thenReturn(todosFlow)
        return TodosViewModel(todoDao)
    }

    @Test
    fun `setFilter and toggle methods update ui state`() = runTest {
        val vm = createViewModel()

        vm.setFilter(TodoFilter.SAVINGS)
        vm.toggleCompletedExpanded()
        vm.clearMessage()

        assertEquals(TodoFilter.SAVINGS, vm.uiState.value.filter)
        assertEquals(true, vm.uiState.value.completedExpanded)
        assertNull(vm.uiState.value.message)
    }

    @Test
    fun `addTask validates blank title and prevents insert`() = runTest {
        val vm = createViewModel()

        vm.addTask("  ", TodoFilter.BILLS, 10.0, null)

        assertEquals("Title is required", vm.uiState.value.message)
        verify(todoDao, never()).insert(any())
    }

    @Test
    fun `addTask inserts mapped entity and success message`() = runTest {
        val vm = createViewModel()

        vm.addTask("  Water Bill  ", TodoFilter.BILLS, 45.0, null)
        advanceUntilIdle()

        val captor = argumentCaptor<TodoEntity>()
        verify(todoDao).insert(captor.capture())
        assertEquals("Water Bill", captor.firstValue.title)
        assertEquals("Bills", captor.firstValue.category)
        assertEquals("LOW", captor.firstValue.priority)
        assertEquals("Task added", vm.uiState.value.message)
    }

    @Test
    fun `editTask validates blank title`() = runTest {
        val vm = createViewModel()
        val todo = TodoEntity(id = 1, title = "x")

        vm.editTask(todo, "", 1.0, 1L)

        assertEquals("Title is required", vm.uiState.value.message)
        verify(todoDao, never()).update(any())
    }

    @Test
    fun `markPaid and snooze and editTask update dao`() = runTest {
        val vm = createViewModel()
        val now = System.currentTimeMillis()
        val todo = TodoEntity(id = 1, title = "x", dueDate = now)

        vm.markPaid(todo)
        vm.snooze(todo)
        vm.editTask(todo, "new", 3.0, now + TimeUnit.DAYS.toMillis(5))
        advanceUntilIdle()

        verify(todoDao).update(todo.copy(isCompleted = true))
        verify(todoDao).update(any())
        assertEquals("Task updated", vm.uiState.value.message)
    }

    @Test
    fun `filteredTodos handles all filter branches`() = runTest {
        val vm = createViewModel()
        todosFlow.value = listOf(
            TodoEntity(id = 1, title = "a", category = "Bills"),
            TodoEntity(id = 2, title = "b", category = "Investments"),
            TodoEntity(id = 3, title = "c", category = "Savings")
        )
        advanceUntilIdle()

        vm.setFilter(TodoFilter.ALL)
        assertEquals(3, vm.filteredTodos().size)

        vm.setFilter(TodoFilter.BILLS)
        assertEquals(1, vm.filteredTodos().size)

        vm.setFilter(TodoFilter.INVESTMENTS)
        assertEquals(1, vm.filteredTodos().size)

        vm.setFilter(TodoFilter.SAVINGS)
        assertEquals(1, vm.filteredTodos().size)
    }

    @Test
    fun `groupOf handles completed overdue today upcoming`() = runTest {
        val vm = createViewModel()
        val day = TimeUnit.DAYS.toMillis(1)
        val now = 5 * day

        assertEquals(TodoGroup.COMPLETED, vm.groupOf(TodoEntity(id = 1, title = "x", isCompleted = true), now))
        assertEquals(TodoGroup.OVERDUE, vm.groupOf(TodoEntity(id = 1, title = "x", dueDate = now - day), now))
        assertEquals(TodoGroup.TODAY, vm.groupOf(TodoEntity(id = 1, title = "x", dueDate = now), now))
        assertEquals(TodoGroup.UPCOMING, vm.groupOf(TodoEntity(id = 1, title = "x", dueDate = now + day), now))
    }
}
