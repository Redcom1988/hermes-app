package dev.redcom1988.hermes.ui.screen.home

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
import dev.redcom1988.hermes.domain.account_data.EmployeeRepository
import dev.redcom1988.hermes.domain.account_data.UserRepository
import dev.redcom1988.hermes.domain.attendance.AttendanceRepository
import dev.redcom1988.hermes.domain.client.ClientRepository
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.service.ServiceRepository
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
import kotlin.time.Duration

data class HomeUiState(
    val isLoading: Boolean = false,
    val isCheckedIn: Boolean = false,
    val currentAttendanceTime: Duration? = null,
    val todayTotalWorkHours: Int = 0,
    val requiredWorkHours: Int = 8,
    val canCheckIn: Boolean = true,
    val errorMessage: String? = null,
    val tasks: List<Task> = emptyList(),
    // Employee details (for employees only)
    val employeeDetails: EmployeeDetails? = null,
    // Admin statistics (for admin only)
    val adminStats: AdminStats? = null,
    // Role-based assigned tasks (for PM and developers)
    val assignedTasks: List<Task> = emptyList(),
    val unfinishedTasks: List<Task> = emptyList(),
    // User role information
    val userRole: String = "",
    val isEmployee: Boolean = false,
    val isAdmin: Boolean = false
)

data class EmployeeDetails(
    val fullName: String,
    val divisionName: String,
    val phoneNumber: String? = null,
    val gender: String? = null,
    val email: String? = null
)

data class AdminStats(
    val totalServices: Int = 0,
    val totalClients: Int = 0,
    val totalUsers: Int = 0,
    val totalEmployees: Int = 0,
    val unfinishedTasksCount: Int = 0
)

class HomeScreenModel : ScreenModel {
    private val attendanceRepository: AttendanceRepository by injectLazy()
    private val taskRepository: TaskRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()
    private val divisionRepository: DivisionRepository by injectLazy()
    private val employeeRepository: EmployeeRepository by injectLazy()
    private val userRepository: UserRepository by injectLazy()
    private val clientRepository: ClientRepository by injectLazy()
    private val serviceRepository: ServiceRepository by injectLazy()

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    private val employeeId: Int? = userPreference.employeeId().get().takeIf { it != -1 }
    private val divisionType: String? = userPreference.divisionType().get()
    private val userRole: String = userPreference.userRole().get()

    init {
        initializeUserData()
        if (employeeId != null) {
            observeAttendanceStatus()
            observeAssignedTasks()
            startAutoCheckoutMonitoring()
        } else {
            // Admin user - load statistics
            loadAdminStatistics()
        }
    }

    private fun initializeUserData() {
        screenModelScope.launch {
            try {
                val isEmployee = employeeId != null
                val isAdmin = userRole.equals("admin", ignoreCase = true)

                _state.value = _state.value.copy(
                    userRole = userRole,
                    isEmployee = isEmployee,
                    isAdmin = isAdmin
                )

                if (isEmployee) {
                    loadEmployeeDetails()
                } else if (isAdmin) {
                    loadAdminStatistics()
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to load user data: ${e.message}"
                )
            }
        }
    }

    private fun loadEmployeeDetails() {
        screenModelScope.launch {
            try {
                employeeId?.let { id ->
                    val employee = employeeRepository.getEmployeeById(id)
                    employee?.let {
                        val divisionName = divisionRepository.getDivisionById(it.divisionId) ?: "Unknown Division"
                        val user = userRepository.getUserById(it.userId)

                        val employeeDetails = EmployeeDetails(
                            fullName = it.fullName,
                            divisionName = divisionName,
                            phoneNumber = it.phoneNumber,
                            gender = it.gender,
                            email = user?.email
                        )

                        _state.value = _state.value.copy(employeeDetails = employeeDetails)
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to load employee details: ${e.message}"
                )
            }
        }
    }

    private fun loadAdminStatistics() {
        screenModelScope.launch {
            try {
                val services = serviceRepository.getServicesFlow().first()
                val clients = clientRepository.getVisibleClients().first()
                val users = userRepository.getVisibleUsers().first()
                val employees = employeeRepository.getVisibleEmployees().first()
                val allTasks = taskRepository.getVisibleTasks().first()

                val unfinishedTasks = allTasks.filter { task ->
                    task.status.name != "COMPLETED" && task.status.name != "CANCELLED"
                }

                val adminStats = AdminStats(
                    totalServices = services.size,
                    totalClients = clients.size,
                    totalUsers = users.size,
                    totalEmployees = employees.size,
                    unfinishedTasksCount = unfinishedTasks.size
                )

                _state.value = _state.value.copy(adminStats = adminStats)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to load admin statistics: ${e.message}"
                )
            }
        }
    }

    private fun loadAssignedTasks() {
        screenModelScope.launch {
            try {
                employeeId?.let { id ->
                    val assignedTasks = taskRepository.getTasksForEmployee(id).first()
                    val unfinishedTasks = assignedTasks.filter { task ->
                        task.status.name != "COMPLETED" && task.status.name != "CANCELLED"
                    }

                    _state.value = _state.value.copy(
                        assignedTasks = assignedTasks,
                        unfinishedTasks = unfinishedTasks
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to load assigned tasks: ${e.message}"
                )
            }
        }
    }

    private fun observeAttendanceStatus() {
        if (employeeId == null) return

        screenModelScope.launch {
            combine(
                attendanceRepository.observeActiveAttendanceForEmployee(employeeId),
                attendanceRepository.observeCurrentAttendanceTime(employeeId),
                attendanceRepository.getVisibleAttendances()
            ) { activeAttendance, attendanceTime, allAttendances ->
                val todayAttendances = allAttendances.filter { attendance ->
                    attendance.employeeId == employeeId && runCatching {
                        attendance.createdAt.toLocalDateTime().toLocalDate() == LocalDate.now()
                    }.getOrDefault(false)
                }

                val totalHours = calculateTotalWorkHours(todayAttendances)

                // Get required hours asynchronously
                val requiredHours = try {
                    if (divisionType != null) {
                        divisionRepository.getDivisionWorkHoursByName(divisionType) ?: 8
                    } else {
                        8
                    }
                } catch (e: Exception) {
                    8 // fallback to 8 hours if there's an error
                }

                val canCheckIn = totalHours < requiredHours && activeAttendance == null

                // Check for auto check-out condition
                checkAutoCheckout(activeAttendance, requiredHours)

                _state.value.copy(
                    isCheckedIn = activeAttendance != null,
                    currentAttendanceTime = attendanceTime,
                    todayTotalWorkHours = totalHours,
                    requiredWorkHours = requiredHours,
                    canCheckIn = canCheckIn
                )
            }
                .onEach { _state.value = it }
                .launchIn(this)
        }
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
            // Use first() to get the current value from the Flow
            val currentState = attendanceRepository.observeActiveAttendanceForEmployee(employeeId!!).first()
            if (currentState != null) {
                val requiredHours = if (divisionType != null) {
                    divisionRepository.getDivisionWorkHoursByName(divisionType) ?: 8
                } else {
                    8
                }
                checkAutoCheckout(currentState, requiredHours)
            }
        } catch (e: Exception) {
            // Silently handle errors in background monitoring
        }
    }

    private fun checkAutoCheckout(activeAttendance: dev.redcom1988.hermes.domain.attendance.Attendance?, requiredHours: Int) {
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

    private fun observeAssignedTasks() {
        if (employeeId == null) return

        screenModelScope.launch {
            loadAssignedTasks()
        }

        taskRepository.getTasksForEmployee(employeeId)
            .onEach { tasks ->
                val unfinishedTasks = tasks.filter { task ->
                    task.status.name != "COMPLETED" && task.status.name != "CANCELLED"
                }
                _state.value = _state.value.copy(
                    tasks = tasks,
                    assignedTasks = tasks,
                    unfinishedTasks = unfinishedTasks
                )
            }
            .launchIn(screenModelScope)
    }

    private fun observeTasks() {
        taskRepository.getVisibleTasks()
            .onEach { tasks ->
                _state.value = _state.value.copy(tasks = tasks)
            }
            .launchIn(screenModelScope)
    }

    private fun calculateTotalWorkHours(todayAttendances: List<dev.redcom1988.hermes.domain.attendance.Attendance>): Int {
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
}