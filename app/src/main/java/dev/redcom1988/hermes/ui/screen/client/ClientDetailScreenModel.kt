package dev.redcom1988.hermes.ui.screen.client

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.client.ClientRepositoryImpl.ClientWithData
import dev.redcom1988.hermes.domain.client.Client
import dev.redcom1988.hermes.domain.client.ClientData
import dev.redcom1988.hermes.domain.client.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class ClientDetailUiState(
    val client: ClientWithData? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showEditClientDialog: Boolean = false,
    val showAddDataDialog: Boolean = false,
    val showEditDataDialog: Boolean = false,
    val editingClientData: ClientData? = null
)

class ClientDetailScreenModel(
    private val clientId: Int
) : ScreenModel {
    private val clientRepository: ClientRepository by injectLazy()

    private val _state = MutableStateFlow(ClientDetailUiState(isLoading = true))
    val state: StateFlow<ClientDetailUiState> = _state

    init {
        observeClient()
    }

    private fun observeClient() {
        screenModelScope.launch {
            try {
                clientRepository.getClientWithData()
                    .map { list -> list.firstOrNull { it.client.id == clientId } }
                    .collect { client ->
                        _state.value = _state.value.copy(
                            client = client,
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

    fun openEditClientDialog() {
        _state.value = _state.value.copy(showEditClientDialog = true)
    }

    fun hideEditClientDialog() {
        _state.value = _state.value.copy(showEditClientDialog = false)
    }

    fun openAddDataDialog() {
        _state.value = _state.value.copy(showAddDataDialog = true)

    }

    fun hideAddDataDialog() {
        _state.value = _state.value.copy(showAddDataDialog = false)

    }

    fun openEditDataDialog(clientData: ClientData) {
        _state.value = _state.value.copy(
            editingClientData = clientData,
            showEditDataDialog = true
        )
    }

    fun hideEditDataDialog() {
        _state.value = _state.value.copy(
            editingClientData = null,
            showEditDataDialog = false
        )
    }

    fun updateClient(client: Client) {
        screenModelScope.launch {
            try {
                clientRepository.update(client)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to update client: ${e.message}")
            }
        }
    }

    fun addClientData(accountType: String, accountCredentials: String, accountPassword: String) {
        screenModelScope.launch {
            try {
                clientRepository.addClientData(
                    clientId = clientId,
                    accountType = accountType,
                    accountCredentials = accountCredentials,
                    accountPassword = accountPassword
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to add client data: ${e.message}")
            }
        }
    }

    fun updateClientData(clientData: ClientData) {
        screenModelScope.launch {
            try {
                clientRepository.update(clientData)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to update client data: ${e.message}")
            }
        }
    }

    fun deleteClientData(clientData: ClientData) {
        screenModelScope.launch {
            try {
                clientRepository.softDeleteClientData(clientData.id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to delete client data: ${e.message}")
            }
        }
    }
}