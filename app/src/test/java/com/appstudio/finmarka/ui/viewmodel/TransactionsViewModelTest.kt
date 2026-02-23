package com.appstudio.finmarka.ui.viewmodel

import com.appstudio.finmarka.data.local.PreferencesManager
import com.appstudio.finmarka.data.model.PaymentMode
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.data.repository.TransactionRepository
import com.appstudio.finmarka.domain.model.Category
import com.appstudio.finmarka.domain.model.Transaction
import com.appstudio.finmarka.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: TransactionRepository = mock()
    private val preferencesManager: PreferencesManager = mock()

    @Test
    fun `init loads categories and transactions`() = runTest {
        whenever(repository.getAllTransactions()).thenReturn(flowOf(listOf(baseDomainTransaction())))
        whenever(repository.getAllCategories()).thenReturn(flowOf(listOf(Category(id = 1, name = "Food", type = TransactionType.EXPENSE))))
        whenever(preferencesManager.currencyCode).thenReturn("EUR")

        val vm = TransactionsViewModel(repository, preferencesManager)
        advanceUntilIdle()

        assertEquals(false, vm.uiState.value.isLoading)
        assertEquals(1, vm.uiState.value.transactions.size)
        assertEquals(1, vm.uiState.value.categories.size)
        assertEquals("EUR", vm.getCurrencyCode)
    }

    @Test
    fun `setFilter updates state and applies repository filter`() = runTest {
        whenever(repository.getAllTransactions()).thenReturn(flowOf(emptyList()))
        whenever(repository.getAllCategories()).thenReturn(flowOf(emptyList()))
        whenever(repository.filterTransactions(any(), any(), any(), any())).thenReturn(flowOf(listOf(baseDomainTransaction(id = 99))))
        whenever(preferencesManager.currencyCode).thenReturn("USD")

        val vm = TransactionsViewModel(repository, preferencesManager)
        vm.setFilter(3, "EXPENSE")
        advanceUntilIdle()

        assertEquals(3, vm.uiState.value.filterCategoryId)
        assertEquals("EXPENSE", vm.uiState.value.filterType)
        assertEquals(99, vm.uiState.value.transactions.first().id)
    }

    @Test
    fun `deleteTransaction delegates to repository`() = runTest {
        whenever(repository.getAllTransactions()).thenReturn(flowOf(emptyList()))
        whenever(repository.getAllCategories()).thenReturn(flowOf(emptyList()))
        whenever(preferencesManager.currencyCode).thenReturn("USD")

        val vm = TransactionsViewModel(repository, preferencesManager)
        vm.deleteTransaction(42)
        advanceUntilIdle()

        verify(repository, times(1)).deleteTransaction(42)
    }

    private fun baseDomainTransaction(id: Int = 1): Transaction = Transaction(
        id = id,
        amount = 11.0,
        convertedAmount = 11.0,
        type = TransactionType.EXPENSE,
        categoryId = 1,
        categoryName = "Food",
        dateTime = 1L,
        paymentMode = PaymentMode.CASH,
        note = null,
        createdTimestamp = 1L
    )
}
