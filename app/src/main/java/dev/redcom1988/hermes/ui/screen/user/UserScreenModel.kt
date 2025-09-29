package dev.redcom1988.hermes.ui.screen.user

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.account_data.UserRepository
import dev.redcom1988.hermes.domain.account_data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class UserScreenModel : ScreenModel {
    private val userRepository: UserRepository by injectLazy()

    private val _state = MutableStateFlow(UserUiState())
    val state: StateFlow<UserUiState> = _state.asStateFlow()

    init {
        observeUsers()
    }

    private fun observeUsers() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                userRepository.getVisibleUsers()
                    .collect { users ->
                        _state.value = _state.value.copy(
                            users = users,
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

}