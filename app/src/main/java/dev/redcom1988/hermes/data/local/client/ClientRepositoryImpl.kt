package dev.redcom1988.hermes.data.local.client

import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.data.remote.api.ClientApi
import dev.redcom1988.hermes.data.remote.model.ClientDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.client.Client
import dev.redcom1988.hermes.domain.client.toDto
import dev.redcom1988.hermes.domain.client.ClientRepository
import dev.redcom1988.hermes.domain.common.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class ClientRepositoryImpl(
    private val clientDao: ClientDao,
    private val api: ClientApi
) : ClientRepository {

    override fun getClientsFlow(): Flow<List<Client>> {
        return clientDao.getVisibleClientsFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    suspend fun syncClientsFromServer(serverClients: List<Client>) {
        val localClients = clientDao.getAllClients()
            .associateBy { it.clientId }

        val mergedClients = serverClients.map { server ->
            val serverEntity = server.toEntity()
            val local = localClients[server.id]

            when {
                // No local version → insert
                local == null -> serverEntity

                // Local was modified more recently → keep local
                local.syncStatus != SyncStatus.UNCHANGED && local.updatedAt > serverEntity.updatedAt -> local

                // Server is newer → overwrite
                else -> serverEntity.copy(syncStatus = SyncStatus.UNCHANGED)
            }
        }
        clientDao.insertClients(mergedClients)
    }

    suspend fun generateTempClientId(): Int {
        val minId = clientDao.getMinTempClientId() ?: 0
        return if (minId >= 0) -1 else minId - 1
    }

    override suspend fun addClient(
        name: String,
        phoneNumber: String,
        email: String,
        address: String
    ): Int {
        val tempId = generateTempClientId()
        val entity = ClientEntity(
            clientId = tempId,
            fullName = name,
            phoneNumber = phoneNumber,
            email = email,
            address = address,
            syncStatus = SyncStatus.CREATED
        )
        clientDao.insertClient(entity)
        return tempId
    }

    override suspend fun updateClient(client: Client) {
        val entity = client.toEntity().copy(
            syncStatus = SyncStatus.UPDATED,
            updatedAt = LocalDateTime.now().formatToString())
        clientDao.updateClient(entity)
    }

    override suspend fun deleteClientById(clientId: Int) {
        clientDao.softDeleteClientById(clientId, SyncStatus.DELETED)
        clientDao.softDeleteServicesByClientId(clientId, SyncStatus.DELETED)
    }

    override suspend fun getPendingSyncClients(): List<ClientEntity> {
        return clientDao.getPendingSyncClients()
    }

    suspend fun pushPendingClientsToServer() {
        val pendingClients = getPendingSyncClients()

        for (client in pendingClients) {
            val dto = client.toDomain().toDto()

            when (client.syncStatus) {
                SyncStatus.CREATED -> {
                    val result = api.createClient(dto)
                    if (result.isSuccessful) {
                        val serverClient = result.parseAs<ClientDto>().toDomain()
                        clientDao.insertClient(serverClient.toEntity()
                            .copy(syncStatus = SyncStatus.UNCHANGED))
                    }
                }

                SyncStatus.UPDATED -> {
                    val result = api.updateClient(client.clientId, client.toDomain().toDto())
                    if (result.isSuccessful) {
                        clientDao.updateClient(client
                            .copy(syncStatus = SyncStatus.UNCHANGED))
                    }
                }

                SyncStatus.DELETED -> {
                    val deletedDto = dto.copy(isDeleted = true)
                    val result = api.updateClient(client.clientId, deletedDto)
                    if (result.isSuccessful) {
                        clientDao.updateClient(client
                            .copy(syncStatus = SyncStatus.UNCHANGED))
                    }
                }

                else -> Unit
            }
        }
    }

    override suspend fun syncClients() {
        pushPendingClientsToServer()

        val response = api.getClients()
        if (response.isSuccessful) {
            val clientsFromServer = response
                .parseAs<List<ClientDto>>()
                .map { it.toDomain() }
            syncClientsFromServer(serverClients = clientsFromServer)
        }
    }

    override suspend fun clearLocalClients() {
        clientDao.deleteAllClients()
    }

}