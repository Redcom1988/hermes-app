package dev.redcom1988.hermes.ui.screen.attendance

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.core.util.extension.getCurrentLocation
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.core.util.extension.isInOfficeLocation
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.account_data.DivisionRepository
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.account_data.enums.UserRole
import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef
import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.attendance.AttendanceRepository
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.time.Duration

data class AttendanceUiState(
    val attendance: List<Attendance> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val attendanceTasks: List<AttendanceTaskCrossRef> = emptyList(),
    val isLoading: Boolean = false,
    val isCheckedIn: Boolean = false,
    val currentAttendanceTime: Duration? = null,
    val errorMessage: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    val filteredAttendances: List<Attendance> = emptyList(),
    val todayTotalWorkHours: Int = 0,
    val requiredWorkHours: Int = 8,
    val canCheckIn: Boolean = true
)

class AttendanceScreenModel : ScreenModel {
    private val taskRepository: TaskRepository by injectLazy()
    private val attendanceRepository: AttendanceRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()
    private val divisionRepository: DivisionRepository by injectLazy()

    private val _state = MutableStateFlow(AttendanceUiState())
    val state: StateFlow<AttendanceUiState> = _state

    private val employeeId: Int? = userPreference.employeeId().get().takeIf { it != -1 }
    private val divisionType: String? = userPreference.divisionType().get()

    init {
        observeAttendances(employeeId)
        observeCheckInStatus(employeeId)
        calculateTodayWorkHours()
        updateRequiredWorkHours()
        startAutoCheckoutMonitoring()
    }

    private fun observeCheckInStatus(employeeId: Int?) {
        if (employeeId == null) return

        combine(
            attendanceRepository.observeActiveAttendanceForEmployee(employeeId),
            attendanceRepository.observeCurrentAttendanceTime(employeeId)
        ) { activeAttendance, attendanceTime ->
            _state.value.copy(
                isCheckedIn = activeAttendance != null,
                currentAttendanceTime = attendanceTime
            )
        }
            .onEach { _state.value = it }
            .launchIn(screenModelScope)
    }

    fun checkIn(context: Context) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                if (_state.value.isCheckedIn) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Already checked in"
                    )
                    return@launch
                }

                val currentEmployeeId = employeeId ?: run {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Employee ID not found"
                    )
                    return@launch
                }

                val location = getCurrentLocation(context)
                if (location == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Location permission required. Please enable location services and grant location permission in app settings."
                    )
                    return@launch
                }

                attendanceRepository.addAttendance(
                    employeeId = currentEmployeeId,
                    startTime = LocalDateTime.now().formatToString(),
                    longitude = location.second,
                    latitude = location.first,
                    imagePath = null,
                    workLocation = if (isInOfficeLocation(context)) {
                        WorkLocation.OFFICE
                    } else {
                        WorkLocation.ANYWHERE
                    }
                )

                _state.value = _state.value.copy(isLoading = false, errorMessage = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to check in: ${e.message}"
                )
            }
        }
    }

    fun checkOut(selectedTasks: List<Task> = emptyList()) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                attendanceRepository.finishAttendance(
                    employeeId = employeeId ?: return@launch,
                    endTime = LocalDateTime.now().formatToString(),
                    taskIds = selectedTasks.map { it.id }
                )

                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to check out: ${e.message}"
                )
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
        updateFilteredAttendancesForDate(date)
    }

    fun setCurrentMonth(month: YearMonth) {
        _state.value = _state.value.copy(currentMonth = month)
    }

    private fun updateFilteredAttendancesForDate(date: LocalDate) {
        val filtered = _state.value.attendance.filter {
            runCatching {
                it.createdAt.toLocalDateTime().toLocalDate() == date
            }.getOrDefault(false)
        }
        _state.value = _state.value.copy(filteredAttendances = filtered)
    }

    private fun calculateTodayWorkHours() {
        screenModelScope.launch {
            try {
                val totalHours = getTodayTotalWorkHours()
                val currentRequiredHours = _state.value.requiredWorkHours // Use the value from state

                // Check if user can still check in (hasn't met required hours and not currently checked in)
                val canCheckIn = totalHours < currentRequiredHours && !_state.value.isCheckedIn

                _state.value = _state.value.copy(
                    todayTotalWorkHours = totalHours,
                    canCheckIn = canCheckIn
                )
            } catch (e: Exception) {
                // Handle error silently, keep default values
            }
        }
    }

    private fun updateRequiredWorkHours() {
        screenModelScope.launch {
            try {
                val requiredHours = if (divisionType != null) {
                    divisionRepository.getDivisionWorkHoursByName(divisionType) ?: 8
                } else {
                    8
                }
                _state.value = _state.value.copy(requiredWorkHours = requiredHours)

                // Recalculate work hours after updating required hours
                calculateTodayWorkHours()
            } catch (e: Exception) {
                // Keep default value of 8
            }
        }
    }

    fun observeAttendances(employeeId: Int? = null) {
        combine(
            attendanceRepository.getVisibleAttendances(),
            taskRepository.getVisibleTasks(),
            attendanceRepository.getVisibleAttendanceTask()
        ) { attendances: List<Attendance>, tasks: List<Task>, attendanceTasks: List<AttendanceTaskCrossRef> ->
            val filteredAttendances = employeeId?.let { id ->
                attendances.filter { it.employeeId == id }
            } ?: attendances

            val filteredAttendanceIds = filteredAttendances.map { it.id }
            val filteredAttendanceTasks = attendanceTasks.filter { it.attendanceId in filteredAttendanceIds }
            val linkedTaskIds = filteredAttendanceTasks.map { it.taskId }.toSet()
            val filteredTasks = tasks.filter { it.id in linkedTaskIds }

            val selectedDate = _state.value.selectedDate
            val filteredForDate = filteredAttendances.filter {
                runCatching {
                    it.createdAt.toLocalDateTime().toLocalDate() == selectedDate
                }.getOrDefault(false)
            }

            val newState = _state.value.copy(
                attendance = filteredAttendances,
                tasks = filteredTasks,
                attendanceTasks = filteredAttendanceTasks,
                isLoading = false,
                errorMessage = null,
                filteredAttendances = filteredForDate
            )

            // Recalculate work hours when attendance data changes
            _state.value = newState
            calculateTodayWorkHours()

            newState
        }
            .onEach { /* State already updated above */ }
            .launchIn(screenModelScope)
    }

    private fun startAutoCheckoutMonitoring() {
        if (employeeId == null) return

        screenModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60_000) // Check every minute
                checkForAutoCheckout()
            }
        }
    }

    private suspend fun checkForAutoCheckout() {
        try {
            val currentState = attendanceRepository.observeActiveAttendanceForEmployee(employeeId!!).first()
            if (currentState != null) {
                val requiredHours = _state.value.requiredWorkHours
                checkAutoCheckout(currentState, requiredHours)
            }
        } catch (e: Exception) {
            // Silently handle errors in background monitoring
        }
    }

    private fun checkAutoCheckout(activeAttendance: Attendance?, requiredHours: Int) {
        if (activeAttendance == null) return

        screenModelScope.launch {
            try {
                val startTime = activeAttendance.startTime.toLocalDateTime()
                val now = LocalDateTime.now()
                val currentDuration = java.time.Duration.between(startTime, now)
                val currentHours = currentDuration.toHours()

                // Auto check-out if worked more than required hours + 1 hour
                val autoCheckoutThreshold = requiredHours + 1

                // Also check if it's past midnight (different day)
                val isDifferentDay = startTime.toLocalDate() != now.toLocalDate()

                if (currentHours >= autoCheckoutThreshold || isDifferentDay) {
                    // Auto check-out with no tasks
                    attendanceRepository.finishAttendance(
                        employeeId = employeeId!!,
                        endTime = now.formatToString(),
                        taskIds = emptyList()
                    )

                    _state.value = _state.value.copy(
                        errorMessage = if (isDifferentDay)
                            "Auto checked-out: Cannot span multiple days"
                        else
                            "Auto checked-out: Exceeded work hours by 1+ hour"
                    )
                }
            } catch (e: Exception) {
                // Handle error silently for auto-checkout
            }
        }
    }

    private fun getTodayTotalWorkHours(): Int {
        val today = LocalDate.now()
        val todayAttendances = _state.value.attendance.filter { attendance ->
            attendance.employeeId == employeeId && runCatching {
                attendance.createdAt.toLocalDateTime().toLocalDate() == today
            }.getOrDefault(false)
        }

        val totalHours = todayAttendances.sumOf { attendance ->
            if (attendance.endTime != null) {
                try {
                    val start = attendance.startTime.toLocalDateTime()
                    val end = attendance.endTime.toLocalDateTime()
                    val duration = java.time.Duration.between(start, end)
                    duration.toHours().toDouble()
                } catch (e: Exception) {
                    0.0
                }
            } else {
                // Active attendance - calculate current duration
                try {
                    val start = attendance.startTime.toLocalDateTime()
                    val now = LocalDateTime.now()
                    val duration = java.time.Duration.between(start, now)
                    duration.toHours().toDouble()
                } catch (e: Exception) {
                    0.0
                }
            }
        }

        return totalHours.toInt()
    }
}
