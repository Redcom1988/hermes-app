package dev.redcom1988.hermes.ui.screen.workplan

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class CreateWorkPlanUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val startTime: String = "09:00",
    val endTime: String = "17:00",
    val selectedLocation: WorkLocation = WorkLocation.OFFICE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPlanCreated: Boolean = false
)

class CreateWorkPlanScreenModel : ScreenModel {
    private val workhourPlanRepository: WorkhourPlanRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()

    private val _state = MutableStateFlow(CreateWorkPlanUiState())
    val state: StateFlow<CreateWorkPlanUiState> = _state

    private val employeeId: Int? = userPreference.employeeId().get().takeIf { it != -1 }

    fun updateSelectedDate(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
    }

    fun updateStartTime(time: String) {
        _state.value = _state.value.copy(startTime = time)
    }

    fun updateEndTime(time: String) {
        _state.value = _state.value.copy(endTime = time)
    }

    fun updateSelectedLocation(location: WorkLocation) {
        _state.value = _state.value.copy(selectedLocation = location)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun createWorkPlan() {
        if (employeeId == null) {
            _state.value = _state.value.copy(errorMessage = "Employee ID not found")
            return
        }

        val currentState = _state.value

        // Validate times
        if (!isValidTimeFormat(currentState.startTime) || !isValidTimeFormat(currentState.endTime)) {
            _state.value = _state.value.copy(errorMessage = "Invalid time format")
            return
        }

        // Validate start time is before end time
        if (!isStartTimeBeforeEndTime(currentState.startTime, currentState.endTime)) {
            _state.value = _state.value.copy(errorMessage = "Start time must be before end time")
            return
        }

        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val planDate = currentState.selectedDate.format(dateFormatter)

                workhourPlanRepository.addPlan(
                    employeeId = employeeId,
                    planDate = planDate,
                    plannedStartTime = currentState.startTime,
                    plannedEndTime = currentState.endTime,
                    workLocation = currentState.selectedLocation
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    isPlanCreated = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to create work plan: ${e.message}"
                )
            }
        }
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isStartTimeBeforeEndTime(startTime: String, endTime: String): Boolean {
        return try {
            val start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"))
            val end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"))
            start.isBefore(end)
        } catch (e: Exception) {
            false
        }
    }
}
