package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.DefaultCategories
import com.appstudio.finmarka.data.local.dao.CategoryDao
import com.appstudio.finmarka.data.local.entity.CategoryEntity
import com.appstudio.finmarka.data.model.TransactionType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CategoryRepositoryTest {

    private val categoryDao: CategoryDao = mock()
    private val repository = CategoryRepository(categoryDao)

    @Test
    fun `getCategoriesByType maps entity list to domain categories`() = runTest {
        whenever(categoryDao.getCategoriesByType(TransactionType.EXPENSE.name)).thenReturn(
            flowOf(
                listOf(
                    CategoryEntity(id = 10, name = "Food", type = TransactionType.EXPENSE.name, icon = "🍔", color = "#fff", isSystem = false)
                )
            )
        )

        val result = repository.getCategoriesByType(TransactionType.EXPENSE).first()

        assertEquals(1, result.size)
        assertEquals("Food", result.first().name)
        assertEquals(TransactionType.EXPENSE, result.first().type)
    }

    @Test
    fun `ensureDefaultCategories inserts only missing defaults`() = runTest {
        val existing = listOf(
            CategoryEntity(name = "Rent / Mortgage", type = TransactionType.EXPENSE.name, isSystem = true),
            CategoryEntity(name = "Salary / Wages", type = TransactionType.INCOME.name, isSystem = true)
        )
        whenever(categoryDao.getAllCategories()).thenReturn(flowOf(existing))

        repository.ensureDefaultCategories()

        val expectedMissing = DefaultCategories.getDefaultCategories().size - existing.size
        verify(categoryDao, times(expectedMissing)).insert(any())
    }

    @Test
    fun `updateCategory updates record when category exists`() = runTest {
        val original = CategoryEntity(id = 8, name = "Old", type = TransactionType.INCOME.name, icon = "a", color = "b")
        whenever(categoryDao.getCategoryById(8)).thenReturn(original)

        repository.updateCategory(id = 8, name = "New", type = TransactionType.EXPENSE, icon = null, color = null)

        verify(categoryDao).update(original.copy(name = "New", type = TransactionType.EXPENSE.name, icon = "a", color = "b"))
    }

    @Test
    fun `updateCategory no-op when category does not exist`() = runTest {
        whenever(categoryDao.getCategoryById(99)).thenReturn(null)

        repository.updateCategory(id = 99, name = "X", type = TransactionType.EXPENSE)

        verify(categoryDao, never()).update(any())
    }

    @Test
    fun `deleteCategory no-op when category not found or system category`() = runTest {
        whenever(categoryDao.getCategoryById(1)).thenReturn(null)
        repository.deleteCategory(1, null)
        verify(categoryDao, never()).deleteById(any())

        whenever(categoryDao.getCategoryById(2)).thenReturn(
            CategoryEntity(id = 2, name = "System", type = TransactionType.EXPENSE.name, isSystem = true)
        )
        repository.deleteCategory(2, null)
        verify(categoryDao, never()).deleteById(2)
    }

    @Test
    fun `deleteCategory deletes non-system category`() = runTest {
        whenever(categoryDao.getCategoryById(3)).thenReturn(
            CategoryEntity(id = 3, name = "Mine", type = TransactionType.EXPENSE.name, isSystem = false)
        )

        repository.deleteCategory(3, reassignToCategoryId = 11)

        verify(categoryDao).deleteById(3)
    }

    @Test
    fun `addCategory inserts non-system category entity`() = runTest {
        whenever(categoryDao.insert(any())).thenReturn(44L)

        val id = repository.addCategory("Books", TransactionType.EXPENSE, "📚", "#ccc")

        assertEquals(44L, id)
        verify(categoryDao).insert(any())
    }

    @Test
    fun `getCategoriesOnce ensures defaults before returning list`() = runTest {
        whenever(categoryDao.getAllCategories()).thenReturn(flowOf(emptyList()), flowOf(listOf(CategoryEntity(name = "A", type = TransactionType.EXPENSE.name))))

        val result = repository.getCategoriesOnce()

        assertTrue(result.isNotEmpty())
        verify(categoryDao).getAllCategories()
    }
}
