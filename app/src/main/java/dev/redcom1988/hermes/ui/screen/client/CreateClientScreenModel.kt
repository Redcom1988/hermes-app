package dev.redcom1988.hermes.ui.screen.client

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.client.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreateClientUiState(
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isClientCreated: Boolean = false
)

class CreateClientScreenModel : ScreenModel {
    private val clientRepository: ClientRepository by injectLazy()

    private val _state = MutableStateFlow(CreateClientUiState())
    val state: StateFlow<CreateClientUiState> = _state

    fun updateFullName(name: String) {
        _state.value = _state.value.copy(fullName = name)
    }

    fun updatePhoneNumber(phone: String) {
        _state.value = _state.value.copy(phoneNumber = phone)
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun updateAddress(address: String) {
        _state.value = _state.value.copy(address = address)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun createClient() {
        val currentState = _state.value

        if (currentState.fullName.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Full name is required")
            return
        }

        if (currentState.phoneNumber.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Phone number is required")
            return
        }

        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                clientRepository.addClient(
                    fullName = currentState.fullName,
                    phoneNumber = currentState.phoneNumber,
                    email = currentState.email,
                    address = currentState.address
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    isClientCreated = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to create client: ${e.message}"
                )
            }
        }
    }
}
