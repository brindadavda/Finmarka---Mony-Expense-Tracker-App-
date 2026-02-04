package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.dao.FinanceDao
import com.appstudio.finmarka.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsRepository @Inject constructor(
    private val financeDao: FinanceDao
) {
    fun getAllAccounts(): Flow<List<AccountEntity>> = financeDao.getAllAccounts()

    suspend fun getAccountById(id: Int): AccountEntity? = financeDao.getAccountById(id)

    suspend fun insertAccount(account: AccountEntity): Long = financeDao.insertAccount(account)

    suspend fun updateAccount(account: AccountEntity) = financeDao.updateAccount(account)

    suspend fun deleteAccount(account: AccountEntity) = financeDao.deleteAccount(account)

    suspend fun ensureDefaultAccount() {
        val existing = financeDao.getAllAccounts().first()
        if (existing.isEmpty()) {
            financeDao.insertAccount(
                AccountEntity(
                    name = "Cash",
                    balance = 0.0,
                    currency = "INR",
                    colorHex = "#4CAF50",
                    iconName = "wallet",
                    pinned = true,
                    excluded = false,
                    isCreditCard = false
                )
            )
        }
    }
}
