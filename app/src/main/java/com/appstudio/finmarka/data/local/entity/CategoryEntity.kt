package com.appstudio.finmarka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // INCOME / EXPENSE
    val icon: String = "",
    val color: String = "#6200EE",
    val isSystem: Boolean = false
)
