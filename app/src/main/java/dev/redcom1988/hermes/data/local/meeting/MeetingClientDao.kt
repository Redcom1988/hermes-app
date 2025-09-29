package dev.redcom1988.hermes.data.local.meeting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingClientCrossRefEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeetingClientDao {

    @Query("SELECT * FROM meeting_clients WHERE isSynced = 0")
    suspend fun getPendingSyncLinks(): List<MeetingClientCrossRefEntity>

    @Query("SELECT * FROM meeting_clients WHERE isDeleted = 0")
    fun getVisibleLinks(): Flow<List<MeetingClientCrossRefEntity>>

    @Query("""
        SELECT * FROM meeting_clients 
        WHERE meetingId = :meetingId AND clientId = :clientId 
        LIMIT 1
    """)
    suspend fun findLink(meetingId: Int, clientId: Int): MeetingClientCrossRefEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLink(crossRef: MeetingClientCrossRefEntity)

    @Query("""
        UPDATE meeting_clients
        SET isDeleted = 0, isSynced = 0, updatedAt = :updatedAt 
        WHERE meetingId = :meetingId AND clientId = :clientId
    """)
    suspend fun reactivateLink(meetingId: Int, clientId: Int, updatedAt: String)

    @Query("""
        UPDATE meeting_clients 
        SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt 
        WHERE meetingId = :meetingId AND clientId = :clientId
    """)
    suspend fun softDeleteLink(meetingId: Int, clientId: Int, updatedAt: String)

    @Query("""
        UPDATE meeting_clients 
        SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt
        WHERE meetingId = :meetingId
    """)
    suspend fun softDeleteAllLinksForMeeting(meetingId: Int, updatedAt: String)

    @Update
    suspend fun updateLink(crossRef: MeetingClientCrossRefEntity)

    @Transaction
    suspend fun upsertLink(meetingId: Int, clientId: Int) {
        val existing = findLink(meetingId, clientId)

        if (existing != null) {
            reactivateLink(meetingId, clientId, formattedNow())
        } else {
            insertLink(
                MeetingClientCrossRefEntity(
                    meetingId = meetingId,
                    clientId = clientId,
                    isSynced = false,
                    isDeleted = false,
                    updatedAt = formattedNow(),
                    createdAt = formattedNow()
                )
            )
        }
    }

    @Transaction
    suspend fun upsertRemoteLink(remote: MeetingClientCrossRefEntity) {
        val existing = findLink(remote.meetingId, remote.clientId)
        if (existing != null) {
            updateLink(remote)
        } else {
            insertLink(remote)
        }
    }

    @Transaction
    suspend fun upsertLinks(remotes: List<MeetingClientCrossRefEntity>) {
        remotes.forEach { remote ->
            upsertRemoteLink(remote)
        }
    }

    @Query("""
        SELECT c.* FROM clients c
        INNER JOIN meeting_clients mc ON c.clientId = mc.clientId
        WHERE mc.meetingId = :meetingId AND mc.isDeleted = 0
    """)
    suspend fun getActiveClientsForMeeting(meetingId: Int): List<ClientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<MeetingClientCrossRefEntity>)

    @Query("DELETE FROM meeting_clients")
    suspend fun deleteAllLinks()
}
