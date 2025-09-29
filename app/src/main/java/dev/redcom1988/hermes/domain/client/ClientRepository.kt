package dev.redcom1988.hermes.domain.client

import dev.redcom1988.hermes.data.local.client.ClientRepositoryImpl.ClientWithData
import dev.redcom1988.hermes.domain.service.Service
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    suspend fun addClient(
        fullName: String,
        phoneNumber: String,
        email: String,
        address: String
    ): Int
    suspend fun addClientData(
        clientId: Int,
        accountType: String,
        accountCredentials: String,
        accountPassword: String
    ): Int
    suspend fun update(client: Client)
    suspend fun update(clientData: ClientData)
    fun getVisibleClients(): Flow<List<Client>>
    fun getClientWithData(): Flow<List<ClientWithData>>
    suspend fun softDeleteClientWithLinks(clientId: Int)
    suspend fun softDeleteClientData(clientDataId: Int)
    suspend fun insertClients(clients: List<Client>)
    suspend fun getPendingSyncClients(): List<Client>
    suspend fun getPendingSyncClientData(): List<ClientData>
    suspend fun getPendingSyncServices(): List<Service>
    suspend fun deleteAllClients()
    suspend fun deleteAllClientData()
    suspend fun wipeClientData()
//    suspend fun syncClientData()

}