package dev.redcom1988.hermes.ui.screen.employee

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.account_data.AccountRepository
import dev.redcom1988.hermes.domain.account_data.model.Division
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.account_data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EmployeeDetailUiState(
    val employee: Employee? = null,
    val user: User? = null,
    val division: Division? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class EmployeeDetailScreenModel(
    private val employeeId: Int
) : ScreenModel {
    private val accountRepository: AccountRepository by injectLazy()

    private val _state = MutableStateFlow(EmployeeDetailUiState())
    val state: StateFlow<EmployeeDetailUiState> = _state

    init {
        loadEmployeeDetails()
    }

    private fun loadEmployeeDetails() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                accountRepository.getAccountDataFlow()
                    .collect { accountData ->
                        val employee = accountData.employees.find { it.id == employeeId }
                        val user = employee?.let { emp ->
                            accountData.users.find { it.id == emp.userId }
                        }
                        val division = employee?.let { emp ->
                            accountData.divisions.find { it.id == emp.divisionId }
                        }

                        _state.value = _state.value.copy(
                            employee = employee,
                            user = user,
                            division = division,
                            isLoading = false,
                            errorMessage = if (employee == null) "Employee not found" else null
                        )
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load employee details"
                )
            }
        }
    }
}
