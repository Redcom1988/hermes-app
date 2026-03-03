package dev.redcom1988.hermes.ui.screen.division

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.account_data.AccountRepository
import dev.redcom1988.hermes.domain.account_data.model.Division
import dev.redcom1988.hermes.domain.account_data.model.Employee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DivisionDetailUiState(
    val division: Division? = null,
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class DivisionDetailScreenModel(
    private val divisionId: Int
) : ScreenModel {
    private val accountRepository: AccountRepository by injectLazy()

    private val _state = MutableStateFlow(DivisionDetailUiState())
    val state: StateFlow<DivisionDetailUiState> = _state

    init {
        loadDivisionDetails()
    }

    private fun loadDivisionDetails() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                accountRepository.getAccountDataFlow()
                    .collect { accountData ->
                        val division = accountData.divisions.find { it.id == divisionId }
                        val employees = accountData.employees.filter { it.divisionId == divisionId }

                        _state.value = _state.value.copy(
                            division = division,
                            employees = employees,
                            isLoading = false,
                            errorMessage = if (division == null) "Division not found" else null
                        )
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load division details"
                )
            }
        }
    }
}
