package dev.redcom1988.hermes.data.local.task

import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.data.local.task.entity.TaskWithSubTasks
import dev.redcom1988.hermes.data.local.task.entity.visibleSubTasks
import dev.redcom1988.hermes.data.local.user.toDomain
import dev.redcom1988.hermes.data.remote.api.TaskApi
import dev.redcom1988.hermes.data.remote.model.TaskDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.common.SyncStatus
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskRepository
import dev.redcom1988.hermes.domain.task.toDto
import dev.redcom1988.hermes.domain.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val api: TaskApi
) : TaskRepository {

    override fun getTasksFlow(): Flow<List<Task>> {
        return taskDao.getVisibleTasksFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getTasksByUserIdFlow(userId: Int): Flow<List<Task>> {
        return taskDao.getUserWithTasksFlow(userId)
            .map { it.tasks.map { task -> task.toDomain() } }
    }

    override fun getUsersByTaskIdFlow(taskId: Int): Flow<List<User>> {
        return taskDao.getTaskWithUsersFlow(taskId)
            .map { it.users.map { user -> user.toDomain() } }
    }

    override fun getTasksWithSubTasksFlow(): Flow<List<TaskWithSubTasks>> {
        return taskDao.getTasksWithSubTasksFlow()
            .map { list -> list.map { taskWithSubTasks ->
                taskWithSubTasks.copy(subTasks = taskWithSubTasks.visibleSubTasks) } }
    }

    suspend fun syncTasksFromServer(serverTasks: List<Task>) {
        val localTasks = taskDao.getAllTasks()
            .associateBy { it.taskId }

        val mergedTasks = serverTasks.map { server ->
            val serverEntity = server.toEntity()
            val local = localTasks[server.id]

            when {
                local == null -> serverEntity
                local.syncStatus != SyncStatus.UNCHANGED && local.updatedAt > server.updatedAt -> local
                else -> serverEntity.copy(syncStatus = SyncStatus.UNCHANGED)
            }
        }
        taskDao.insertTasks(mergedTasks)
    }

    suspend fun generateTempTaskId(): Int {
        val minTempId = taskDao.getMinTempTaskId() ?: 0
        return minTempId - 1
    }

    override suspend fun addTask(
        name: String,
        description: String?,
        deadline: String,
        parentTaskId: Int?
    ): Int {
        val tempId = generateTempTaskId()
        val entity = TaskEntity(
            taskId = tempId,
            taskName = name,
            taskDescription = description,
            deadline = deadline,
            parentTaskId = parentTaskId
        )
        taskDao.insertTask(entity)
        return tempId
    }

    override suspend fun updateTask(task: Task) {
        val entity = task.toEntity().copy(
            syncStatus = SyncStatus.UPDATED,
            updatedAt = LocalDateTime.now().formatToString()
        )
        taskDao.updateTask(entity)
    }

    override suspend fun deleteTaskById(taskId: Int) {
        taskDao.softDeleteTaskById(taskId, SyncStatus.DELETED)
        taskDao.softDeleteSubTasksOf(taskId, SyncStatus.DELETED)
    }

    override suspend fun getPendingSyncTasks(): List<TaskEntity> {
        return taskDao.getPendingSyncTasks()
    }

    suspend fun pushPendingTasksToServer() {
        val pendingTasks = getPendingSyncTasks()

        for (task in pendingTasks) {
            val dto = task.toDomain().toDto()

            when (task.syncStatus) {
                SyncStatus.CREATED -> {
                    val result = api.createTask(dto)
                    if (result.isSuccessful) {
                        val serverTask = result.parseAs<TaskDto>().toDomain()
                        taskDao.insertTask(serverTask.toEntity()
                            .copy(syncStatus = SyncStatus.UNCHANGED))
                    }
                }

                SyncStatus.UPDATED -> {
                    val result = api.updateTask(task.taskId, task.toDomain().toDto())
                    if (result.isSuccessful) {
                        taskDao.updateTask(task
                            .copy(syncStatus = SyncStatus.UNCHANGED))
                    }
                }

                SyncStatus.DELETED -> {
                    val deletedDto = dto.copy(isDeleted = true)
                    val result = api.updateTask(task.taskId, deletedDto)
                    if (result.isSuccessful) {
                        taskDao.updateTask((task
                            .copy(syncStatus = SyncStatus.UNCHANGED)))
                    }
                }

                else -> Unit
            }
        }
    }

    override suspend fun syncTasks() {
        pushPendingTasksToServer()

        val response = api.getTasks()
        if (response.isSuccessful) {
            val tasksFromServer = response
                .parseAs<List<TaskDto>>()
                .map { dto: TaskDto -> dto.toDomain() }
            syncTasksFromServer(serverTasks = tasksFromServer)
        }
    }

    override suspend fun clearLocalTasks() {
        taskDao.deleteAllTasks()
    }
}