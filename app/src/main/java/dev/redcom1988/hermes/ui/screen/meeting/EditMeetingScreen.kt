package dev.redcom1988.hermes.ui.screen.meeting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.ui.main.FormLayout
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class EditMeetingScreen(val meetingId: Int) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { EditMeetingScreenModel(meetingId) }
        val state by screenModel.state.collectAsState()

        // Navigate back when meeting is updated successfully
        LaunchedEffect(state.isMeetingUpdated) {
            if (state.isMeetingUpdated) {
                navigator.pop()
            }
        }

        FormLayout(
            title = "Edit Meeting",
            onBack = { navigator.pop() }
        ) {
            if (state.isLoading && state.meeting == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.meeting == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "Meeting not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Meeting Form Card
                    item {
                        MeetingFormCard(
                            title = state.title,
                            note = state.note,
                            startDate = state.startDate,
                            startTime = state.startTime,
                            endDate = state.endDate,
                            endTime = state.endTime,
                            onTitleChange = screenModel::updateTitle,
                            onNoteChange = screenModel::updateNote,
                            onStartDateChange = screenModel::updateStartDate,
                            onStartTimeChange = screenModel::updateStartTime,
                            onEndDateChange = screenModel::updateEndDate,
                            onEndTimeChange = screenModel::updateEndTime
                        )
                    }

                    // Error message
                    if (state.errorMessage != null) {
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
                                    text = state.errorMessage!!,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Save Button
                    item {
                        Button(
                            onClick = { screenModel.updateMeeting() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = state.title.isNotBlank() && !state.isLoading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Save Changes",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeetingFormCard(
    title: String,
    note: String,
    startDate: String,
    startTime: String,
    endDate: String,
    endTime: String,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Meeting Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Meeting Title *") },
                leadingIcon = {
                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(20.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Meeting Note (Optional)") },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.Note, contentDescription = null, modifier = Modifier.size(20.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            // Start Date & Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start Date
                OutlinedTextField(
                    value = startDate,
                    onValueChange = {},
                    label = { Text("Start Date *") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                        showStartDatePicker = true
                                    }
                                }
                            }
                        }
                )

                // Start Time
                OutlinedTextField(
                    value = startTime,
                    onValueChange = {},
                    label = { Text("Start Time *") },
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                        showStartTimePicker = true
                                    }
                                }
                            }
                        }
                )
            }

            // End Date & Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // End Date
                OutlinedTextField(
                    value = endDate,
                    onValueChange = {},
                    label = { Text("End Date *") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                        showEndDatePicker = true
                                    }
                                }
                            }
                        }
                )

                // End Time
                OutlinedTextField(
                    value = endTime,
                    onValueChange = {},
                    label = { Text("End Time *") },
                    leadingIcon = {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                        showEndTimePicker = true
                                    }
                                }
                            }
                        }
                )
            }
        }
    }

    // Date Pickers
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = try {
                LocalDate.parse(startDate.ifBlank { LocalDate.now().toString() }, dateFormatter)
                    .toEpochDay() * 86400000
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / 86400000)
                        onStartDateChange(date.format(dateFormatter))
                    }
                    showStartDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = try {
                LocalDate.parse(endDate.ifBlank { LocalDate.now().toString() }, dateFormatter)
                    .toEpochDay() * 86400000
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / 86400000)
                        onEndDateChange(date.format(dateFormatter))
                    }
                    showEndDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Pickers
    if (showStartTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = try {
                LocalTime.parse(startTime.ifBlank { "09:00" }).hour
            } catch (e: Exception) {
                9
            },
            initialMinute = try {
                LocalTime.parse(startTime.ifBlank { "09:00" }).minute
            } catch (e: Exception) {
                0
            }
        )
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    onStartTimeChange(time.format(DateTimeFormatter.ofPattern("HH:mm")))
                    showStartTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    if (showEndTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = try {
                LocalTime.parse(endTime.ifBlank { "10:00" }).hour
            } catch (e: Exception) {
                10
            },
            initialMinute = try {
                LocalTime.parse(endTime.ifBlank { "10:00" }).minute
            } catch (e: Exception) {
                0
            }
        )
        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    onEndTimeChange(time.format(DateTimeFormatter.ofPattern("HH:mm")))
                    showEndTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}
