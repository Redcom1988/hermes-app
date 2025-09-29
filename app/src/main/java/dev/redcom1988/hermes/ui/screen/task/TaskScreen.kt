package dev.redcom1988.hermes.ui.screen.task

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.ui.main.ScreenLayout
import dev.redcom1988.hermes.ui.screen.task.components.AssignTaskDialog
import dev.redcom1988.hermes.ui.screen.task.components.CreateTaskDialog
import dev.redcom1988.hermes.ui.screen.task.components.TaskCard
import dev.redcom1988.hermes.ui.screen.task.components.TaskDetailDialog

object TaskScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = TaskScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { TaskScreenModel() }
        val state by screenModel.state.collectAsState()

        ScreenLayout(
            screen = TaskScreen,
            title = getScreenTitle(state.userDivision, state.isAdmin),
            isLoading = state.isLoading,
            loadingText = "Loading tasks...",
            floatingActionButton = {
                if (screenModel.canCreateTasks()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        FloatingActionButton(
                            onClick = { screenModel.showCreateTaskDialog() },
                            modifier = Modifier.padding(16.dp),
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Task"
                            )
                        }
                    }
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header section with different UI based on division
                item {
                    when {
                        state.userDivision == DivisionType.DEV -> {
                            DeveloperHeader(taskCount = state.tasks.size)
                        }
                        state.userDivision == DivisionType.PM || state.isAdmin -> {
                            ManagerAdminHeader(
                                taskCount = state.tasks.size,
                                canCreateTasks = screenModel.canCreateTasks()
                            )
                        }
                    }
                }

                // Tasks list or empty state
                if (state.tasks.isEmpty() && !state.isLoading) {
                    item {
                        EmptyTasksCard(
                            userDivision = state.userDivision,
                            isAdmin = state.isAdmin
                        )
                    }
                } else {
                    items(state.tasks) { task ->
                        TaskCard(
                            task = task,
                            canAssign = screenModel.canAssignTasks(),
                            canDelete = screenModel.canDeleteTasks(),
                            onTaskClick = { screenModel.showTaskDetail(task) },
                            onStatusChange = { newStatus ->
                                screenModel.updateTaskStatus(task, newStatus)
                            },
                            onAssignClick = { screenModel.showAssignTaskDialog(task) }
                        )
                    }
                }

                // Error handling
                state.errorMessage?.let { message ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = message,
                                modifier = Modifier.padding(20.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Extra spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Dialogs
        if (state.showCreateTaskDialog) {
            CreateTaskDialog(
                isAdmin = state.isAdmin,
                onCreateTask = { name, description, deadline ->
                    screenModel.createTask(name, description, deadline)
                },
                onDismiss = { screenModel.hideCreateTaskDialog() }
            )
        }

        if (state.showTaskDetailDialog && state.selectedTask != null) {
            TaskDetailDialog(
                task = state.selectedTask!!,
                canDelete = screenModel.canDeleteTasks(),
                onUpdateNote = { note ->
                    screenModel.updateTaskNote(state.selectedTask!!, note)
                },
                onStatusChange = { newStatus ->
                    screenModel.updateTaskStatus(state.selectedTask!!, newStatus)
                },
                onDeleteTask = {
                    screenModel.deleteTask(state.selectedTask!!)
                },
                onDismiss = { screenModel.hideTaskDetail() }
            )
        }

        if (state.showAssignTaskDialog && state.selectedTask != null) {
            AssignTaskDialog(
                task = state.selectedTask!!,
                employees = state.employees,
                initiallyAssignedEmployeeIds = state.assignedEmployeeIds,
                onAssignTask = { employeeIds ->
                    screenModel.updateTaskAssignments(state.selectedTask!!, employeeIds)
                },
                onDismiss = { screenModel.hideAssignTaskDialog() }
            )
        }
    }

    @Composable
    private fun getScreenTitle(userDivision: DivisionType?, isAdmin: Boolean): String {
        return when {
            userDivision == DivisionType.DEV -> "My Tasks"
            userDivision == DivisionType.PM -> "Project Tasks"
            isAdmin -> "All Tasks"
            else -> "Tasks"
        }
    }

    @Composable
    private fun DeveloperHeader(taskCount: Int) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Assigned Tasks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$taskCount tasks assigned to you",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }

    @Composable
    private fun ManagerAdminHeader(
        taskCount: Int,
        canCreateTasks: Boolean
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Task Management",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "$taskCount total tasks",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun EmptyTasksCard(
        userDivision: DivisionType?,
        isAdmin: Boolean
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = when {
                        userDivision == DivisionType.DEV -> "No tasks assigned"
                        userDivision == DivisionType.PM -> "No tasks created yet"
                        isAdmin -> "No tasks in the system"
                        else -> "No tasks available"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = when {
                        userDivision == DivisionType.DEV -> "You don't have any tasks assigned to you."
                        userDivision == DivisionType.PM -> "Start by creating and assigning tasks to developers."
                        isAdmin -> "Create tasks and assign them to team members."
                        else -> "Check back later for new tasks."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
