package dev.redcom1988.hermes.data.local.attendance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceWithTasks
import dev.redcom1988.hermes.domain.common.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendances")
    fun getAllAttendances(): List<AttendanceEntity>

    @Query("SELECT * FROM attendances WHERE syncStatus != 'DELETED' AND isDeleted = 0")
    fun getVisibleAttendancesFlow(): Flow<List<AttendanceEntity>>

    @Transaction
    @Query("SELECT * FROM attendances WHERE syncStatus != 'DELETED' AND isDeleted = 0")
    fun getVisibleAttendancesWithTasksFlow() : Flow<List<AttendanceWithTasks>>

    @Query("SELECT * FROM attendances WHERE userId = :userId AND endTime IS NULL LIMIT 1")
    suspend fun getActiveAttendanceForUser(userId: Int): AttendanceEntity?

    @Query("SELECT MIN(attendanceId) FROM attendances WHERE attendanceId < 0")
    suspend fun getMinTempAttendanceId(): Int?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAttendances(attendances: List<AttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Query("UPDATE attendances SET syncStatus = :status WHERE attendanceId = :id")
    suspend fun softDeleteAttendanceById(id: Int, status: SyncStatus = SyncStatus.DELETED)

    @Query("SELECT * FROM attendances WHERE syncStatus IN ('CREATED', 'UPDATED', 'DELETED')")
    suspend fun getPendingSyncAttendances(): List<AttendanceEntity>

    @Query("DELETE FROM attendances")
    suspend fun deleteAllAttendances()
}