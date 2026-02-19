package com.appstudio.finmarka.ui.screens.notes

/**
 * Lightweight in-memory note model used by Notes feature.
 */
data class NoteItem(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val category: String? = null,
    val isPinned: Boolean = false,
    val isImportant: Boolean = false
)
