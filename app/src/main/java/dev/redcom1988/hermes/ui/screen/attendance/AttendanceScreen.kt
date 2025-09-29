package dev.redcom1988.hermes.ui.screen.attendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.ui.components.CircularIconButton
import dev.redcom1988.hermes.ui.components.CurrentStatusCard
import dev.redcom1988.hermes.ui.components.TaskSelectionDialog
import dev.redcom1988.hermes.ui.main.ScreenLayout
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.text.format
import kotlin.time.Duration

object AttendanceScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = AttendanceScreen

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { AttendanceScreenModel() }
        val state by screenModel.state.collectAsState()
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        var showTaskSelection by remember { mutableStateOf(false) }

        ScreenLayout(
            screen = this,
            title = "Attendance",
            isLoading = state.isLoading,
            loadingText = "Processing attendance..."
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // smaller spacing
            ) {
                // Current Status Card (compact, themed)
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

                // Calendar Section
                CalendarSection(
                    selectedDate = state.selectedDate,
                    onDateSelected = { screenModel.setSelectedDate(it) },
                    markedDates = state.attendance.mapNotNull { attendance ->
                        runCatching { attendance.createdAt.toLocalDateTime().toLocalDate() }.getOrNull()
                    }.toSet(),
                    currentMonth = state.currentMonth,
                    onMonthChanged = { screenModel.setCurrentMonth(it) }
                )

                // Attendance(s) for selected date
                AttendanceListForSelectedDate(
                    attendances = state.filteredAttendances,
                    onAttendanceClick = { attendanceId ->
                        navigator.push(AttendanceDetailScreen(attendanceId))
                    }
                )

                // Error Card
                state.errorMessage?.let { message ->
                    ErrorCard(message = message)
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
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
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

@Composable
private fun CalendarSection(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    markedDates: Set<LocalDate>,
    currentMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit
) {
    var isCalendarExpanded by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Calendar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { isCalendarExpanded = !isCalendarExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isCalendarExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isCalendarExpanded) "Collapse calendar" else "Expand calendar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isCalendarExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    SimpleCalendarView(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        onDateSelected = onDateSelected,
                        markedDates = markedDates,
                        onMonthChanged = onMonthChanged
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleCalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    markedDates: Set<LocalDate>,
    onMonthChanged: (YearMonth) -> Unit
) {
    Column {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        val firstSunday = firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value % 7).toLong())
        val lastSaturday = lastDayOfMonth.plusDays((6 - (lastDayOfMonth.dayOfWeek.value % 7)).toLong())

        var currentDate = firstSunday
        while (currentDate <= lastSaturday) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { dayIndex ->
                    val date = currentDate.plusDays(dayIndex.toLong())
                    val isInCurrentMonth = date.month == currentMonth.month && date.year == currentMonth.year
                    val isSelected = date == selectedDate
                    val isMarked = markedDates.contains(date)
                    val isToday = date == LocalDate.now()

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(enabled = isInCurrentMonth) {
                                if (isInCurrentMonth) {
                                    onDateSelected(date)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    !isInCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                            )

                            // Attendance indicator dot
                            if (isMarked && isInCurrentMonth) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                                else -> MaterialTheme.colorScheme.tertiary
                                            },
                                            CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
            currentDate = currentDate.plusWeeks(1)
        }
    }
}

@Composable
private fun EmptyAttendanceCard() {
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
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No attendance data",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "No records found for this date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun AttendanceDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Add AttendanceDetailCard for displaying attendance details
@Composable
private fun AttendanceDetailCard(
    attendance: Attendance,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Attendance #${attendance.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Status indicator
                val isActive = attendance.endTime == null
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isActive) "Active" else "Completed",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AttendanceDetailRow(
                icon = Icons.Default.Person,
                label = "Employee ID",
                value = attendance.employeeId.toString(),
                color = MaterialTheme.colorScheme.primary
            )
            AttendanceDetailRow(
                icon = Icons.Default.AccessTime,
                label = "Start Time",
                value = attendance.createdAt,
                color = MaterialTheme.colorScheme.tertiary
            )
            attendance.endTime?.let {
                AttendanceDetailRow(
                    icon = Icons.Default.Stop,
                    label = "End Time",
                    value = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Quick work location info
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = attendance.workLocation.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Tap for details",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Add composable to show attendances for selected date
@Composable
private fun AttendanceListForSelectedDate(
    attendances: List<Attendance>,
    onAttendanceClick: (Int) -> Unit
) {
    if (attendances.isEmpty()) {
        EmptyAttendanceCard()
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            attendances.forEach { attendance ->
                AttendanceDetailCard(
                    attendance = attendance,
                    onClick = { onAttendanceClick(attendance.id) }
                )
            }
        }
    }
}
