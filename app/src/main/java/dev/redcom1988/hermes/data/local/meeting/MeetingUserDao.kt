package dev.redcom1988.hermes.data.local.meeting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingUserCrossRefEntity
import dev.redcom1988.hermes.data.local.account_data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeetingUserDao {

    @Query("SELECT * FROM meeting_users WHERE isSynced = 0")
    suspend fun getPendingSyncLinks(): List<MeetingUserCrossRefEntity>

    @Query("SELECT * FROM meeting_users WHERE isDeleted = 0")
    fun getVisibleLinks(): Flow<List<MeetingUserCrossRefEntity>>

    @Query("""
        SELECT * FROM meeting_users 
        WHERE meetingId = :meetingId AND userId = :userId 
        LIMIT 1
    """)
    suspend fun findLink(meetingId: Int, userId: Int): MeetingUserCrossRefEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLink(crossRef: MeetingUserCrossRefEntity)

    @Query("""
        UPDATE meeting_users
        SET isDeleted = 0, isSynced = 0, updatedAt = :updatedAt 
        WHERE meetingId = :meetingId AND userId = :userId
    """)
    suspend fun reactivateLink(meetingId: Int, userId: Int, updatedAt: String)

    @Query("""
        UPDATE meeting_users 
        SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt 
        WHERE meetingId = :meetingId AND userId = :userId
    """)
    suspend fun softDeleteLink(meetingId: Int, userId: Int, updatedAt: String)

    @Query("""
        UPDATE meeting_users 
        SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt
        WHERE meetingId = :meetingId
    """)
    suspend fun softDeleteAllLinksForMeeting(meetingId: Int, updatedAt: String)

    @Update
    suspend fun updateLink(crossRef: MeetingUserCrossRefEntity)

    @Transaction
    suspend fun upsertLink(meetingId: Int, userId: Int) {
        val now = formattedNow()
        val existing = findLink(meetingId, userId)

        if (existing != null) {
            reactivateLink(meetingId, userId, now)
        } else {
            insertLink(
                MeetingUserCrossRefEntity(
                    meetingId = meetingId,
                    userId = userId,
                    isSynced = false,
                    isDeleted = false,
                    updatedAt = now,
                    createdAt = now
                )
            )
        }
    }

    @Transaction
    suspend fun upsertRemoteLink(remote: MeetingUserCrossRefEntity) {
        val existing = findLink(remote.meetingId, remote.userId)
        if (existing != null) {
            updateLink(remote)
        } else {
            insertLink(remote)
        }
    }

    @Transaction
    suspend fun upsertLinks(links: List<MeetingUserCrossRefEntity>) {
        links.forEach { link ->
            upsertRemoteLink(link)
        }
    }

    @Query("""
        SELECT u.* FROM users u
        INNER JOIN meeting_users mu ON u.userId = mu.userId
        WHERE mu.meetingId = :meetingId AND mu.isDeleted = 0
    """)
    suspend fun getActiveUsersForMeeting(meetingId: Int): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<MeetingUserCrossRefEntity>)

    @Query("DELETE FROM meeting_users")
    suspend fun deleteAllLinks()
}
