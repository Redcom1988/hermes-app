package dev.redcom1988.hermes.data.local.meeting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingEntity
import dev.redcom1988.hermes.data.local.account_data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeetingDao {

    @Query("SELECT * FROM meetings")
    suspend fun getAllMeetings(): List<MeetingEntity>

    @Query("SELECT * FROM meetings WHERE isDeleted = 0")
    fun getVisibleMeetingsFlow(): Flow<List<MeetingEntity>>

    @Query("SELECT MIN(meetingId) FROM meetings WHERE meetingId < 0")
    suspend fun getMinTempMeetingId(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: MeetingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeetings(meetings: List<MeetingEntity>)

    @Update
    suspend fun updateMeeting(meeting: MeetingEntity)

    @Query("UPDATE meetings SET isDeleted = 1, isSynced = 0 WHERE meetingId = :id")
    suspend fun softDeleteMeetingById(id: Int)

    @Query("SELECT * FROM meetings WHERE isSynced = 0")
    suspend fun getPendingSyncMeetings(): List<MeetingEntity>

    @Query("DELETE FROM meetings")
    suspend fun deleteAllMeetings()

    @Query("""
        SELECT c.* FROM clients c
        INNER JOIN meeting_clients mc ON c.clientId = mc.clientId
        WHERE mc.meetingId = :meetingId AND mc.isDeleted = 0
    """)
    suspend fun getClientsForMeeting(meetingId: Int): List<ClientEntity>

    @Query("""
        SELECT u.* FROM users u
        INNER JOIN meeting_users mu ON u.userId = mu.userId
        WHERE mu.meetingId = :meetingId AND mu.isDeleted = 0
    """)
    suspend fun getUsersForMeeting(meetingId: Int): List<UserEntity>

    @Query("SELECT * FROM meetings WHERE meetingId = :id")
    suspend fun getMeetingById(id: Int): MeetingEntity?

    @Transaction
    suspend fun upsertMeeting(meeting: MeetingEntity) {
        val existingMeeting = getMeetingById(meeting.meetingId)
        if (existingMeeting != null) {
            updateMeeting(meeting.copy(isSynced = false))
        } else {
            insertMeeting(meeting)
        }
    }

    @Transaction
    suspend fun upsertRemoteMeeting(remote: MeetingEntity) {
        val existingMeeting = getMeetingById(remote.meetingId)
        if (existingMeeting != null) {
            updateMeeting(remote)
        } else {
            insertMeeting(remote)
        }
    }

    @Transaction
    suspend fun upsertMeetings(meetings: List<MeetingEntity>) {
        meetings.forEach { meeting ->
            upsertRemoteMeeting(meeting)
        }
    }
}
