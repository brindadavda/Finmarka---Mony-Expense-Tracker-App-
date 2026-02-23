package com.appstudio.finmarka.ui.viewmodel

import com.appstudio.finmarka.data.local.entity.AccountEntity
import com.appstudio.finmarka.data.repository.AccountsRepository
import com.appstudio.finmarka.data.repository.TransactionRepository
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
class AccountsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val accountsRepository: AccountsRepository = mock()
    private val transactionRepository: TransactionRepository = mock()

    @Test
    fun `filteredAccounts returns all when query blank and filters by name and details`() = runTest {
        whenever(accountsRepository.getAllAccounts()).thenReturn(
            flowOf(
                listOf(
                    AccountEntity(id = 1, name = "Cash Wallet", balance = 0.0, details = "daily", currency = "USD", colorHex = "#1", iconName = "w"),
                    AccountEntity(id = 2, name = "Savings", balance = 0.0, details = "Goal", currency = "USD", colorHex = "#2", iconName = "s")
                )
            )
        )
        whenever(transactionRepository.getAllTransactions()).thenReturn(flowOf(emptyList()))

        val vm = AccountsViewModel(accountsRepository, transactionRepository)
        advanceUntilIdle()

        assertEquals(2, vm.filteredAccounts.value.size)

        vm.updateSearchQuery("wallet")
        advanceUntilIdle()
        assertEquals(1, vm.filteredAccounts.value.size)

        vm.updateSearchQuery("goal")
        advanceUntilIdle()
        assertEquals(1, vm.filteredAccounts.value.size)
    }

    @Test
    fun `init ensures default account and add update delegate to repository`() = runTest {
        whenever(accountsRepository.getAllAccounts()).thenReturn(flowOf(emptyList()))
        whenever(transactionRepository.getAllTransactions()).thenReturn(flowOf(emptyList()))
        whenever(accountsRepository.insertAccount(any())).thenReturn(1L)

        val vm = AccountsViewModel(accountsRepository, transactionRepository)
        val account = AccountEntity(name = "A", balance = 1.0, currency = "USD", colorHex = "#1", iconName = "icon")

        vm.addAccount(account)
        vm.updateAccount(account)
        advanceUntilIdle()

        verify(accountsRepository, times(1)).ensureDefaultAccount()
        verify(accountsRepository, times(1)).insertAccount(account)
        verify(accountsRepository, times(1)).updateAccount(account)
    }
}
