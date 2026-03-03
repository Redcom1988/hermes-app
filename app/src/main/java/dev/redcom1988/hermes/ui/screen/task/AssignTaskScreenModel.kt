package dev.redcom1988.hermes.ui.screen.task

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.account_data.AccountRepository
import dev.redcom1988.hermes.domain.account_data.DivisionRepository
import dev.redcom1988.hermes.domain.account_data.EmployeeRepository
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class AssignTaskUiState(
    val task: Task? = null,
    val employees: List<EmployeeWithDivision> = emptyList(),
    val selectedEmployeeIds: Set<Int> = emptySet(),
    val initiallyAssignedEmployeeIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AssignTaskScreenModel(
    private val taskId: Int
) : ScreenModel {
    private val taskRepository: TaskRepository by injectLazy()
    private val employeeRepository: EmployeeRepository by injectLazy()
    private val accountRepository: AccountRepository by injectLazy()
    private val divisionRepository: DivisionRepository by injectLazy()

    private val _state = MutableStateFlow(AssignTaskUiState())
    val state: StateFlow<AssignTaskUiState> = _state

    init {
        loadTask()
        loadAssignableEmployees()
        loadCurrentAssignments()
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

    private fun loadAssignableEmployees() {
        screenModelScope.launch {
            try {
                // Get all divisions and find both PM and DEV divisions
                val accountData = accountRepository.getAccountDataFlow().firstOrNull()
                val divisions = accountData?.divisions ?: emptyList()
                val pmDivision = divisions.find { it.name.equals(DivisionType.PM.label, ignoreCase = true) }
                val devDivision = divisions.find { it.name.equals(DivisionType.DEV.label, ignoreCase = true) }
                
                val divisionIds = listOfNotNull(pmDivision?.id, devDivision?.id)
                
                if (divisionIds.isNotEmpty()) {
                    // Load employees from both PM and DEV divisions
                    employeeRepository.getVisibleEmployees()
                        .onEach { allEmployees ->
                            val assignableEmployees = allEmployees
                                .filter { employee -> employee.divisionId in divisionIds }
                                .map { employee ->
                                    val divisionName = divisionRepository.getDivisionById(employee.divisionId) 
                                        ?: "Unknown"
                                    EmployeeWithDivision(employee, divisionName)
                                }
                            _state.value = _state.value.copy(employees = assignableEmployees)
                        }
                        .launchIn(screenModelScope)
                } else {
                    // Fallback to all employees if divisions not found
                    employeeRepository.getVisibleEmployees()
                        .onEach { allEmployees ->
                            val employeesWithDivision = allEmployees.map { employee ->
                                val divisionName = divisionRepository.getDivisionById(employee.divisionId) 
                                    ?: "Unknown"
                                EmployeeWithDivision(employee, divisionName)
                            }
                            _state.value = _state.value.copy(employees = employeesWithDivision)
                        }
                        .launchIn(screenModelScope)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to load employees: ${e.message}"
                )
            }
        }
    }

    private fun loadCurrentAssignments() {
        taskRepository.getEmployeesForTask(taskId)
            .onEach { assignedEmployees ->
                val assignedIds = assignedEmployees.map { it.id }.toSet()
                _state.value = _state.value.copy(
                    selectedEmployeeIds = assignedIds,
                    initiallyAssignedEmployeeIds = assignedIds
                )
            }
            .launchIn(screenModelScope)
    }

    fun toggleEmployeeSelection(employeeId: Int) {
        val currentSelection = _state.value.selectedEmployeeIds
        _state.value = _state.value.copy(
            selectedEmployeeIds = if (employeeId in currentSelection) {
                currentSelection - employeeId
            } else {
                currentSelection + employeeId
            }
        )
    }

    fun saveAssignments(onSuccess: () -> Unit) {
        screenModelScope.launch {
            try {
                val currentIds = _state.value.selectedEmployeeIds
                val initialIds = _state.value.initiallyAssignedEmployeeIds

                // Find employees to assign and unassign
                val toAssign = currentIds - initialIds
                val toUnassign = initialIds - currentIds

                // Assign new employees
                toAssign.forEach { employeeId ->
                    taskRepository.upsertEmployeeTaskLink(employeeId, taskId)
                }

                // Unassign removed employees
                toUnassign.forEach { employeeId ->
                    taskRepository.softDeleteEmployeeTaskLink(employeeId, taskId)
                }

                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to update task assignments: ${e.message}"
                )
            }
        }
    }
}
