package dev.redcom1988.hermes.data.local.client

import androidx.room.Transaction
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.data.local.client.entity.ClientDataEntity
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.data.local.service.ServiceDao
import dev.redcom1988.hermes.data.local.service.toDomain
import dev.redcom1988.hermes.data.remote.api.ClientApi
import dev.redcom1988.hermes.data.remote.model.requests.ClientApiRequestDto
import dev.redcom1988.hermes.data.remote.model.responses.ClientApiResponseDto
import dev.redcom1988.hermes.data.remote.model.responses.toDomainClients
import dev.redcom1988.hermes.domain.client.Client
import dev.redcom1988.hermes.domain.client.ClientData
import dev.redcom1988.hermes.domain.client.ClientRepository
import dev.redcom1988.hermes.domain.client.toDto
import dev.redcom1988.hermes.domain.service.Service
import dev.redcom1988.hermes.domain.service.toDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ClientRepositoryImpl(
    private val clientDao: ClientDao,
    private val serviceDao: ServiceDao,
    private val api: ClientApi
) : ClientRepository {

    suspend fun generateTempClientId(): Int {
        val minTempId = clientDao.getMinTempClientId() ?: 0
        return minTempId - 1
    }

    suspend fun getMinTempClientDataId(): Int {
        val minTempId = clientDao.getMinTempClientDataId() ?: 0
        return minTempId - 1
    }

    override suspend fun addClient(
        fullName: String,
        phoneNumber: String,
        email: String,
        address: String
    ): Int {
        val tempId = generateTempClientId()
        val clientEntity = ClientEntity(
            clientId = tempId,
            fullName = fullName,
            phoneNumber = phoneNumber,
            email = email,
            address = address,
            isSynced = false,
            isDeleted = false
        )
        clientDao.upsertClient(clientEntity)
        return tempId
    }

    override suspend fun addClientData(
        clientId: Int,
        accountType: String,
        accountCredentials: String,
        accountPassword: String
    ): Int {
        val tempId = getMinTempClientDataId()
        val entity = ClientDataEntity(
            dataId = tempId,
            clientId = clientId,
            accountType = accountType,
            accountCredentials = accountCredentials,
            accountPassword = accountPassword,
            isSynced = false,
            isDeleted = false,
            createdAt = formattedNow(),
            updatedAt = formattedNow()
        )
        clientDao.upsertClientData(entity)
        return tempId
    }

    override suspend fun update(client: Client) {
        val updated = client.copy(updatedAt = formattedNow())
        if (updated.isDeleted) softDeleteClientWithLinks(clientId = updated.id)
        clientDao.updateClient(updated.toEntity(isSynced = false))
    }

    override suspend fun update(clientData: ClientData) {
        val updated = clientData.copy(updatedAt = formattedNow())
        clientDao.updateClientData(updated.toEntity(isSynced = false))
    }

    @Transaction
    suspend fun clearClientIdFromService(clientId: Int) {
        val services = serviceDao.getServicesByClientId(clientId)
        services.forEach { service ->
            val updatedService =
                service.copy(clientId = -1, isSynced = false, updatedAt = formattedNow())
            serviceDao.updateService(updatedService)
        }
    }

    @Transaction
    override suspend fun softDeleteClientWithLinks(clientId: Int) {
        clientDao.softDeleteClientById(clientId, formattedNow())
        clientDao.softDeleteClientDataByClientId(clientId, formattedNow())
        // Only soft delete to match web behavior
        clientDao.softDeleteServicesByClientId(clientId, formattedNow())
//        if (softDeleteConnected) {
//            clearClientIdFromService(clientId)
//        } else {
//            clientDao.softDeleteServicesByClientId(clientId)
//        }
    }

    override suspend fun softDeleteClientData(clientDataId: Int) {
        clientDao.softDeleteClientDataById(clientDataId, formattedNow())
    }

    override fun getVisibleClients(): Flow<List<Client>> {
        return clientDao.getVisibleClientsFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    data class ClientWithData(
        val client: Client,
        val data: List<ClientData>,
        val services: List<Service>
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getClientWithData(): Flow<List<ClientWithData>> {
        return clientDao.getVisibleClientsFlow().flatMapLatest { clients ->
            if (clients.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    clients.map { client ->
                        combine(
                            clientDao.getClientDataByClientId(client.clientId),
                            clientDao.getServicesByClientId(client.clientId)
                        ) { data, services ->
                            ClientWithData(
                                client = client.toDomain(),
                                data = data.map { it.toDomain() },
                                services = services.map { it.toDomain() }
                            )
                        }
                    }
                ) { it.toList() }
            }
        }
    }

//    override fun getVisibleClients(): Flow<List<Client>> {
//        return clientDao.getVisibleClientsFlow()
//            .map { list -> list.map { it.toDomain() } }
//    }
//
//    override fun getClientDataFlow(): Flow<List<ClientData>> {
//        return clientDao.getClientDataFlow()
//            .map { list -> list.map { it.toDomain() } }
//    }

    override suspend fun insertClients(clients: List<Client>) {
        clientDao.insertClients(clients.map { it.toEntity() })
    }

    override suspend fun getPendingSyncClients(): List<Client> {
        return clientDao.getPendingSyncClients().map { it.toDomain() }
    }

    override suspend fun getPendingSyncClientData(): List<ClientData> {
        return clientDao.getPendingSyncClientData().map { it.toDomain() }
    }

    override suspend fun getPendingSyncServices(): List<Service> {
        return serviceDao.getPendingSyncServices().map { it.toDomain() }
    }

    override suspend fun deleteAllClients() {
        clientDao.deleteAllClients()
    }

    override suspend fun deleteAllClientData() {
        clientDao.deleteAllClientData()
    }

    override suspend fun  wipeClientData() {
        deleteAllClients()
        deleteAllClientData()
    }

    private suspend fun fetchDataFromRemote(): ClientApiResponseDto {
        val response = api.getClientData()
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch data from remote: ${response.code}")
        }

        val bodyString = response.body.string()
        return Json.decodeFromString<ClientApiResponseDto>(bodyString)
    }

//    @Transaction
//    private suspend fun upsertDataFromRemote(response: ClientApiResponseDto) {
//        val remoteClients = response.toDomainClients()
//
//        remoteClients.forEach { client ->
//            val entity = client.toEntity()
//            clientDao.upsertRemoteClientIfClean(entity)
//        }
//    }
//
//    private suspend fun pushChangesToRemote() {
//        val requestDto = ClientApiRequestDto(
//            clients = getPendingSyncClients().map { it.toDto() },
//            clientData = getPendingSyncClientData().map { it.toDto() },
//            services = getPendingSyncServices().map { it.toDto() }
//        )
//        api.pushClientChanges(requestDto)
//    }

//    override suspend fun syncClientData() {
//        pushChangesToRemote()
//        val response = fetchDataFromRemote()
//        upsertDataFromRemote(response)
//    }

}