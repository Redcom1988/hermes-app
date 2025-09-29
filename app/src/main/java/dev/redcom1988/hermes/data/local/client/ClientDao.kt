package dev.redcom1988.hermes.data.local.client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.client.entity.ClientDataEntity
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query("SELECT * FROM clients WHERE isSynced = 0")
    suspend fun getPendingSyncClients(): List<ClientEntity>

    @Query("SELECT * FROM client_data WHERE isSynced = 0")
    suspend fun getPendingSyncClientData(): List<ClientDataEntity>

    @Query("SELECT * FROM clients")
    suspend fun getAllClients(): List<ClientEntity>

    @Query("SELECT * FROM clients WHERE isDeleted = 0")
    fun getVisibleClientsFlow(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM client_data WHERE clientId = :clientId AND isDeleted = 0")
    fun getClientDataByClientId(clientId: Int): Flow<List<ClientDataEntity>>

    @Query("SELECT * FROM services where clientId = :clientId AND isDeleted = 0")
    fun getServicesByClientId(clientId: Int): Flow<List<ServiceEntity>>

    @Query("SELECT MIN(clientId) FROM clients WHERE clientId < 0")
    suspend fun getMinTempClientId(): Int?

    @Query("SELECT MIN(dataId) FROM client_data WHERE dataId < 0")
    suspend fun getMinTempClientDataId(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientData(clientData: ClientDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientDataList(clientDataList: List<ClientDataEntity>)

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Update
    suspend fun updateClientData(clientData: ClientDataEntity)

    @Query("UPDATE clients SET isSynced = 0, isDeleted = 1, updatedAt = :updatedAt WHERE clientId = :id")
    suspend fun softDeleteClientById(id: Int, updatedAt: String)

    @Query("UPDATE client_data SET isSynced = 0, isDeleted = 1, updatedAt = :updatedAt WHERE dataId = :dataId")
    suspend fun softDeleteClientDataById(dataId: Int, updatedAt: String)

    @Query("UPDATE client_data SET isSynced = 0, isDeleted = 1, updatedAt = :updatedAt WHERE clientId = :clientId")
    suspend fun softDeleteClientDataByClientId(clientId: Int, updatedAt: String)

    @Query("UPDATE services SET isSynced = 0, isDeleted = 1, updatedAt = :updatedAt WHERE clientId = :clientId")
    suspend fun softDeleteServicesByClientId(clientId: Int, updatedAt: String)

    @Query ("UPDATE service_type_data SET isSynced = 0, isDeleted = 1, updatedAt = :updatedAt WHERE serviceId IN (SELECT serviceId FROM services WHERE clientId = :clientId)")
    suspend fun softDeleteServiceTypeDataByClientId(clientId: Int, updatedAt: String)

    @Query("SELECT * FROM clients WHERE clientId = :clientId")
    suspend fun getClientById(clientId: Int): ClientEntity?

    @Query("SELECT * FROM client_data WHERE dataId = :dataId")
    suspend fun getClientDataById(dataId: Int): ClientDataEntity?

    // Alt push response handling for updated IDs
//    @Query("UPDATE clients SET clientId = :newId, isSynced = 0, updatedAt = :updatedAt WHERE clientId = :oldId")
//    suspend fun updateClientId(oldId: Int, newId: Int, updatedAt: String)
//
//    @Query("UPDATE client_data SET dataId = :newDataId, isSynced = 0, updatedAt = :updatedAt WHERE dataId = :oldDataId")
//    suspend fun updateClientDataId(oldDataId: Int, newDataId: Int, updatedAt: String)
//
//    @Query("UPDATE client_data SET clientId = :newId, isSynced = 0, updatedAt = :updatedAt WHERE clientId = :oldId")
//    suspend fun updateClientIdInClientData(oldId: Int, newId: Int, updatedAt: String)

    @Transaction
    suspend fun upsertClient(client: ClientEntity) {
        val existingClient = getClientById(client.clientId)
        if (existingClient != null) {
            updateClient(client.copy(isSynced = false))
        } else {
            insertClient(client)
        }
    }

    @Transaction
    suspend fun upsertRemoteClient(client: ClientEntity) {
        val existingClient = getClientById(client.clientId)
        if (existingClient != null) {
            updateClient(client)
        } else {
            insertClient(client)
        }
    }

    @Transaction
    suspend fun upsertClients(clients: List<ClientEntity>) {
        clients.forEach { client ->
            upsertRemoteClient(client)
        }
    }

    @Transaction
    suspend fun upsertClientData(clientData: ClientDataEntity) {
        val existingData = getClientDataById(clientData.dataId)
        if (existingData != null) {
            updateClientData(clientData.copy(isSynced = false))
        } else {
            insertClientData(clientData)
        }
    }

    @Transaction
    suspend fun upsertRemoteClientData(clientData: ClientDataEntity) {
        val existingData = getClientDataById(clientData.dataId)
        if (existingData != null) {
            updateClientData(clientData)
        } else {
            insertClientData(clientData)
        }
    }

    @Transaction
    suspend fun upsertClientDataList(clientDataList: List<ClientDataEntity>) {
        clientDataList.forEach { clientData ->
            upsertRemoteClientData(clientData)
        }
    }

    @Query("DELETE FROM clients")
    suspend fun deleteAllClients()

    @Query("DELETE FROM client_data")
    suspend fun deleteAllClientData()

}