package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.dao.TransactionDao
import com.appstudio.finmarka.data.local.entity.TransactionEntity
import com.appstudio.finmarka.data.model.PaymentMode
import com.appstudio.finmarka.data.model.TransactionStatus
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Category
import com.appstudio.finmarka.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryRepository: CategoryRepository
) {

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().combine(categoryRepository.getAllCategories()) { transactions, categories ->
            transactions.map { entity ->
                entity.toTransaction(
                    categories.find { it.id == entity.categoryId }?.name ?: ""
                )
            }
        }
    }

    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(limit).combine(categoryRepository.getAllCategories()) { transactions, categories ->
            transactions.map { entity ->
                entity.toTransaction(
                    categories.find { it.id == entity.categoryId }?.name ?: ""
                )
            }
        }
    }

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate).combine(categoryRepository.getAllCategories()) { transactions, categories ->
            transactions.map { entity ->
                entity.toTransaction(
                    categories.find { it.id == entity.categoryId }?.name ?: ""
                )
            }
        }
    }

    fun filterTransactions(
        startDate: Long,
        endDate: Long,
        categoryId: Int,
        type: String?
    ): Flow<List<Transaction>> {
        return transactionDao.filterTransactions(startDate, endDate, categoryId, type)
            .combine(categoryRepository.getAllCategories()) { transactions, categories ->
                transactions.map { entity ->
                    entity.toTransaction(
                        categories.find { it.id == entity.categoryId }?.name ?: ""
                    )
                }
            }
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        val entity = transactionDao.getTransactionById(id) ?: return null
        val categoryName = categoryRepository.getCategoryById(entity.categoryId)?.name ?: ""
        return entity.toTransaction(categoryName)
    }

    suspend fun insertTransaction(
        amount: Double,
        currency: String,
        convertedAmount: Double,
        type: TransactionType,
        categoryId: Int,
        dateTime: Long,
        note: String?,
        paymentMode: PaymentMode,
        accountId: Int? = null,
        merchantName: String? = null,
        status: TransactionStatus = TransactionStatus.COMPLETED,
        isRecurring: Boolean = false,
        isReimbursement: Boolean = false,
        isTemplate: Boolean = false,
        isExcluded: Boolean = false,
        attachmentUris: String? = null
    ): Long {
        return transactionDao.insert(
            TransactionEntity(
                amount = amount,
                currency = currency,
                convertedAmount = convertedAmount,
                type = type.name,
                categoryId = categoryId,
                accountId = accountId,
                merchantName = merchantName,
                dateTime = dateTime,
                note = note,
                paymentMode = paymentMode.name,
                status = status.name,
                isRecurring = isRecurring,
                isReimbursement = isReimbursement,
                isTemplate = isTemplate,
                isExcluded = isExcluded,
                attachmentUris = attachmentUris
            )
        )
    }

    suspend fun updateTransaction(
        id: Int,
        amount: Double,
        currency: String,
        convertedAmount: Double,
        type: TransactionType,
        categoryId: Int,
        dateTime: Long,
        note: String?,
        paymentMode: PaymentMode,
        accountId: Int? = null,
        merchantName: String? = null,
        status: TransactionStatus = TransactionStatus.COMPLETED,
        isRecurring: Boolean = false,
        isReimbursement: Boolean = false,
        isTemplate: Boolean = false,
        isExcluded: Boolean = false,
        attachmentUris: String? = null
    ) {
        val entity = transactionDao.getTransactionById(id) ?: return
        transactionDao.update(
            entity.copy(
                amount = amount,
                currency = currency,
                convertedAmount = convertedAmount,
                type = type.name,
                categoryId = categoryId,
                accountId = accountId,
                merchantName = merchantName,
                dateTime = dateTime,
                note = note,
                paymentMode = paymentMode.name,
                status = status.name,
                isRecurring = isRecurring,
                isReimbursement = isReimbursement,
                isTemplate = isTemplate,
                isExcluded = isExcluded,
                attachmentUris = attachmentUris
            )
        )
    }

    suspend fun deleteTransaction(id: Int) {
        transactionDao.deleteById(id)
    }

    suspend fun getTotalIncome(startDate: Long, endDate: Long): Double {
        return transactionDao.getTotalIncome(startDate, endDate)
    }

    suspend fun getTotalExpense(startDate: Long, endDate: Long): Double {
        return transactionDao.getTotalExpense(startDate, endDate)
    }

    fun getTotalIncomeFlow(): Flow<Double> {
        return transactionDao.getTotalIncomeFlow()
    }

    fun getTotalExpenseFlow(): Flow<Double> {
        return transactionDao.getTotalExpenseFlow()
    }

    fun getTotalIncomeFlow(startDate: Long, endDate: Long): Flow<Double> {
        return transactionDao.getTotalIncomeFlow(startDate, endDate)
    }

    fun getTotalExpenseFlow(startDate: Long, endDate: Long): Flow<Double> {
        return transactionDao.getTotalExpenseFlow(startDate, endDate)
    }

    suspend fun getExpenseByCategory(categoryId: Int, startDate: Long, endDate: Long): Double {
        return transactionDao.getExpenseSumByCategory(categoryId, startDate, endDate)
    }

    private fun TransactionEntity.toTransaction(categoryName: String): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            convertedAmount = convertedAmount,
            type = TransactionType.valueOf(type),
            categoryId = categoryId,
            categoryName = categoryName,
            accountId = accountId,
            merchantName = merchantName,
            dateTime = dateTime,
            note = note,
            paymentMode = PaymentMode.valueOf(paymentMode),
            status = TransactionStatus.valueOf(status),
            isRecurring = isRecurring,
            isReimbursement = isReimbursement,
            isTemplate = isTemplate,
            isExcluded = isExcluded,
            attachmentUris = attachmentUris,
            createdTimestamp = createdTimestamp
        )
    }

    fun getAllCategories(): Flow<List<Category>> {
        return categoryRepository.getCategoriesAsDomain()
    }

}
