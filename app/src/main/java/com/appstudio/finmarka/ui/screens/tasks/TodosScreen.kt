package com.appstudio.finmarka.ui.screens.tasks

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appstudio.finmarka.data.local.entity.TodoEntity
import com.appstudio.finmarka.ui.viewmodel.TodoFilter
import com.appstudio.finmarka.ui.viewmodel.TodoGroup
import com.appstudio.finmarka.ui.viewmodel.TodosViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodosScreen(
    viewModel: TodosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddSheet by rememberSaveable { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<TodoEntity?>(null) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Group records into Overdue/Today/Upcoming/Completed buckets
    val grouped = viewModel.filteredTodos().groupBy { viewModel.groupOf(it) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Finance Todo", style = MaterialTheme.typography.headlineSmall)
            FilterTabs(selected = state.filter, onSelect = viewModel::setFilter)

            TodoGroupSection("Overdue", grouped[TodoGroup.OVERDUE].orEmpty(), viewModel, onEdit = { editTarget = it })
            TodoGroupSection("Today", grouped[TodoGroup.TODAY].orEmpty(), viewModel, onEdit = { editTarget = it })
            TodoGroupSection("Upcoming", grouped[TodoGroup.UPCOMING].orEmpty(), viewModel, onEdit = { editTarget = it })
            CollapsibleCompletedSection(
                expanded = state.completedExpanded,
                onToggle = viewModel::toggleCompletedExpanded,
                tasks = grouped[TodoGroup.COMPLETED].orEmpty(),
                viewModel = viewModel,
                onEdit = { editTarget = it }
            )
        }
    }

    if (showAddSheet) {
        TaskEditorDialog(
            title = "Add Task",
            initialTitle = "",
            initialAmount = "",
            initialCategory = state.filter.takeIf { it != TodoFilter.ALL } ?: TodoFilter.BILLS,
            initialDueDate = null,
            onDismiss = { showAddSheet = false },
            onSave = { t, c, a, d ->
                viewModel.addTask(t, c, a.toDoubleOrNull() ?: 0.0, d)
                showAddSheet = false
            }
        )
    }

    editTarget?.let { task ->
        TaskEditorDialog(
            title = "Edit Task",
            initialTitle = task.title,
            initialAmount = task.amount.toString(),
            initialCategory = when (task.category.lowercase()) {
                "investments" -> TodoFilter.INVESTMENTS
                "savings" -> TodoFilter.SAVINGS
                else -> TodoFilter.BILLS
            },
            initialDueDate = task.dueDate,
            onDismiss = { editTarget = null },
            onSave = { t, _, a, d ->
                viewModel.editTask(task, t, a.toDoubleOrNull() ?: task.amount, d)
                editTarget = null
            }
        )
    }
}

@Composable
private fun FilterTabs(selected: TodoFilter, onSelect: (TodoFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        listOf(TodoFilter.ALL, TodoFilter.BILLS, TodoFilter.INVESTMENTS, TodoFilter.SAVINGS).forEach { filter ->
            val active = selected == filter
            Card(
                modifier = Modifier.clickable { onSelect(filter) },
                colors = CardDefaults.cardColors(
                    containerColor = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoGroupSection(
    title: String,
    tasks: List<TodoEntity>,
    viewModel: TodosViewModel,
    onEdit: (TodoEntity) -> Unit
) {
    if (tasks.isEmpty()) return
    Text(title, style = MaterialTheme.typography.titleMedium)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tasks.forEach { todo ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    when (value) {
                        SwipeToDismissBoxValue.EndToStart -> {
                            viewModel.snooze(todo)
                            false
                        }
                        SwipeToDismissBoxValue.StartToEnd -> {
                            viewModel.markPaid(todo)
                            false
                        }
                        else -> false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF455A64), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mark Paid", color = Color.White)
                        Text("Edit / Snooze", color = Color.White)
                    }
                }
            ) {
                TodoCard(todo = todo, onMarkPaid = { viewModel.markPaid(todo) }, onEdit = { onEdit(todo) }, onSnooze = { viewModel.snooze(todo) })
            }
        }
    }
}

@Composable
private fun CollapsibleCompletedSection(
    expanded: Boolean,
    onToggle: () -> Unit,
    tasks: List<TodoEntity>,
    viewModel: TodosViewModel,
    onEdit: (TodoEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth().clickable { onToggle() }, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Completed")
                Text(if (expanded) "Hide" else "Show")
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                tasks.forEach { todo ->
                    TodoCard(todo = todo, onMarkPaid = { viewModel.markPaid(todo) }, onEdit = { onEdit(todo) }, onSnooze = { viewModel.snooze(todo) })
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun TodoCard(todo: TodoEntity, onMarkPaid: () -> Unit, onEdit: () -> Unit, onSnooze: () -> Unit) {
    val dueText = todo.dueDate?.let { SimpleDateFormat("dd MMM", Locale.getDefault()).format(java.util.Date(it)) } ?: "No due date"
    val priorityColor = when (todo.priority.uppercase()) {
        "OVERDUE" -> Color(0xFFD32F2F)
        "SOON" -> Color(0xFFFFA000)
        else -> Color(0xFF388E3C)
    }
    val categoryIcon = when (todo.category.lowercase()) {
        "bills" -> "💳"
        "investments" -> "📈"
        "savings" -> "💰"
        else -> "📝"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row {
            Box(modifier = Modifier
                .padding(start = 0.dp)
                .background(priorityColor)
                .fillMaxWidth(0.015f)
                .height(112.dp))
            Column(modifier = Modifier.padding(12.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("$categoryIcon  ${todo.title}", style = MaterialTheme.typography.titleMedium)
                Text("₹ ${"%,.0f".format(todo.amount)}")
                Text("Due: $dueText")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onMarkPaid, enabled = !todo.isCompleted) { Text("Mark Paid") }
                    Button(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = null); Text(" Edit") }
                    Button(onClick = onSnooze) { Icon(Icons.Default.Notifications, contentDescription = null); Text(" Snooze") }
                }
            }
        }
    }
}

@Composable
private fun TaskEditorDialog(
    title: String,
    initialTitle: String,
    initialAmount: String,
    initialCategory: TodoFilter,
    initialDueDate: Long?,
    onDismiss: () -> Unit,
    onSave: (String, TodoFilter, String, Long?) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = remember { Calendar.getInstance().apply { initialDueDate?.let { timeInMillis = it } } }
    var taskTitle by rememberSaveable { mutableStateOf(initialTitle) }
    var amount by rememberSaveable { mutableStateOf(initialAmount) }
    var category by rememberSaveable { mutableStateOf(initialCategory) }
    var dueDate by rememberSaveable { mutableStateOf(initialDueDate) }

    val picker = remember {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                calendar.set(y, m, d, 10, 0)
                dueDate = calendar.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(value = taskTitle, onValueChange = { taskTitle = it }, label = { Text("Task title") })
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(TodoFilter.BILLS, TodoFilter.INVESTMENTS, TodoFilter.SAVINGS).forEach {
                        Button(onClick = { category = it }) { Text(it.name.lowercase().replaceFirstChar { c -> c.uppercase() }) }
                    }
                }
                Button(onClick = { picker.show() }) { Text("Pick due date") }
                dueDate?.let { Text("Due: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(java.util.Date(it))}") }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = { onSave(taskTitle, category, amount, dueDate) }) { Text("Save") }
                }
            }
        }
    }
}
