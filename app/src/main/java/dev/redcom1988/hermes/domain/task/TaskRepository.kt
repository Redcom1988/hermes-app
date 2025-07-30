package dev.redcom1988.hermes.domain.task

import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.data.local.task.entity.TaskWithSubTasks
import dev.redcom1988.hermes.domain.user.User
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksFlow(): Flow<List<Task>>
    fun getTasksWithSubTasksFlow(): Flow<List<TaskWithSubTasks>>
    fun getTasksByUserIdFlow(userId: Int): Flow<List<Task>>
    fun getUsersByTaskIdFlow(taskId: Int): Flow<List<User>>
    suspend fun addTask(
        name: String,
        description: String?,
        deadline: String,
        parentTaskId: Int? = null,
    ): Int
    suspend fun updateTask(task: Task)
    suspend fun deleteTaskById(taskId: Int)
    suspend fun getPendingSyncTasks(): List<TaskEntity>
    suspend fun syncTasks()
    suspend fun clearLocalTasks()
}