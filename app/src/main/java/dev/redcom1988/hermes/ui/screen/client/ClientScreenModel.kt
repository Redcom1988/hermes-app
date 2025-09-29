package dev.redcom1988.hermes.ui.screen.client

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.client.ClientRepositoryImpl.ClientWithData
import dev.redcom1988.hermes.domain.client.Client
import dev.redcom1988.hermes.domain.client.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ClientUiState(
    val clientWithData: List<ClientWithData> = emptyList(),
    val filteredClients: List<ClientWithData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingClient: Client? = null,
)

class ClientScreenModel : ScreenModel {
    private val clientRepository: ClientRepository by injectLazy()

    private val _state = MutableStateFlow(ClientUiState())
    val state: StateFlow<ClientUiState> = _state

    init {
        observeClients()
    }

    private fun observeClients() {
        clientRepository.getClientWithData()
            .onEach { clients ->
                _state.value = _state.value.copy(
                    clientWithData = clients,
                    isLoading = false,
                    errorMessage = null
                )
                applyFilters()
            }
            .launchIn(screenModelScope)
    }

    private fun applyFilters() {
        val currentState = _state.value
        var filteredClients = currentState.clientWithData

        // Filter by search query (client name)
        if (currentState.searchQuery.isNotBlank()) {
            filteredClients = filteredClients.filter { clientWithData ->
                clientWithData.client.fullName.contains(currentState.searchQuery, ignoreCase = true)
            }
        }

        _state.value = _state.value.copy(filteredClients = filteredClients)
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(
            showCreateDialog = true,
            editingClient = null
        )
    }

    fun showEditDialog(client: Client) {
        _state.value = _state.value.copy(
            showEditDialog = true,
            editingClient = client
        )
    }

    fun hideDialogs() {
        _state.value = _state.value.copy(
            showCreateDialog = false,
            showEditDialog = false,
            editingClient = null
        )
    }

    fun addClient(client: Client) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                clientRepository.addClient(
                    fullName = client.fullName,
                    phoneNumber = client.phoneNumber,
                    email = client.email,
                    address = client.address
                )
                hideDialogs()
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to create client: ${e.message}"
                )
            }
        }
    }

    fun updateClient(client: Client) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                clientRepository.update(client)
                hideDialogs()
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to update client: ${e.message}"
                )
            }
        }
    }

    fun deleteClient(clientId: Int) {
        screenModelScope.launch {
            try {
                clientRepository.softDeleteClientWithLinks(clientId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to delete client: ${e.message}")
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

}