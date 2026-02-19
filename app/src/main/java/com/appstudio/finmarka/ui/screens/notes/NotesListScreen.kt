package com.appstudio.finmarka.ui.screens.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.ui.viewmodel.NotesViewModel

/**
 * Main notes list screen with search + swipe delete + pin + FAB.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    onAddNote: () -> Unit,
    onEditNote: (Int) -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Notes", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = state.search,
                onValueChange = viewModel::setSearch,
                label = { Text("Search notes") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            AnimatedVisibility(visible = state.notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No notes yet. Tap + to create your first note.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }

            if (state.notes.isNotEmpty()) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items = state.notes, key = { it.id }) { note ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.deleteNote(note.id)
                                }
                                false
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFD32F2F)
                                    )
                                }
                            }
                        ) {
                            NoteCard(
                                note = note,
                                onClick = { onEditNote(note.id) },
                                onTogglePin = { viewModel.togglePin(note.id) },
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    }
}
