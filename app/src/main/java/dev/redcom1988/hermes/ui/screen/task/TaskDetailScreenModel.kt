package dev.redcom1988.hermes.ui.screen.task

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.account_data.DivisionRepository
import dev.redcom1988.hermes.domain.account_data.EmployeeRepository
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskRepository
import dev.redcom1988.hermes.domain.task.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val task: Task? = null,
    val assignedEmployees: List<EmployeeWithDivision> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userDivision: DivisionType? = null,
    val isAdmin: Boolean = false
)

class TaskDetailScreenModel(
    private val taskId: Int
) : ScreenModel {
    private val taskRepository: TaskRepository by injectLazy()
    private val employeeRepository: EmployeeRepository by injectLazy()
    private val divisionRepository: DivisionRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()

    private val _state = MutableStateFlow(TaskDetailUiState())
    val state: StateFlow<TaskDetailUiState> = _state

    private val divisionType: String? = userPreference.divisionType().get()

    init {
        loadUserInfo()
        loadTask()
        loadAssignedEmployees()
    }

    private fun loadUserInfo() {
        val division = when (divisionType) {
            "Developer" -> DivisionType.DEV
            "Project Manager" -> DivisionType.PM
            else -> null // Admin or unknown
        }

        val isAdmin = divisionType.isNullOrBlank()

        _state.value = _state.value.copy(
            userDivision = division,
            isAdmin = isAdmin
        )
    }

    private fun loadTask() {
        _state.value = _state.value.copy(isLoading = true)

        taskRepository.getTaskById(taskId)
            .onEach { task ->
                _state.value = _state.value.copy(
                    task = task,
                    isLoading = false
                )
            }
            .launchIn(screenModelScope)
    }

    private fun loadAssignedEmployees() {
        taskRepository.getEmployeesForTask(taskId)
            .onEach { employees ->
                // Load division names for each employee
                val employeesWithDivision = employees.map { employee ->
                    val divisionName = divisionRepository.getDivisionById(employee.divisionId) ?: "Unknown"
                    EmployeeWithDivision(employee, divisionName)
                }
                _state.value = _state.value.copy(assignedEmployees = employeesWithDivision)
            }
            .launchIn(screenModelScope)
    }

    fun updateTaskStatus(newStatus: TaskStatus) {
        screenModelScope.launch {
            try {
                _state.value.task?.let { task ->
                    val updatedTask = task.copy(status = newStatus)
                    taskRepository.update(updatedTask)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to update task status: ${e.message}"
                )
            }
        }
    }

    fun updateTaskNote(note: String) {
        screenModelScope.launch {
            try {
                _state.value.task?.let { task ->
                    val updatedTask = task.copy(note = note)
                    taskRepository.update(updatedTask)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to update task note: ${e.message}"
                )
            }
        }
    }

    fun deleteTask(onSuccess: () -> Unit) {
        screenModelScope.launch {
            try {
                taskRepository.softDeleteTaskWithLinks(taskId)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to delete task: ${e.message}"
                )
            }
        }
    }

    fun canEditTask(): Boolean {
        return _state.value.userDivision == DivisionType.PM || _state.value.isAdmin
    }

    fun canDeleteTask(): Boolean {
        return _state.value.userDivision == DivisionType.PM || _state.value.isAdmin
    }

    fun canAssignTask(): Boolean {
        return _state.value.userDivision == DivisionType.PM || _state.value.isAdmin
    }
}
