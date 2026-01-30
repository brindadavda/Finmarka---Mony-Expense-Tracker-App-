package com.appstudio.finmarka.data.repository

import android.content.Context
import android.net.Uri
import com.appstudio.finmarka.data.local.dao.BudgetDao
import com.appstudio.finmarka.data.local.dao.CategoryDao
import com.appstudio.finmarka.data.local.dao.TransactionDao
import com.appstudio.finmarka.data.local.entity.BudgetEntity
import com.appstudio.finmarka.data.local.entity.CategoryEntity
import com.appstudio.finmarka.data.local.entity.TransactionEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val transactions: List<TransactionEntity>,
    val categories: List<CategoryEntity>,
    val budgets: List<BudgetEntity>
)

@Singleton
class BackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val gson: Gson
) {

    suspend fun exportToJson(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val transactions = transactionDao.getAllTransactions().first()
            val categories = categoryDao.getAllCategories().first()
            val budgets = budgetDao.getAllBudgets().first()
            context.contentResolver.openOutputStream(uri)?.use { out ->
                OutputStreamWriter(out).use { writer ->
                    val data = BackupData(
                        transactions = transactions,
                        categories = categories,
                        budgets = budgets
                    )
                    writer.write(gson.toJson(data))
                }
            } ?: return@withContext Result.failure(Exception("Could not open output"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportToCsv(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val transactions = transactionDao.getAllTransactions().first()
            context.contentResolver.openOutputStream(uri)?.use { out ->
                OutputStreamWriter(out).use { writer ->
                    writer.write("id,amount,currency,convertedAmount,type,categoryId,dateTime,note,paymentMode,createdTimestamp\n")
                    transactions.forEach { t ->
                        writer.write("${t.id},${t.amount},${t.currency},${t.convertedAmount},${t.type},${t.categoryId},${t.dateTime},${t.note ?: ""},${t.paymentMode},${t.createdTimestamp}\n")
                    }
                }
            } ?: return@withContext Result.failure(Exception("Could not open output"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importFromJson(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                ?: return@withContext Result.failure(Exception("Could not read file"))
            val type = object : TypeToken<BackupData>() {}.type
            val data: BackupData = gson.fromJson(json, type)
            data.categories.forEach { categoryDao.insert(it) }
            data.transactions.forEach { transactionDao.insert(it) }
            data.budgets.forEach { budgetDao.insert(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
