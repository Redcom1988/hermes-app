package dev.redcom1988.hermes.ui.screen.employee

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.account_data.AccountRepository
import dev.redcom1988.hermes.domain.account_data.model.Employee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EmployeeUiState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class EmployeeScreenModel : ScreenModel {
    val accountRepository : AccountRepository by injectLazy()

    private val _state = MutableStateFlow(EmployeeUiState())
    val state: StateFlow<EmployeeUiState> = _state

    init {
        observeEmployees()
//        syncAccountData()
    }

    private fun observeEmployees() {
        screenModelScope.launch {
            accountRepository.getAccountDataFlow()
                .collect { accountData ->
                    _state.value = _state.value.copy(
                        employees = accountData.employees,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

//    private fun syncAccountData() {
//        screenModelScope.launch {
//            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
//            try {
//                accountRepository.fetchAccountDataFromRemote()
//            } catch (e: Exception) {
//                _state.value = _state.value.copy(
//                    isLoading = false,
//                    errorMessage = e.message
//                )
//            }
//        }
//    }


}