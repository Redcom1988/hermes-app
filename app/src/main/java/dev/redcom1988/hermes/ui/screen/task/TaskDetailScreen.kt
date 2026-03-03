package dev.redcom1988.hermes.ui.screen.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskStatus
import dev.redcom1988.hermes.ui.components.StatusChip
import dev.redcom1988.hermes.ui.main.FormLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class TaskDetailScreen(
    val taskId: Int
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { TaskDetailScreenModel(taskId) }
        val state by screenModel.state.collectAsState()
        var showDeleteConfirmation by remember { mutableStateOf(false) }

        FormLayout(
            title = state.task?.name ?: "Task Details",
            onBack = { navigator.pop() },
            actions = {
                // Delete button in app bar
                if (screenModel.canDeleteTask()) {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                state.task?.let { task ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Task Info Card
                        item {
                            TaskInfoCard(
                                task = task,
                                canEdit = screenModel.canEditTask(),
                                onEdit = { navigator.push(EditTaskScreen(taskId)) },
                                onStatusChange = { newStatus ->
                                    screenModel.updateTaskStatus(newStatus)
                                }
                            )
                        }

                        // Assigned Employees Card
                        if (state.assignedEmployees.isNotEmpty()) {
                            item {
                                AssignedEmployeesCard(
                                    assignedEmployees = state.assignedEmployees,
                                    canAssign = screenModel.canAssignTask(),
                                    onAssign = { navigator.push(AssignTaskScreen(taskId)) }
                                )
                            }
                        }

                        // Notes Card
                        item {
                            NotesCard(
                                note = task.note,
                                onNoteChange = { newNote ->
                                    screenModel.updateTaskNote(newNote)
                                }
                            )
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Task not found",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Error handling
            state.errorMessage?.let { message ->
                LaunchedEffect(message) {
                    // Could show snackbar here
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            screenModel.deleteTask {
                                navigator.pop()
                            }
                            showDeleteConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun TaskInfoCard(
    task: Task,
    canEdit: Boolean,
    onEdit: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Task Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Edit icon button
                if (canEdit) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Task Name
            InfoRow(label = "Task Name", value = task.name)

            // Description
            if (!task.description.isNullOrBlank()) {
                InfoRow(label = "Description", value = task.description)
            }

            // Deadline
            InfoRow(label = "Deadline", value = formatDeadline(task.deadline))

            // Status Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskStatus.entries.forEach { status ->
                        StatusChip(
                            text = status.label,
                            isSelected = status == task.status,
                            onClick = { onStatusChange(status) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignedEmployeesCard(
    assignedEmployees: List<dev.redcom1988.hermes.ui.screen.task.EmployeeWithDivision>,
    canAssign: Boolean,
    onAssign: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Assigned Team",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${assignedEmployees.size} assigned",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Assign icon button
                    if (canAssign) {
                        IconButton(onClick = onAssign) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Assign Team",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            assignedEmployees.forEach { employeeWithDivision ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = employeeWithDivision.employee.fullName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = employeeWithDivision.divisionName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesCard(
    note: String?,
    onNoteChange: (String) -> Unit
) {
    var noteText by remember { mutableStateOf(note ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (!isEditing) {
                    TextButton(onClick = { isEditing = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isEditing) {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("Add notes about this task...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 6
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        noteText = note ?: ""
                        isEditing = false
                    }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onNoteChange(noteText)
                        isEditing = false
                    }) {
                        Text("Save")
                    }
                }
            } else {
                if (noteText.isNotBlank()) {
                    Text(
                        text = noteText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = "No notes added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun formatDeadline(deadline: String): String {
    return try {
        val date = LocalDate.parse(deadline)
        date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    } catch (e: DateTimeParseException) {
        deadline
    }
}
