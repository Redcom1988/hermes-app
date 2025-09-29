package dev.redcom1988.hermes.ui.screen.task

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.account_data.EmployeeRepository
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef
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
    val employees: List<Employee> = emptyList(),
    val links: List<EmployeeTaskCrossRef> = emptyList(),
    val assignedEmployeeIds: List<Int> = emptyList(),
    val isLoadingAssignedEmployees: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userDivision: DivisionType? = null,
    val currentEmployeeId: Int? = null,
    val isAdmin: Boolean = false,
    val showCreateTaskDialog: Boolean = false,
    val selectedTask: Task? = null,
    val showTaskDetailDialog: Boolean = false,
    val showAssignTaskDialog: Boolean = false
)

class TaskScreenModel : ScreenModel {
    private val taskRepository: TaskRepository by injectLazy()
    private val employeeRepository: EmployeeRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()

    private val _state = MutableStateFlow(TaskUiState())
    val state: StateFlow<TaskUiState> = _state

    private val employeeId: Int? = userPreference.employeeId().get().takeIf { it != -1 }
    private val divisionType: String? = userPreference.divisionType().get()

    init {
        loadUserInfo()
        observeEmployees()
        observeTasks()
        observeTaskLinks()
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

    private fun observeEmployees() {
        employeeRepository.getVisibleEmployees()
            .onEach { employees ->
                _state.value = _state.value.copy(employees = employees)
            }
            .launchIn(screenModelScope)
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

    fun observeTaskLinks() {
        taskRepository.getVisibleLinks()
            .onEach { crossrefs ->
                _state.value = _state.value.copy(links = crossrefs)
            }
            .launchIn(screenModelScope)
    }

    fun showCreateTaskDialog() {
        _state.value = _state.value.copy(showCreateTaskDialog = true)
    }

    fun hideCreateTaskDialog() {
        _state.value = _state.value.copy(showCreateTaskDialog = false)
    }

    fun showTaskDetail(task: Task) {
        _state.value = _state.value.copy(
            selectedTask = task,
            showTaskDetailDialog = true
        )
    }

    fun hideTaskDetail() {
        _state.value = _state.value.copy(
            selectedTask = null,
            showTaskDetailDialog = false
        )
    }

    fun showAssignTaskDialog(task: Task) {
        _state.value = _state.value.copy(
            selectedTask = task,
            showAssignTaskDialog = true,
            assignedEmployeeIds = getEmployeeIdsFromLink(state.value.links, task.id)
        )
    }

    fun hideAssignTaskDialog() {
        _state.value = _state.value.copy(
            selectedTask = null,
            showAssignTaskDialog = false
        )
    }

    fun getEmployeeIdsFromLink(crossrefs: List<EmployeeTaskCrossRef>, taskId: Int): List<Int> {
        val assignedEmployeeIds = crossrefs.filter { it.taskId == taskId }.map { it.employeeId }.toSet()
        return assignedEmployeeIds.toList()
    }

    fun createTask(
        name: String,
        description: String?,
        deadline: String,
    ) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val taskId = taskRepository.addTask(
                    name = name,
                    description = description,
                    deadline = deadline,
                    parentTaskId = null
                )

                hideCreateTaskDialog()
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to create task: ${e.message}"
                )
            }
        }
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

    fun updateTaskNote(task: Task, note: String) {
        screenModelScope.launch {
            try {
                val updatedTask = task.copy(note = note)
                taskRepository.update(updatedTask)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to update task note: ${e.message}"
                )
            }
        }
    }

    fun deleteTask(task: Task) {
        screenModelScope.launch {
            try {
                taskRepository.softDeleteTaskWithLinks(task.id)
                hideTaskDetail()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to delete task: ${e.message}"
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

    fun updateTaskAssignments(task: Task, newAssignedEmployeeIds: List<Int>) {
        screenModelScope.launch {
            try {
                val currentAssignedIds = _state.value.assignedEmployeeIds.toSet()
                val newAssignedIds = newAssignedEmployeeIds.toSet()

                val toAssign = newAssignedIds - currentAssignedIds
                val toUnassign = currentAssignedIds - newAssignedIds

                toAssign.forEach { employeeId ->
                    taskRepository.upsertEmployeeTaskLink(employeeId, task.id)
                }

                toUnassign.forEach { employeeId ->
                    taskRepository.softDeleteEmployeeTaskLink(employeeId, task.id)
                }

                hideAssignTaskDialog()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to update task assignments: ${e.message}"
                )
            }
        }
    }
}
