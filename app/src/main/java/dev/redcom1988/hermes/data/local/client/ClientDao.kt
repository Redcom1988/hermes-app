package dev.redcom1988.hermes.data.local.client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.domain.common.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query ("SELECT * FROM clients")
    suspend fun getAllClients(): List<ClientEntity>

    @Query("SELECT * FROM clients WHERE syncStatus != 'DELETED' AND isDeleted = 0")
    fun getVisibleClientsFlow(): Flow<List<ClientEntity>>

    @Query("SELECT MIN(clientId) FROM clients WHERE clientId < 0")
    suspend fun getMinTempClientId(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Query("UPDATE clients SET syncStatus = :status WHERE clientId = :id")
    suspend fun softDeleteClientById(id: Int, status: SyncStatus = SyncStatus.DELETED)

    @Query("UPDATE services SET syncStatus = :status WHERE clientId = :clientId")
    suspend fun softDeleteServicesByClientId(clientId: Int, status: SyncStatus = SyncStatus.DELETED)

    @Query("SELECT * FROM clients WHERE syncStatus IN ('CREATED', 'UPDATED', 'DELETED')")
    suspend fun getPendingSyncClients(): List<ClientEntity>

    @Query("DELETE FROM clients")
    suspend fun deleteAllClients()

}