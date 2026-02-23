package com.appstudio.finmarka.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.appstudio.finmarka.data.local.dao.BudgetDao
import com.appstudio.finmarka.data.local.dao.CategoryDao
import com.appstudio.finmarka.data.local.dao.TransactionDao
import com.appstudio.finmarka.data.local.entity.BudgetEntity
import com.appstudio.finmarka.data.local.entity.CategoryEntity
import com.appstudio.finmarka.data.local.entity.TransactionEntity
import com.appstudio.finmarka.data.model.PaymentMode
import com.appstudio.finmarka.data.model.TransactionType
import com.google.gson.Gson
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class BackupRepositoryTest {

    private val context: Context = mock()
    private val contentResolver: ContentResolver = mock()
    private val transactionDao: TransactionDao = mock()
    private val categoryDao: CategoryDao = mock()
    private val budgetDao: BudgetDao = mock()
    private val gson = Gson()
    private val uri: Uri = mock()

    private val repository = BackupRepository(context, transactionDao, categoryDao, budgetDao, gson)

    @Test
    fun `exportToJson returns success when streams available`() = runTest {
        whenever(context.contentResolver).thenReturn(contentResolver)
        whenever(contentResolver.openOutputStream(uri)).thenReturn(ByteArrayOutputStream())
        whenever(transactionDao.getAllTransactions()).thenReturn(flowOf(listOf(baseTransaction())))
        whenever(categoryDao.getAllCategories()).thenReturn(flowOf(listOf(CategoryEntity(name = "Food", type = TransactionType.EXPENSE.name))))
        whenever(budgetDao.getAllBudgets()).thenReturn(flowOf(listOf(BudgetEntity(categoryId = 1, limitAmount = 1.0, month = 1, year = 2025))))

        val result = repository.exportToJson(uri)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `exportToCsv returns failure when output stream missing`() = runTest {
        whenever(context.contentResolver).thenReturn(contentResolver)
        whenever(contentResolver.openOutputStream(uri)).thenReturn(null)
        whenever(transactionDao.getAllTransactions()).thenReturn(flowOf(emptyList()))

        val result = repository.exportToCsv(uri)

        assertTrue(result.isFailure)
    }

    @Test
    fun `importFromJson inserts parsed data and returns success`() = runTest {
        whenever(context.contentResolver).thenReturn(contentResolver)
        val data = BackupData(
            transactions = listOf(baseTransaction()),
            categories = listOf(CategoryEntity(name = "Food", type = TransactionType.EXPENSE.name)),
            budgets = listOf(BudgetEntity(categoryId = 1, limitAmount = 1.0, month = 1, year = 2025))
        )
        whenever(contentResolver.openInputStream(uri)).thenReturn(ByteArrayInputStream(gson.toJson(data).toByteArray()))
        whenever(transactionDao.insert(any())).thenReturn(1L)
        whenever(categoryDao.insert(any())).thenReturn(1L)
        whenever(budgetDao.insert(any())).thenReturn(1L)

        val result = repository.importFromJson(uri)

        assertTrue(result.isSuccess)
        verify(transactionDao, times(1)).insert(any())
        verify(categoryDao, times(1)).insert(any())
        verify(budgetDao, times(1)).insert(any())
    }

    private fun baseTransaction() = TransactionEntity(
        amount = 1.0,
        currency = "USD",
        convertedAmount = 1.0,
        type = TransactionType.EXPENSE.name,
        categoryId = 1,
        dateTime = 1L,
        paymentMode = PaymentMode.CASH.name
    )
}
