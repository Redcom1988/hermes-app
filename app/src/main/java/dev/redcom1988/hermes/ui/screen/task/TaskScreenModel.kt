package dev.redcom1988.hermes.ui.screen.task

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskRepository
import dev.redcom1988.hermes.domain.task.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userDivision: DivisionType? = null,
    val currentEmployeeId: Int? = null,
    val isAdmin: Boolean = false
)

class TaskScreenModel : ScreenModel {
    private val taskRepository: TaskRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()

    private val _state = MutableStateFlow(TaskUiState())
    val state: StateFlow<TaskUiState> = _state

    private val employeeId: Int? = userPreference.employeeId().get().takeIf { it != -1 }
    private val divisionType: String? = userPreference.divisionType().get()

    init {
        loadUserInfo()
        observeTasks()
    }

    private fun loadUserInfo() {
        val division = when (divisionType) {
            "Developer" -> DivisionType.DEV
            "Project Manager" -> DivisionType.PM
            else -> null // Admin or unknown
        }

        val isAdmin = divisionType.isNullOrBlank()
        println("TaskScreenModel: Final division = $division, isAdmin = $isAdmin")

        _state.value = _state.value.copy(
            userDivision = division,
            currentEmployeeId = employeeId,
            isAdmin = isAdmin
        )
    }

    private fun observeTasks() {
        _state.value = _state.value.copy(isLoading = true)

        val tasksFlow = when {
            // Developer: show only assigned tasks
            _state.value.userDivision == DivisionType.DEV && employeeId != null -> {
                taskRepository.getTasksForEmployee(employeeId)
            }
            // Project Manager or Admin: show all tasks
            else -> {
                taskRepository.getVisibleTasks()
            }
        }

        tasksFlow
            .onEach { tasks ->
                _state.value = _state.value.copy(
                    tasks = tasks,
                    isLoading = false
                )
            }
            .launchIn(screenModelScope)
    }

    fun updateTaskStatus(task: Task, newStatus: TaskStatus) {
        screenModelScope.launch {
            try {
                val updatedTask = task.copy(status = newStatus)
                taskRepository.update(updatedTask)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to update task status: ${e.message}"
                )
            }
        }
    }

    fun canCreateTasks(): Boolean {
        return _state.value.userDivision == DivisionType.PM || _state.value.isAdmin
    }

    fun canAssignTasks(): Boolean {
        return _state.value.userDivision == DivisionType.PM || _state.value.isAdmin
    }

    fun canDeleteTasks(): Boolean {
        return _state.value.userDivision == DivisionType.PM || _state.value.isAdmin
    }
}
