package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.dao.TransactionDao
import com.appstudio.finmarka.data.local.entity.CategoryEntity
import com.appstudio.finmarka.data.local.entity.TransactionEntity
import com.appstudio.finmarka.data.model.PaymentMode
import com.appstudio.finmarka.data.model.TransactionStatus
import com.appstudio.finmarka.data.model.TransactionType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TransactionRepositoryTest {
    private val transactionDao: TransactionDao = mock()
    private val categoryRepository: CategoryRepository = mock()
    private val repository = TransactionRepository(transactionDao, categoryRepository)

    @Test
    fun `getAllTransactions maps category name when present`() = runTest {
        whenever(transactionDao.getAllTransactions()).thenReturn(
            flowOf(listOf(baseEntity(categoryId = 2)))
        )
        whenever(categoryRepository.getAllCategories()).thenReturn(
            flowOf(listOf(CategoryEntity(id = 2, name = "Food", type = TransactionType.EXPENSE.name)))
        )

        val items = repository.getAllTransactions().first()

        assertEquals("Food", items.first().categoryName)
    }

    @Test
    fun `getAllTransactions uses empty category name when not found`() = runTest {
        whenever(transactionDao.getAllTransactions()).thenReturn(flowOf(listOf(baseEntity(categoryId = 99))))
        whenever(categoryRepository.getAllCategories()).thenReturn(flowOf(emptyList()))

        val items = repository.getAllTransactions().first()

        assertEquals("", items.first().categoryName)
    }

    @Test
    fun `getTransactionById returns null when dao returns null`() = runTest {
        whenever(transactionDao.getTransactionById(5)).thenReturn(null)

        val result = repository.getTransactionById(5)

        assertNull(result)
    }

    @Test
    fun `getTransactionById maps entity to domain transaction`() = runTest {
        whenever(transactionDao.getTransactionById(1)).thenReturn(baseEntity(id = 1, categoryId = 3))
        whenever(categoryRepository.getCategoryById(3)).thenReturn(
            CategoryEntity(id = 3, name = "Rent", type = TransactionType.EXPENSE.name)
        )

        val result = repository.getTransactionById(1)

        assertEquals("Rent", result?.categoryName)
        assertEquals(PaymentMode.CASH, result?.paymentMode)
    }

    @Test
    fun `insertTransaction forwards built entity to dao`() = runTest {
        whenever(transactionDao.insert(any())).thenReturn(77L)

        val id = repository.insertTransaction(
            amount = 100.0,
            currency = "USD",
            convertedAmount = 100.0,
            type = TransactionType.INCOME,
            categoryId = 2,
            dateTime = 1234L,
            note = "salary",
            paymentMode = PaymentMode.BANK,
            accountId = 8,
            accountName = "Main",
            merchantName = "Employer",
            status = TransactionStatus.PENDING,
            isRecurring = true,
            isReimbursement = true,
            isTemplate = true,
            isExcluded = true,
            attachmentUris = "a,b"
        )

        assertEquals(77L, id)
        val captor = argumentCaptor<TransactionEntity>()
        verify(transactionDao).insert(captor.capture())
        assertEquals("INCOME", captor.firstValue.type)
        assertEquals("BANK", captor.firstValue.paymentMode)
        assertEquals("PENDING", captor.firstValue.status)
    }

    @Test
    fun `updateTransaction no-op when entity not found`() = runTest {
        whenever(transactionDao.getTransactionById(999)).thenReturn(null)

        repository.updateTransaction(
            id = 999,
            amount = 1.0,
            currency = "USD",
            convertedAmount = 1.0,
            type = TransactionType.EXPENSE,
            categoryId = 1,
            dateTime = 1L,
            note = null,
            paymentMode = PaymentMode.CASH
        )

        verify(transactionDao, never()).update(any())
    }

    @Test
    fun `updateTransaction updates existing entity`() = runTest {
        whenever(transactionDao.getTransactionById(2)).thenReturn(baseEntity(id = 2))

        repository.updateTransaction(
            id = 2,
            amount = 2.5,
            currency = "EUR",
            convertedAmount = 3.0,
            type = TransactionType.SAVINGS,
            categoryId = 7,
            dateTime = 200L,
            note = "n",
            paymentMode = PaymentMode.CARD,
            status = TransactionStatus.EXCLUDED,
            isRecurring = true,
            isReimbursement = true,
            isTemplate = true,
            isExcluded = true
        )

        val captor = argumentCaptor<TransactionEntity>()
        verify(transactionDao).update(captor.capture())
        assertEquals(2.5, captor.firstValue.amount, 0.0)
        assertEquals("SAVINGS", captor.firstValue.type)
        assertEquals("CARD", captor.firstValue.paymentMode)
        assertEquals("EXCLUDED", captor.firstValue.status)
    }

    private fun baseEntity(id: Int = 0, categoryId: Int = 1): TransactionEntity = TransactionEntity(
        id = id,
        amount = 10.0,
        currency = "USD",
        convertedAmount = 10.0,
        type = TransactionType.EXPENSE.name,
        categoryId = categoryId,
        dateTime = 1000L,
        paymentMode = PaymentMode.CASH.name,
        status = TransactionStatus.COMPLETED.name
    )
}
