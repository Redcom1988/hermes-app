package dev.redcom1988.hermes.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.ui.components.CurrentStatusCard
import dev.redcom1988.hermes.ui.components.TaskSelectionDialog
import dev.redcom1988.hermes.ui.main.ScreenLayout

object HomeScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = HomeScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { HomeScreenModel() }
        val state by screenModel.state.collectAsState()
        val context = LocalContext.current
        var showTaskSelection by remember { mutableStateOf(false) }

        ScreenLayout(
            screen = HomeScreen,
            title = "Home",
            isLoading = state.isLoading,
            loadingText = "Processing attendance..."
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Welcome section (for all users)
                    WelcomeCard(
                        userName = state.employeeDetails?.fullName ?: "User",
                        userRole = state.userRole,
                        isEmployee = state.isEmployee
                    )

                    // Show attendance card only for employees
                    if (state.isEmployee) {
                        CurrentStatusCard(
                            isCheckedIn = state.isCheckedIn,
                            currentTime = state.currentAttendanceTime,
                            todayTotalHours = state.todayTotalWorkHours,
                            requiredHours = state.requiredWorkHours,
                            canCheckIn = state.canCheckIn,
                            isLoading = state.isLoading,
                            onCheckIn = { screenModel.checkIn(context) },
                            onCheckOut = { showTaskSelection = true }
                        )
                    }

                    // Employee Details Card (for employees only)
                    state.employeeDetails?.let { details ->
                        EmployeeDetailsCard(details = details)
                    }

                    // Admin Statistics Card (for admin only)
                    state.adminStats?.let { stats ->
                        AdminStatisticsCard(stats = stats)
                    }

                    // Assigned Tasks Card (for PM and developers)
                    if (state.isEmployee && state.unfinishedTasks.isNotEmpty()) {
                        AssignedTasksCard(
                            unfinishedTasks = state.unfinishedTasks,
                            totalTasks = state.assignedTasks.size
                        )
                    }

                    // Error Card
                    state.errorMessage?.let { message ->
                        ErrorCard(message = message)
                    }
                }
            }
        }

        // Task Selection Dialog
        if (showTaskSelection) {
            TaskSelectionDialog(
                tasks = state.tasks,
                onTasksSelected = { selectedTasks ->
                    screenModel.checkOut(selectedTasks)
                    showTaskSelection = false
                },
                onDismiss = { showTaskSelection = false }
            )
        }
    }
}

@Composable
private fun EmployeeDetailsCard(details: EmployeeDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Employee Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            DetailRow(label = "Name", value = details.fullName)
            DetailRow(label = "Division", value = details.divisionName)
            details.phoneNumber?.let {
                DetailRow(label = "Phone", value = it)
            }
            details.gender?.let {
                DetailRow(label = "Gender", value = it.replaceFirstChar { it.titlecase() })
            }
            details.email?.let {
                DetailRow(label = "Email", value = it)
            }
        }
    }
}

@Composable
private fun AdminStatisticsCard(stats: AdminStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "System Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    label = "Services",
                    count = stats.totalServices,
                    modifier = Modifier.weight(1f)
                )
                StatisticItem(
                    label = "Clients",
                    count = stats.totalClients,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    label = "Users",
                    count = stats.totalUsers,
                    modifier = Modifier.weight(1f)
                )
                StatisticItem(
                    label = "Employees",
                    count = stats.totalEmployees,
                    modifier = Modifier.weight(1f)
                )
            }

            StatisticItem(
                label = "Unfinished Tasks",
                count = stats.unfinishedTasksCount,
                isHighlight = stats.unfinishedTasksCount > 0,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AssignedTasksCard(unfinishedTasks: List<Task>, totalTasks: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Your Tasks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Tasks: $totalTasks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${unfinishedTasks.size} Unfinished",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (unfinishedTasks.isNotEmpty())
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            if (unfinishedTasks.isNotEmpty()) {
                Text(
                    text = "Recent Unfinished Tasks:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                unfinishedTasks.take(3).forEach { task ->
                    Text(
                        text = "• ${task.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (unfinishedTasks.size > 3) {
                    Text(
                        text = "... and ${unfinishedTasks.size - 3} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeCard(userName: String, userRole: String, isEmployee: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isEmployee) "Welcome back, $userName" else "Welcome to Hermes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isEmployee) userRole.replaceFirstChar { it.titlecase() } else "Administrator Dashboard",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isEmployee)
                        "Track your attendance and manage your tasks"
                    else
                        "Monitor system statistics and manage operations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
private fun StatisticItem(
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
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
