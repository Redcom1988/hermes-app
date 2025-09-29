package dev.redcom1988.hermes.ui.screen.login

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.sync.SyncDataJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.SyncFailedException

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val rememberMe: Boolean = false
)

class LoginScreenModel : ScreenModel {
    val authRepository: AuthRepository by injectLazy()

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail)
    }

    fun onPasswordChanged(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword)
    }

    fun togglePasswordVisibility() {
        _state.value = _state.value.copy(isPasswordVisible = !_state.value.isPasswordVisible)
    }

    fun clearErrorMessage() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        screenModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)

                val result = authRepository.login(
                    email = _state.value.email,
                    password = _state.value.password
                )

                if (result.isSuccess) {
                    onSuccess()
                } else {
                    onError(result.exceptionOrNull()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An unexpected error occurred")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
