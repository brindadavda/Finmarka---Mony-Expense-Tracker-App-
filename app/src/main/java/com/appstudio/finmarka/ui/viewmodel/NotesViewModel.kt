package com.appstudio.finmarka.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.appstudio.finmarka.ui.screens.notes.NoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NotesUiState(
    val search: String = "",
    val notes: List<NoteItem> = emptyList()
)

/** MVVM state holder for Notes feature with in-memory backing store. */
@HiltViewModel
class NotesViewModel @Inject constructor() : ViewModel() {

    private object InMemoryNotesStore {
        val notes = MutableStateFlow(
            listOf(
                NoteItem(1, "Insurance Renewal", "Renew motor insurance before month end.", now(), now(), "Bills", isPinned = true),
                NoteItem(2, "Emergency Fund", "Keep 6 months of expenses in liquid savings.", now(), now(), "Savings", isImportant = true)
            )
        )
    }

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun setSearch(query: String) {
        _uiState.update { it.copy(search = query) }
        refresh()
    }

    fun getById(id: Int): NoteItem? = InMemoryNotesStore.notes.value.firstOrNull { it.id == id }

    fun saveNote(id: Int?, title: String, content: String, category: String?, important: Boolean, pinned: Boolean) {
        val now = now()
        InMemoryNotesStore.notes.update { current ->
            if (id == null) {
                val nextId = (current.maxOfOrNull { it.id } ?: 0) + 1
                current + NoteItem(nextId, title, content, now, now, category, pinned, important)
            } else {
                current.map {
                    if (it.id == id) it.copy(
                        title = title,
                        content = content,
                        category = category,
                        isImportant = important,
                        isPinned = pinned,
                        updatedAt = now
                    ) else it
                }
            }
        }
        refresh()
    }

    fun deleteNote(id: Int) {
        InMemoryNotesStore.notes.update { list -> list.filterNot { it.id == id } }
        refresh()
    }

    fun togglePin(id: Int) {
        InMemoryNotesStore.notes.update { list ->
            list.map { if (it.id == id) it.copy(isPinned = !it.isPinned, updatedAt = now()) else it }
        }
        refresh()
    }

    private fun refresh() {
        val query = _uiState.value.search
        val filtered = filterNotes(InMemoryNotesStore.notes.value, query)
        _uiState.update { it.copy(notes = sort(filtered)) }
    }

    private fun filterNotes(notes: List<NoteItem>, query: String): List<NoteItem> {
        if (query.isBlank()) return notes
        val q = query.trim().lowercase()
        return notes.filter { it.title.lowercase().contains(q) || it.content.lowercase().contains(q) }
    }

    private fun sort(notes: List<NoteItem>): List<NoteItem> =
        notes.sortedWith(compareByDescending<NoteItem> { it.isPinned }.thenByDescending { it.updatedAt })
}

private fun now(): Long = System.currentTimeMillis()
