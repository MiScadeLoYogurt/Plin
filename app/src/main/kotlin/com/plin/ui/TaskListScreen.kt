package com.plin.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plin.domain.enums.TaskStatus
import com.plin.domain.models.Task
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val CheckGreen = Color(0xFF16A34A)

/**
 * First Plin screen: add a task and see the list from the local database.
 *
 * Short click marks a task completed; long press opens the inspect dialog.
 */
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = viewModel(),
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    var draft by rememberSaveable { mutableStateOf("") }
    var selectedTaskId by remember { mutableStateOf<Long?>(null) }
    val selectedTask = tasks.find { it.id == selectedTaskId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 48.dp),
    ) {
        Text(
            text = "Plin",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Tap to complete · long-press for details.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("New task") },
            )
            Button(
                onClick = {
                    val title = draft.trim()
                    if (title.isNotEmpty()) {
                        viewModel.addTask(title)
                        draft = ""
                    }
                },
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (tasks.isEmpty()) {
            Text(
                text = "No tasks yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onClick = { viewModel.completeTask(task) },
                        onLongClick = { selectedTaskId = task.id },
                    )
                }
            }
        }
    }

    selectedTask?.let { task ->
        TaskInspectDialog(
            task = task,
            onDismiss = { selectedTaskId = null },
            onStatusChange = { status -> viewModel.setStatus(task, status) },
            onDelete = {
                viewModel.deleteTask(task)
                selectedTaskId = null
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskRow(
    task: Task,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val isDone = task.status == TaskStatus.COMPLETED

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            textDecoration = if (isDone) TextDecoration.LineThrough else null,
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = if (isDone) 0.45f else 1f,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 28.dp)
                .align(Alignment.CenterStart),
        )
        if (isDone) {
            Text(
                text = "✓",
                color = CheckGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

/**
 * Inspect window: shows task details, editable status, and delete.
 */
@Composable
private fun TaskInspectDialog(
    task: Task,
    onDismiss: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Task details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailRow(label = "Name", value = task.title)
                StatusDetailRow(
                    status = task.status,
                    onStatusChange = onStatusChange,
                )
                DetailRow(label = "Created", value = formatCreatedAt(task.createdAt))
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}

@Composable
private fun StatusDetailRow(
    status: TaskStatus,
    onStatusChange: (TaskStatus) -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Status",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Box {
            Text(
                text = status.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { menuOpen = true }
                    .padding(vertical = 2.dp),
            )
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
            ) {
                TaskStatus.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name) },
                        onClick = {
                            onStatusChange(option)
                            menuOpen = false
                        },
                    )
                }
            }
        }
        Text(
            text = "Tap to change",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun formatCreatedAt(epochMillis: Long): String {
    val formatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(epochMillis))
}
