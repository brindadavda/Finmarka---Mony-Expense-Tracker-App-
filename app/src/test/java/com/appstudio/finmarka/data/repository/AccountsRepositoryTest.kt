package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.dao.FinanceDao
import com.appstudio.finmarka.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AccountsRepositoryTest {

    private val financeDao: FinanceDao = mock()
    private val repository = AccountsRepository(financeDao)

    @Test
    fun `ensureDefaultAccount inserts default account when account list is empty`() = runTest {
        whenever(financeDao.getAllAccounts()).thenReturn(flowOf(emptyList()))

        repository.ensureDefaultAccount()

        val captor: ArgumentCaptor<AccountEntity> = argumentCaptor()
        verify(financeDao, times(1)).insertAccount(captor.capture())
        assertEquals("Cash", captor.value.name)
        assertEquals("INR", captor.value.currency)
        assertEquals(true, captor.value.pinned)
    }

    @Test
    fun `ensureDefaultAccount does not insert when account list is not empty`() = runTest {
        whenever(financeDao.getAllAccounts()).thenReturn(
            flowOf(listOf(AccountEntity(id = 1, name = "Bank", balance = 10.0, currency = "USD", colorHex = "#111", iconName = "bank")))
        )

        repository.ensureDefaultAccount()

        verify(financeDao, never()).insertAccount(org.mockito.kotlin.any())
    }
}
