package com.appstudio.finmarka.ui.screens.calendar

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class CalendarEvent(
    val id: Int,
    val date: String,
    val title: String,
    val type: String
)

@Composable
fun CalendarViewScreen() {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    var selectedDate by rememberSaveable { mutableStateOf(dateFormatter.format(calendar.time)) }
    var title by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("Note") }
    val events = remember {
        mutableStateListOf(
            CalendarEvent(1, selectedDate, "Loan EMI", "Loan"),
            CalendarEvent(2, selectedDate, "Mom Birthday", "Birthday")
        )
    }

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                selectedDate = dateFormatter.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    ModuleScaffold(
        title = "Calendar",
        subtitle = "Add notes, birthdays, loan dates, and reminders"
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text("Selected date") }
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { datePicker.show() }) {
                Text("Pick date")
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Note / Event title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Type (Note, Birthday, Loan, etc.)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = {
                if (title.isNotBlank()) {
                    events.add(
                        CalendarEvent(
                            id = (events.maxOfOrNull { it.id } ?: 0) + 1,
                            date = selectedDate,
                            title = title.trim(),
                            type = type.trim().ifBlank { "Note" }
                        )
                    )
                    title = ""
                    type = "Note"
                }
            }) {
                Text("Add to calendar")
            }
        }

        Text("Upcoming Entries", style = MaterialTheme.typography.titleMedium)

        events.sortedBy { it.date }.forEach { event ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${event.date} · ${event.type} · ${event.title}")
                    IconButton(onClick = { events.remove(event) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete calendar entry")
                    }
                }
            }
        }
    }
}
