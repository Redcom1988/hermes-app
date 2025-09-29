package dev.redcom1988.hermes.ui.screen.division

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.account_data.AccountRepository
import dev.redcom1988.hermes.domain.account_data.model.Division
import dev.redcom1988.hermes.domain.account_data.model.Employee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DivisionUiState(
    val divisions: List<Division> = emptyList(),
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class DivisionScreenModel : ScreenModel {
    private val accountRepository: AccountRepository by injectLazy()

    private val _state = MutableStateFlow(DivisionUiState())
    val state: StateFlow<DivisionUiState> = _state.asStateFlow()

    init {
        observeDivisions()
    }

    private fun observeDivisions() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                accountRepository.getAccountDataFlow()
                    .collect { accountData ->
                        _state.value = _state.value.copy(
                            divisions = accountData.divisions,
                            employees = accountData.employees,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun getEmployeeCountForDivision(divisionId: Int): Int {
        return _state.value.employees.count { it.divisionId == divisionId }
    }
}
