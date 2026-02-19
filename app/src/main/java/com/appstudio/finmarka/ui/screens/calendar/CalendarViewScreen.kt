package com.appstudio.finmarka.ui.screens.calendar

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Switch
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
import androidx.core.content.ContextCompat
import com.appstudio.finmarka.notifications.ReminderScheduler
import com.appstudio.finmarka.ui.screens.common.ModuleScaffold
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class CalendarEvent(
    val id: Int,
    val dateTime: Long,
    val title: String,
    val type: String,
    val reminderEnabled: Boolean
)

@Composable
fun CalendarViewScreen() {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val dateTimeFormatter = remember { SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()) }

    var selectedDate by rememberSaveable { mutableStateOf(dateFormatter.format(calendar.time)) }
    var selectedTime by rememberSaveable { mutableStateOf(timeFormatter.format(calendar.time)) }
    var title by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("Note") }
    var setReminder by rememberSaveable { mutableStateOf(true) }
    var statusMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val events = remember {
        mutableStateListOf(
            CalendarEvent(1, calendar.timeInMillis, "Loan EMI", "Loan", true),
            CalendarEvent(2, calendar.timeInMillis, "Mom Birthday", "Birthday", false)
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        statusMessage = if (granted) {
            "Notification permission granted."
        } else {
            "Notification permission denied. Reminders may not appear."
        }
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

    val timePicker = remember {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                selectedTime = timeFormatter.format(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
    }

    ModuleScaffold(
        title = "Calendar",
        subtitle = "Add notes, birthdays, loan dates, and set reminders"
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }) {
                        Text("Enable notifications")
                    }
                }
            }
        }

        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text("Selected date") }
        )

        OutlinedTextField(
            value = selectedTime,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text("Selected time") }
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { datePicker.show() }) {
                Text("Pick date")
            }
            Button(onClick = { timePicker.show() }) {
                Text("Pick time")
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Set reminder")
            Switch(checked = setReminder, onCheckedChange = { setReminder = it })
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = {
                if (title.isBlank()) {
                    statusMessage = "Please enter event title."
                    return@Button
                }

                val event = CalendarEvent(
                    id = (events.maxOfOrNull { it.id } ?: 0) + 1,
                    dateTime = calendar.timeInMillis,
                    title = title.trim(),
                    type = type.trim().ifBlank { "Note" },
                    reminderEnabled = setReminder
                )
                events.add(event)

                if (setReminder) {
                    if (event.dateTime <= System.currentTimeMillis()) {
                        statusMessage = "Event added, but reminder time must be in the future."
                    } else {
                        val scheduled = ReminderScheduler.scheduleReminder(
                            context = context,
                            triggerAtMillis = event.dateTime,
                            title = "${event.type} Reminder",
                            body = event.title,
                            requestCode = event.id
                        )
                        statusMessage = if (scheduled) {
                            "Reminder scheduled for ${dateTimeFormatter.format(calendar.time)}"
                        } else {
                            "Event added, but reminder could not be scheduled on this device."
                        }
                    }
                } else {
                    statusMessage = "Event added without reminder."
                }

                title = ""
                type = "Note"
            }) {
                Text("Add to calendar")
            }
        }

        statusMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }

        Text("Upcoming Entries", style = MaterialTheme.typography.titleMedium)

        events.sortedBy { it.dateTime }.forEach { event ->
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
                    val whenText = dateTimeFormatter.format(java.util.Date(event.dateTime))
                    val reminderLabel = if (event.reminderEnabled) "Reminder ON" else "Reminder OFF"
                    Text("$whenText · ${event.type} · ${event.title} · $reminderLabel")
                    IconButton(onClick = { events.remove(event) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete calendar entry")
                    }
                }
            }
        }
    }
}
