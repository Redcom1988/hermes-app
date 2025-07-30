package dev.redcom1988.hermes.data.local.meeting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingWithUsersAndClients
import dev.redcom1988.hermes.domain.common.SyncStatus
import dev.redcom1988.hermes.domain.meeting.Meeting
import kotlinx.coroutines.flow.Flow

@Dao
interface MeetingDao {

    @Query("SELECT * FROM meetings")
    suspend fun getAllMeetings(): List<MeetingEntity>

    @Query("SELECT * FROM meetings WHERE syncStatus != 'DELETED' AND isDeleted = 0")
    fun getVisibleMeetingsFlow(): Flow<List<MeetingEntity>>

    @Query("SELECT * FROM meetings WHERE syncStatus != 'DELETED' AND isDeleted = 0")
    fun getVisibleMeetingsWithUsersOrClients(): Flow<List<MeetingWithUsersAndClients>>

    @Query("SELECT MIN(meetingId) FROM meetings WHERE meetingId < 0")
    suspend fun getMinTempMeetingId(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: MeetingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeetings(meetings: List<MeetingEntity>)

    @Update
    suspend fun updateMeeting(meeting: MeetingEntity)

    @Query("UPDATE meetings SET syncStatus = :status WHERE meetingId = :id")
    suspend fun softDeleteMeetingById(id: Int, status: SyncStatus = SyncStatus.DELETED)

    @Query("SELECT * FROM meetings WHERE syncStatus IN ('CREATED', 'UPDATED', 'DELETED')")
    suspend fun getPendingSyncMeetings(): List<MeetingEntity>

    @Query("DELETE FROM meetings")
    suspend fun deleteAllMeetings()

}