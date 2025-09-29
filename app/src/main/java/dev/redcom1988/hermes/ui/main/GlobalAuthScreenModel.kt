package dev.redcom1988.hermes.ui.main

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.account_data.enums.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GlobalAuthState(
    val isInitialized: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userId: Int = -1,
    val employeeId: Int = -1,
    val userEmail: String = "",
    val userRole: UserRole? = null,
    val divisionType: DivisionType? = null,
)

class GlobalAuthScreenModel(
    private val authRepository: AuthRepository
) : StateScreenModel<GlobalAuthState>(GlobalAuthState()) {

    private val _authState = MutableStateFlow(GlobalAuthState())
    val authState = _authState.asStateFlow()

    fun initialize() {
        if (_authState.value.isInitialized) return

        screenModelScope.launch {
            val isLoggedIn = authRepository.isLoggedIn()

            val newState = if (isLoggedIn) {
                GlobalAuthState(
                    isInitialized = true,
                    isLoggedIn = true,
                    userId = authRepository.getCurrentUserId(),
                    employeeId = authRepository.getCurrentEmployeeId(),
                    userEmail = authRepository.getCurrentUserEmail(),
                    userRole = authRepository.getCurrentUserRole(),
                    divisionType = authRepository.getCurrentDivision()
                )
            } else {
                GlobalAuthState(
                    isInitialized = true,
                )
            }
            _authState.update { newState }
            mutableState.update { newState }
        }
    }

    fun updateAfterLogin() {
        screenModelScope.launch {
            val newState = GlobalAuthState(
                isInitialized = true,
                isLoggedIn = true,
                userId = authRepository.getCurrentUserId(),
                employeeId = authRepository.getCurrentEmployeeId(),
                userEmail = authRepository.getCurrentUserEmail(),
                userRole = authRepository.getCurrentUserRole(),
                divisionType = authRepository.getCurrentDivision(),
            )
            _authState.update { newState }
            mutableState.update { newState }
        }
    }

    fun logout() {
        screenModelScope.launch {
            authRepository.logout()
            val clearedState = GlobalAuthState(isInitialized = true)
            _authState.update { clearedState }
            mutableState.update { clearedState }
        }
    }

    // Convenience methods for easy access
    fun isUser(): Boolean = state.value.userRole == UserRole.USER
    fun isAdmin(): Boolean = state.value.userRole == UserRole.ADMIN
}