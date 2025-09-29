package dev.redcom1988.hermes.ui.screen.workplan

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.account_data.EmployeeRepository
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlan
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class WorkplanUiState(
    val plans: List<WorkhourPlan> = emptyList(),
    val employees: List<Employee> = emptyList(),
    val filteredPlans: List<WorkhourPlan> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    val isDateFilterEnabled: Boolean = false,
    val showOnlyMyPlans: Boolean = false,
    val showCreateDialog: Boolean = false,
    val userDivision: DivisionType? = null,
    val currentEmployeeId: Int? = null,
    val isAdmin: Boolean = false
)

class WorkplanScreenModel : ScreenModel {
    private val workplanRepository: WorkhourPlanRepository by injectLazy()
    private val employeeRepository: EmployeeRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()

    private val _state = MutableStateFlow(WorkplanUiState())
    val state: StateFlow<WorkplanUiState> = _state

    private val employeeId: Int? = userPreference.employeeId().get().takeIf { it != -1 }
    private val divisionType: String? = userPreference.divisionType().get()

    init {
        loadUserInfo()
        observePlans()
        observeEmployees()
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
            currentEmployeeId = employeeId,
            isAdmin = isAdmin
        )
    }

    private fun observePlans() {
        workplanRepository.getVisiblePlans()
            .onEach { plans ->
                _state.value = _state.value.copy(plans = plans)
                applyFilters()
            }
            .launchIn(screenModelScope)
    }

    private fun observeEmployees() {
        employeeRepository.getVisibleEmployees()
            .onEach { employees ->
                _state.value = _state.value.copy(employees = employees)
                applyFilters()
            }
            .launchIn(screenModelScope)
    }

    private fun applyFilters() {
        val currentState = _state.value
        var filteredPlans = currentState.plans

        // Filter by search query (employee name)
        if (currentState.searchQuery.isNotBlank()) {
            val employeeMap = currentState.employees.associateBy { it.id }
            filteredPlans = filteredPlans.filter { plan ->
                val employee = employeeMap[plan.employeeId]
                employee?.fullName?.contains(currentState.searchQuery, ignoreCase = true) == true
            }
        }

        // Filter by selected date if date filter is enabled
        if (currentState.isDateFilterEnabled) {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val selectedDateString = currentState.selectedDate.format(dateFormatter)
            filteredPlans = filteredPlans.filter { plan ->
                plan.planDate == selectedDateString
            }
        }

        // Filter to show only user's own plans if enabled
        if (currentState.showOnlyMyPlans) {
            val currentEmployeeId = currentState.currentEmployeeId
            filteredPlans = filteredPlans.filter { plan ->
                plan.employeeId == currentEmployeeId
            }
        }

        _state.value = _state.value.copy(filteredPlans = filteredPlans)
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun setSelectedDate(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
        applyFilters()
    }

    fun setCurrentMonth(month: YearMonth) {
        _state.value = _state.value.copy(currentMonth = month)
    }

    fun toggleDateFilter() {
        _state.value = _state.value.copy(isDateFilterEnabled = !_state.value.isDateFilterEnabled)
        applyFilters()
    }

    fun toggleShowOnlyMyPlans() {
        _state.value = _state.value.copy(showOnlyMyPlans = !_state.value.showOnlyMyPlans)
        applyFilters()
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = false)
    }

    fun canCreatePlans(): Boolean {
        // Admin can't create plans (no employee ID), but everyone else can
        return !_state.value.isAdmin && _state.value.currentEmployeeId != null
    }

    fun createPlan(
        planDate: String,
        plannedStartTime: String,
        plannedEndTime: String,
        workLocation: WorkLocation
    ) {
        val currentEmployeeId = _state.value.currentEmployeeId
        if (currentEmployeeId == null) {
            _state.value = _state.value.copy(errorMessage = "Cannot create plan: No employee ID found")
            return
        }

        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                workplanRepository.addPlan(
                    employeeId = currentEmployeeId,
                    planDate = planDate,
                    plannedStartTime = plannedStartTime,
                    plannedEndTime = plannedEndTime,
                    workLocation = workLocation
                )
                hideCreateDialog()
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to create plan: ${e.message}"
                )
            }
        }
    }

    fun deletePlan(planId: Int) {
        screenModelScope.launch {
            try {
                workplanRepository.softDeletePlan(planId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to delete plan: ${e.message}")
            }
        }
    }

    fun getEmployeeForPlan(plan: WorkhourPlan): Employee? {
        return _state.value.employees.find { it.id == plan.employeeId }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    val hasEmployeeId: Boolean
        get() = _state.value.currentEmployeeId != null

    fun canDeletePlan(plan: WorkhourPlan): Boolean {
        val currentEmployeeId = _state.value.currentEmployeeId
        val isAdmin = _state.value.isAdmin

        // Admin can delete any plan, or user can delete their own plans
        return isAdmin || (currentEmployeeId != null && plan.employeeId == currentEmployeeId)
    }
}