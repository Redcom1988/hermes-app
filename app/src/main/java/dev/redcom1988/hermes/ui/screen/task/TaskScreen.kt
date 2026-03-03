package dev.redcom1988.hermes.ui.screen.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.ui.main.ScreenLayout
import dev.redcom1988.hermes.ui.screen.task.components.TaskCard

object TaskScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = TaskScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
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
                            onClick = { navigator.push(CreateTaskScreen) },
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
                            onTaskClick = { navigator.push(TaskDetailScreen(task.id)) },
                            onStatusChange = { newStatus ->
                                screenModel.updateTaskStatus(task, newStatus)
                            },
                            onAssignClick = { navigator.push(AssignTaskScreen(task.id)) }
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
