package dev.redcom1988.hermes.ui.screen.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.domain.auth.SyncRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SettingsScreenModel : ScreenModel {
    private val authRepository: AuthRepository by injectLazy()
    private val syncRepository: SyncRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = authRepository.logout()

                if (result.isSuccess) {
                    _state.value = _state.value.copy(isLoading = false)
                    onSuccess()
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Logout failed"
                    )
                    onError("Logout failed")
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "An error occurred during logout"
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)
            }
        }
    }

    fun forcedClearSync() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                syncRepository.performSync(
                    lastSyncTime = userPreference.lastSyncTime().get(),
                    forceClearDataOverride = true
                )
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                val errorMsg = e.message ?: "An error occurred during forced clear sync"
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
