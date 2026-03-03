package dev.redcom1988.hermes.ui.screen.client

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.domain.client.Client
import dev.redcom1988.hermes.domain.client.ClientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EditClientScreenModel(private val clientId: Int) : ScreenModel, KoinComponent {

    private val repository: ClientRepository by inject()

    private val _state = MutableStateFlow(EditClientState())
    val state: StateFlow<EditClientState> = _state.asStateFlow()

    init {
        loadClient()
    }

    private fun loadClient() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getClientById(clientId).collectLatest { client ->
                if (client != null) {
                    _state.update {
                        it.copy(
                            client = client,
                            fullName = client.fullName,
                            phoneNumber = client.phoneNumber,
                            email = client.email,
                            address = client.address,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, client = null) }
                }
            }
        }
    }

    fun updateFullName(value: String) {
        _state.update { it.copy(fullName = value) }
    }

    fun updatePhoneNumber(value: String) {
        _state.update { it.copy(phoneNumber = value) }
    }

    fun updateEmail(value: String) {
        _state.update { it.copy(email = value) }
    }

    fun updateAddress(value: String) {
        _state.update { it.copy(address = value) }
    }

    fun updateClient() {
        val currentState = _state.value
        val client = currentState.client ?: return

        if (currentState.fullName.isBlank()) {
            _state.update { it.copy(errorMessage = "Full name is required") }
            return
        }

        if (currentState.phoneNumber.isBlank()) {
            _state.update { it.copy(errorMessage = "Phone number is required") }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val updatedClient = client.copy(
                    fullName = currentState.fullName.trim(),
                    phoneNumber = currentState.phoneNumber.trim(),
                    email = currentState.email.trim(),
                    address = currentState.address.trim()
                )
                repository.update(updatedClient)
                _state.update { it.copy(isLoading = false, isClientUpdated = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update client: ${e.message}"
                    )
                }
            }
        }
    }
}

data class EditClientState(
    val client: Client? = null,
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isClientUpdated: Boolean = false
)
