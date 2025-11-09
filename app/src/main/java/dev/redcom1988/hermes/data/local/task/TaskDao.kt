package dev.redcom1988.hermes.data.local.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.domain.task.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE isSynced = 0")
    suspend fun getPendingSyncTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND parentTaskId = :taskId")
    suspend fun getTasksByParentId(taskId: Int): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isDeleted = 0")
    fun getVisibleTasksFlow(): Flow<List<TaskEntity>>

    @Query("SELECT MIN(taskId) FROM tasks WHERE taskId < 0")
    suspend fun getMinTempTaskId(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE taskId = :taskId")
    suspend fun softDeleteTaskById(taskId: Int, updatedAt: String)

    @Query("UPDATE tasks SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE parentTaskId = :taskId")
    suspend fun softDeleteSubTasksByParentId(taskId: Int, updatedAt: String)

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("UPDATE tasks SET status = :status AND isSynced = 0 AND updatedAt = :updatedAt WHERE taskId = :taskId")
    suspend fun markTaskAsCompleted(taskId: Int, status: TaskStatus, updatedAt: String)

    @Query("SELECT * FROM tasks WHERE parentTaskId = :taskId")
    fun getSubTasksByParentId(taskId: Int): Flow<List<TaskEntity>>

    @Transaction
    suspend fun upsertTask(task: TaskEntity) {
        val existing = getTaskById(task.taskId)
        if (existing != null) {
            updateTask(task.copy(isSynced = false))
        } else {
            insertTask(task)
        }
    }

    @Transaction
    suspend fun upsertRemoteTask(task: TaskEntity) {
        val existing = getTaskById(task.taskId)
        if (existing != null) {
            updateTask(task)
        } else {
            insertTask(task)
        }
    }

    @Transaction
    suspend fun upsertTasks(tasks: List<TaskEntity>) {
        tasks.forEach { task ->
            upsertRemoteTask(task)
        }
    }

    @Transaction
    suspend fun upsertRemoteTaskIfClean(remote: TaskEntity) {
        val local = getTaskById(remote.taskId)

        when {
            // No local → insert remote
            local == null -> insertTask(remote)

            // Local has unsynced changes → keep local, else last write wins
            !local.isSynced -> return
            remote.updatedAt.toLocalDateTime() > local.updatedAt.toLocalDateTime() -> insertTask(remote)

            else -> return
        }
    }


    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

}