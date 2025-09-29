package dev.redcom1988.hermes.data.local.attendance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceTaskCrossRefEntity

@Dao
interface AttendanceTaskDao {

    @Query("SELECT * FROM attendance_tasks WHERE isSynced = 0")
    suspend fun getPendingSyncLinks(): List<AttendanceTaskCrossRefEntity>

    @Query("""SELECT * FROM attendance_tasks
        WHERE attendanceId = :attendanceId AND taskId = :taskId 
        LIMIT 1
    """)
    suspend fun findLink(attendanceId: Int, taskId: Int): AttendanceTaskCrossRefEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertLink(attendanceTask: AttendanceTaskCrossRefEntity)

    @Query("""
        UPDATE attendance_tasks
        SET isDeleted = 0, isSynced = 0, updatedAt = :updatedAt
        WHERE attendanceId = :attendanceId AND taskId = :taskId
    """)
    suspend fun reactivateLink(attendanceId: Int, taskId: Int, updatedAt: String)

    @Query("""
        UPDATE attendance_tasks
        SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt
        WHERE attendanceId = :attendanceId AND taskId = :taskId
    """)
    suspend fun softDeleteLink(attendanceId: Int, taskId: Int, updatedAt: String)

    @Query("""
    UPDATE attendance_tasks
    SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt
    WHERE attendanceId = :attendanceId
    """)
    suspend fun softDeleteLinksByAttendanceId(attendanceId: Int, updatedAt: String)

    @Update
    suspend fun updateLink(attendanceTask: AttendanceTaskCrossRefEntity)

    @Transaction
    suspend fun upsertLink(attendanceId: Int, taskId: Int) {
        val existing = findLink(attendanceId, taskId)
        if (existing != null) {
            reactivateLink(attendanceId, taskId, formattedNow())
        } else {
            insertLink(AttendanceTaskCrossRefEntity(
                attendanceId = attendanceId,
                taskId = taskId,
                isSynced = false,
                isDeleted = false,
                updatedAt = formattedNow(),
                createdAt = formattedNow()
            ))
        }
    }

    @Transaction
    suspend fun upsertRemoteLink(remote: AttendanceTaskCrossRefEntity) {
        val existing = findLink(remote.attendanceId, remote.taskId)
        if (existing != null) {
            updateLink(remote)
        } else
            insertLink(remote)
    }

    @Transaction
    suspend fun upsertLinks(remotes: List<AttendanceTaskCrossRefEntity>) {
        remotes.forEach { remote ->
            upsertRemoteLink(remote)
        }
    }

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertLinks (attendanceTasks: List<AttendanceTaskCrossRefEntity>)

    @Query("DELETE FROM attendance_tasks")
    suspend fun deleteAllLinks()

}