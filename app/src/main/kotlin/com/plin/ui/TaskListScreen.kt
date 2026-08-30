package com.plin.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plin.domain.enums.TaskCategory
import com.plin.domain.models.Task
import com.plin.ui.theme.TaskAppearance
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val CheckGreen = Color(0xFF16A34A)
private val FabBottomPadding = 40.dp

/**
 * Main Plin screen: current-week list or full archive, with add / inspect dialogs.
 *
 * Short click toggles completed; long press opens the inspect dialog.
 */
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = viewModel(),
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val currentWeek by viewModel.currentWeek.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTaskKey by remember { mutableStateOf<String?>(null) }
    val selectedTask = tasks.find { "${it.id}-${it.instanceNumber}" == selectedTaskKey }
    val isArchive = selectedTab == TaskListTab.ARCHIVE
    val currentWeekKey = currentWeek?.key.orEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
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
            if (isArchive) {
                Text(
                    text = "Archive",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                )
                Text(
                    text = "Past weeks only. Set assigned week to the current week to restore.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                )
            } else {
                currentWeek?.let { week ->
                    Text(
                        text = week.displayLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    )
                }
                Text(
                    text = "Tap to toggle done · long-press for details.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (tasks.isEmpty()) {
                Text(
                    text = if (isArchive) "No tasks in archive." else "No tasks yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = FabBottomPadding + 56.dp),
                ) {
                    items(tasks, key = { "${it.id}-${it.instanceNumber}" }) { task ->
                        TaskRow(
                            task = task,
                            showWeek = isArchive,
                            onClick = { viewModel.toggleTaskCompletion(task) },
                            onLongClick = { selectedTaskKey = "${task.id}-${task.instanceNumber}" },
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.toggleTab() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = FabBottomPadding),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Text(
                text = if (isArchive) "Week" else "Archive",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        if (!isArchive) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 20.dp, end = 20.dp, bottom = FabBottomPadding),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Text(
                    text = "Add",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, category, isWeekly ->
                viewModel.addTask(title, category, isWeekly)
                showAddDialog = false
            },
        )
    }

    selectedTask?.let { task ->
        TaskInspectDialog(
            task = task,
            currentWeekKey = currentWeekKey,
            onDismiss = { selectedTaskKey = null },
            onUpdate = { updated -> viewModel.updateTask(updated) },
            onCompletedChange = { completed -> viewModel.setCompleted(task, completed) },
            onDelete = {
                viewModel.deleteTask(task)
                selectedTaskKey = null
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskRow(
    task: Task,
    showWeek: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val isDone = task.completed
    val categoryColor = TaskAppearance.colorForCategory(task.category)
    val backgroundColor = TaskAppearance.backgroundForCategory(task.category)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TaskAppearance.shape)
            .background(backgroundColor)
            .border(
                width = TaskAppearance.borderWidth,
                color = categoryColor.copy(alpha = if (isDone) 0.35f else 0.85f),
                shape = TaskAppearance.shape,
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(
                horizontal = TaskAppearance.contentPaddingHorizontal,
                vertical = TaskAppearance.contentPaddingVertical,
            ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (isDone) TextDecoration.LineThrough else null,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = if (isDone) 0.45f else 1f,
                ),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${task.points} pts",
                style = MaterialTheme.typography.bodyMedium,
                color = categoryColor.copy(alpha = if (isDone) 0.45f else 0.9f),
                modifier = Modifier.padding(start = 8.dp),
            )
            if (isDone) {
                Text(
                    text = "✓",
                    color = CheckGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
        if (showWeek) {
            Text(
                text = "${task.assignedWeek.ifEmpty { "—" }} · ${if (isDone) "done" else "open"}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

/**
 * Form for creating a task. Only user-editable fields — no ids or timestamps.
 */
@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: TaskCategory, isWeekly: Boolean) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(TaskCategory.GENERIC) }
    var isWeekly by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Title") },
                )
                CategoryPickerRow(
                    category = category,
                    onCategoryChange = { category = it },
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isWeekly,
                        onCheckedChange = { isWeekly = it },
                    )
                    Text(
                        text = "Repeat every week",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = title.trim()
                    if (trimmed.isNotEmpty()) {
                        onSave(trimmed, category, isWeekly)
                    }
                },
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun CategoryPickerRow(
    category: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Box {
            Text(
                text = formatCategory(category),
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
                TaskCategory.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(formatCategory(option)) },
                        onClick = {
                            onCategoryChange(option)
                            menuOpen = false
                        },
                    )
                }
            }
        }
    }
}

/**
 * Inspect window: edit task details (including assigned week), toggle completed, or delete.
 */
@Composable
private fun TaskInspectDialog(
    task: Task,
    currentWeekKey: String,
    onDismiss: () -> Unit,
    onUpdate: (Task) -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    var title by remember(task.id, task.instanceNumber) { mutableStateOf(task.title) }
    var category by remember(task.id, task.instanceNumber) { mutableStateOf(task.category) }
    var pointsText by remember(task.id, task.instanceNumber) { mutableStateOf(task.points.toString()) }
    var isWeekly by remember(task.id, task.instanceNumber) { mutableStateOf(task.isWeekly) }
    var assignedWeek by remember(task.id, task.instanceNumber) { mutableStateOf(task.assignedWeek) }

    fun saveEditsAndDismiss() {
        val trimmed = title.trim()
        val parsedPoints = pointsText.toIntOrNull()?.coerceAtLeast(0) ?: task.points
        val week = assignedWeek.trim()
        if (trimmed.isNotEmpty()) {
            val updated = task.copy(
                title = trimmed,
                category = category,
                points = parsedPoints,
                isWeekly = isWeekly,
                assignedWeek = week,
            )
            if (
                updated.title != task.title ||
                updated.category != task.category ||
                updated.points != task.points ||
                updated.isWeekly != task.isWeekly ||
                updated.assignedWeek != task.assignedWeek
            ) {
                onUpdate(updated)
            }
        }
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = { saveEditsAndDismiss() },
        title = { Text("Task details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Name") },
                )
                CategoryPickerRow(
                    category = category,
                    onCategoryChange = { category = it },
                )
                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            pointsText = value
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Points") },
                )
                OutlinedTextField(
                    value = assignedWeek,
                    onValueChange = { assignedWeek = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Assigned week") },
                    supportingText = {
                        Text("e.g. 2026-W35")
                    },
                )
                if (currentWeekKey.isNotEmpty()) {
                    TextButton(
                        onClick = { assignedWeek = currentWeekKey },
                    ) {
                        Text("Set to current week ($currentWeekKey)")
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isWeekly,
                        onCheckedChange = { isWeekly = it },
                    )
                    Text(
                        text = "Repeat every week",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = task.completed,
                        onCheckedChange = onCompletedChange,
                    )
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                DetailRow(label = "Created", value = formatCreatedAt(task.createdAt))
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = { saveEditsAndDismiss() }) {
                Text("Close")
            }
        },
    )
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

private fun formatCategory(category: TaskCategory): String =
    category.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }

private fun formatCreatedAt(epochMillis: Long): String {
    val formatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(epochMillis))
}
