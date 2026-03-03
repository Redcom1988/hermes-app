package dev.redcom1988.hermes.ui.screen.workplan

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.account_data.EmployeeRepository
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlan
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class WorkPlanDetailUiState(
    val workPlan: WorkhourPlan? = null,
    val employee: Employee? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAdmin: Boolean = false,
    val currentEmployeeId: Int? = null
)

class WorkPlanDetailScreenModel(
    private val planId: Int
) : ScreenModel {
    private val workPlanRepository: WorkhourPlanRepository by injectLazy()
    private val employeeRepository: EmployeeRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()

    private val _state = MutableStateFlow(WorkPlanDetailUiState(isLoading = true))
    val state: StateFlow<WorkPlanDetailUiState> = _state

    private val currentEmployeeId: Int? = userPreference.employeeId().get().takeIf { it != -1 }
    private val userRole: String = userPreference.userRole().get()

    init {
        loadWorkPlan()
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val isAdmin = userRole.equals("admin", ignoreCase = true)

        _state.value = _state.value.copy(
            isAdmin = isAdmin,
            currentEmployeeId = currentEmployeeId
        )
    }

    private fun loadWorkPlan() {
        screenModelScope.launch {
            try {
                Log.d("WorkPlanDetail", "Loading work plan with ID: $planId")
                val workPlan = workPlanRepository.getWorkPlanById(planId)
                Log.d("WorkPlanDetail", "Loaded work plan: $workPlan")
                if (workPlan != null) {
                    Log.d("WorkPlanDetail", "Work plan employeeId: ${workPlan.employeeId}")
                    val employees = employeeRepository.getVisibleEmployees().firstOrNull() ?: emptyList()
                    val employee = employees.find { it.id == workPlan.employeeId }
                    Log.d("WorkPlanDetail", "Found employee: $employee")

                    _state.value = _state.value.copy(
                        workPlan = workPlan,
                        employee = employee,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    Log.d("WorkPlanDetail", "Work plan not found for ID: $planId")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Work plan not found"
                    )
                }
            } catch (e: Exception) {
                Log.e("WorkPlanDetail", "Error loading work plan", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun deleteWorkPlan(onDeleted: () -> Unit) {
        screenModelScope.launch {
            try {
                workPlanRepository.softDeletePlan(planId)
                onDeleted()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to delete work plan: ${e.message}"
                )
            }
        }
    }

    fun canDeletePlan(): Boolean {
        val workPlan = _state.value.workPlan ?: return false
        val isAdmin = _state.value.isAdmin

        // Admin can delete any plan, or user can delete their own plans
        val canDelete = isAdmin || (currentEmployeeId != null && workPlan.employeeId == currentEmployeeId)
        
        return canDelete
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
