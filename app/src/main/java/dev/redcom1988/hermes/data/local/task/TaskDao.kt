package dev.redcom1988.hermes.data.local.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.data.local.task.entity.TaskWithSubTasks
import dev.redcom1988.hermes.data.local.task.entity.TaskWithUsers
import dev.redcom1988.hermes.data.local.user.entity.UserWithTasks
import dev.redcom1988.hermes.domain.common.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {


     @Query("SELECT * FROM tasks")
     suspend fun getAllTasks(): List<TaskEntity>

     @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND syncStatus != 'DELETED'")
     fun getVisibleTasksFlow(): Flow<List<TaskEntity>>

     @Transaction
     @Query("SELECT * FROM users WHERE userId = :userId")
     fun getUserWithTasksFlow(userId: Int): Flow<UserWithTasks>

     @Transaction
     @Query("SELECT * FROM tasks WHERE taskId = :taskId AND isDeleted = 0 AND syncStatus != 'DELETED'")
     fun getTaskWithUsersFlow(taskId: Int): Flow<TaskWithUsers>

     @Transaction
     @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND syncStatus != 'DELETED'")
     fun getTasksWithSubTasksFlow(): Flow<List<TaskWithSubTasks>>

     @Query("SELECT MIN(taskId) FROM tasks WHERE taskId < 0")
     suspend fun getMinTempTaskId(): Int?

     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertTask(task: TaskEntity)

     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertTasks(tasks: List<TaskEntity>)

     @Update
     suspend fun updateTask(task: TaskEntity)

     @Query("UPDATE tasks SET syncStatus = :status where taskId = :id")
     suspend fun softDeleteTaskById(id: Int, status: SyncStatus = SyncStatus.DELETED)

    @Query("UPDATE tasks SET syncStatus = :status WHERE parentTaskId = :parentId")
    suspend fun softDeleteSubTasksOf(parentId: Int, status: SyncStatus = SyncStatus.DELETED)


    @Query("SELECT * FROM tasks WHERE syncStatus IN ('CREATED', 'UPDATED', 'DELETED')")
     suspend fun getPendingSyncTasks(): List<TaskEntity>

     @Query("DELETE FROM tasks")
     suspend fun deleteAllTasks()

}