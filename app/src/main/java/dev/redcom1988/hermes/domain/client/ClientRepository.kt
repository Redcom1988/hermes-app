package dev.redcom1988.hermes.domain.client

import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun getClientsFlow(): Flow<List<Client>>
    suspend fun syncClients()
    suspend fun getPendingSyncClients(): List<ClientEntity>
    suspend fun addClient(
        name: String,
        phoneNumber: String,
        email: String,
        address: String
    ): Int
    suspend fun updateClient(client: Client)
    suspend fun deleteClientById(clientId: Int)
    suspend fun clearLocalClients()
}