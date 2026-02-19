
package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.DefaultCategories
import com.appstudio.finmarka.data.local.dao.CategoryDao
import com.appstudio.finmarka.data.local.entity.CategoryEntity
import com.appstudio.finmarka.data.model.TransactionType
import com.appstudio.finmarka.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    fun getCategoriesAsDomain(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { list ->
            list.map { it.toCategory() }
        }
    }

    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type.name).map { list ->
            list.map { it.toCategory() }
        }
    }

    suspend fun getCategoryById(id: Int): CategoryEntity? {
        return categoryDao.getCategoryById(id)
    }

    suspend fun ensureDefaultCategories() {
        val existing = categoryDao.getAllCategories().first()
        val existingKeys = existing.map { "${it.type}::${it.name.lowercase()}" }.toSet()
        DefaultCategories.getDefaultCategories().forEach { cat ->
            val key = "${cat.type}::${cat.name.lowercase()}"
            if (!existingKeys.contains(key)) {
                categoryDao.insert(cat)
            }
        }
    }

    suspend fun getCategoriesOnce(): List<CategoryEntity> {
        ensureDefaultCategories()
        return categoryDao.getAllCategories().first()
    }

    suspend fun addCategory(name: String, type: TransactionType, icon: String = "", color: String = "#6200EE"): Long {
        return categoryDao.insert(
            CategoryEntity(
                name = name,
                type = type.name,
                icon = icon,
                color = color,
                isSystem = false
            )
        )
    }

    suspend fun updateCategory(id: Int, name: String, type: TransactionType, icon: String? = null, color: String? = null) {
        val entity = categoryDao.getCategoryById(id) ?: return
        categoryDao.update(entity.copy(
            name = name,
            type = type.name,
            icon = icon ?: entity.icon,
            color = color ?: entity.color
        ))
    }

    suspend fun deleteCategory(id: Int, reassignToCategoryId: Int?) {
        val category = categoryDao.getCategoryById(id) ?: return
        if (category.isSystem) return

        if (reassignToCategoryId != null) {
            // Reassignment would require updating transactions - for MVP we just delete
            // and leave orphan categoryId (or add foreign key with cascade). Simplified: just delete.
        }
        categoryDao.deleteById(id)
    }

    private fun CategoryEntity.toCategory(): Category {
        return Category(
            id = id,
            name = name,
            type = TransactionType.valueOf(type),
            icon = icon,
            color = color,
            isSystem = isSystem
        )
    }
}
