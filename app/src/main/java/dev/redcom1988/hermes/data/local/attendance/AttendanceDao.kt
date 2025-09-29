package dev.redcom1988.hermes.data.local.attendance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceTaskCrossRefEntity
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendances")
    suspend fun getAllAttendances(): List<AttendanceEntity>

    @Query("SELECT * FROM attendances WHERE isDeleted = 0")
    fun getVisibleAttendancesFlow(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_tasks WHERE isDeleted = 0")
    fun getVisibleAttendanceTasks(): Flow<List<AttendanceTaskCrossRefEntity>>

    @Query("SELECT * FROM attendances WHERE employeeId = :employeeId AND endTime IS NULL LIMIT 1")
    suspend fun getActiveAttendanceForEmployee(employeeId: Int): AttendanceEntity?

    @Query("SELECT * FROM attendances WHERE employeeId = :employeeId AND endTime IS NULL LIMIT 1")
    fun observeActiveAttendanceForEmployee(employeeId: Int): Flow<AttendanceEntity?>

    @Query("SELECT tasks.* FROM tasks INNER JOIN attendance_tasks ON tasks.taskId = attendance_tasks.taskId WHERE attendance_tasks.attendanceId = :attendanceId")
     fun getTasksForAttendance(attendanceId: Int): Flow<List<TaskEntity>>

    @Query("SELECT MIN(attendanceId) FROM attendances WHERE attendanceId < 0")
    suspend fun getMinTempAttendanceId(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendances: List<AttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendances WHERE attendanceId = :attendanceId")
    suspend fun getAttendanceById(attendanceId: Int): AttendanceEntity?

    @Transaction
    suspend fun upsertAttendance(attendance: AttendanceEntity) {
        val existing = getAttendanceById(attendance.attendanceId)
        if (existing != null) {
            updateAttendance(attendance.copy(isSynced = false))
        } else {
            insertAttendance(attendance)
        }
    }

    @Transaction
    suspend fun upsertRemoteAttendance(remote: AttendanceEntity) {
        val existing = getAttendanceById(remote.attendanceId)
        if (existing != null) {
            updateAttendance(remote)
        } else {
            insertAttendance(remote)
        }
    }

    @Transaction
    suspend fun upsertAttendances(attendances: List<AttendanceEntity>) {
        attendances.forEach { attendance ->
            upsertRemoteAttendance(attendance)
        }
    }

    @Query("UPDATE attendances SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE attendanceId = :attendanceId")
    suspend fun softDeleteAttendanceById(attendanceId: Int, updatedAt: String)

    @Query("SELECT * FROM attendances WHERE isSynced = 0 AND endTime IS NOT NULL")
    suspend fun getPendingSyncAttendances(): List<AttendanceEntity>

    @Query("DELETE FROM attendances")
    suspend fun deleteAllAttendances()
}
