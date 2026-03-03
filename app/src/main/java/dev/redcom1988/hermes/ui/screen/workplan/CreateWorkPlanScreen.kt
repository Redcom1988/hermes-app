package dev.redcom1988.hermes.ui.screen.workplan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.ui.components.StatusChip
import dev.redcom1988.hermes.ui.main.FormLayout
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object CreateWorkPlanScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { CreateWorkPlanScreenModel() }
        val state by screenModel.state.collectAsState()

        // Navigate back when plan is created successfully
        LaunchedEffect(state.isPlanCreated) {
            if (state.isPlanCreated) {
                navigator.pop()
            }
        }

        FormLayout(
            title = "Create Work Plan",
            onBack = { navigator.pop() }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Work Plan Form Card
                item {
                    WorkPlanFormCard(
                        selectedDate = state.selectedDate,
                        startTime = state.startTime,
                        endTime = state.endTime,
                        selectedLocation = state.selectedLocation,
                        onDateChange = screenModel::updateSelectedDate,
                        onStartTimeChange = screenModel::updateStartTime,
                        onEndTimeChange = screenModel::updateEndTime,
                        onLocationChange = screenModel::updateSelectedLocation
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

                // Create Button
                item {
                    Button(
                        onClick = { screenModel.createWorkPlan() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isLoading,
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
                                text = "Create Work Plan",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkPlanFormCard(
    selectedDate: LocalDate,
    startTime: String,
    endTime: String,
    selectedLocation: WorkLocation,
    onDateChange: (LocalDate) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onLocationChange: (WorkLocation) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

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
                text = "Work Plan Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Date Selection
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    onValueChange = { },
                    label = { Text("Plan Date") },
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                // Transparent clickable overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showDatePicker = true }
                )
            }

            // Time Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start Time
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { },
                        label = { Text("Start Time") },
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    // Transparent clickable overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showStartTimePicker = true }
                    )
                }

                // End Time
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { },
                        label = { Text("End Time") },
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    // Transparent clickable overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showEndTimePicker = true }
                    )
                }
            }

            // Work Location Selection
            Text(
                text = "Work Location",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WorkLocation.entries.forEach { location ->
                    StatusChip(
                        text = location.label,
                        isSelected = selectedLocation == location,
                        onClick = { onLocationChange(location) },
                        leadingIcon = if (location == WorkLocation.OFFICE) Icons.Default.Business else Icons.Default.Home,
                        modifier = Modifier.weight(1f).height(48.dp)
                    )
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
        )

        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            onDateChange(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false
                )
            }
        )
    }

    // Start Time Picker Dialog
    if (showStartTimePicker) {
        val currentTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"))
        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.hour,
            initialMinute = currentTime.minute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hour = timePickerState.hour.toString().padStart(2, '0')
                        val minute = timePickerState.minute.toString().padStart(2, '0')
                        onStartTimeChange("$hour:$minute")
                        showStartTimePicker = false
                    }
                ) {
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

    // End Time Picker Dialog
    if (showEndTimePicker) {
        val currentTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"))
        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.hour,
            initialMinute = currentTime.minute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hour = timePickerState.hour.toString().padStart(2, '0')
                        val minute = timePickerState.minute.toString().padStart(2, '0')
                        onEndTimeChange("$hour:$minute")
                        showEndTimePicker = false
                    }
                ) {
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
