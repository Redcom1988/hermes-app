package dev.redcom1988.hermes.data.local.task

import androidx.room.Transaction
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.data.local.account_data.mapper.toDomain
import dev.redcom1988.hermes.data.local.account_data.mapper.toEntity
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.data.remote.api.TaskApi
import dev.redcom1988.hermes.data.remote.model.requests.TaskApiRequestDto
import dev.redcom1988.hermes.data.remote.model.responses.TaskApiResponseDto
import dev.redcom1988.hermes.data.remote.model.responses.toDomainEmployeeTasks
import dev.redcom1988.hermes.data.remote.model.responses.toDomainTasks
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef
import dev.redcom1988.hermes.domain.account_data.mapper.toDto
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskRepository
import dev.redcom1988.hermes.domain.task.TaskStatus
import dev.redcom1988.hermes.domain.task.toDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val employeeTaskDao: EmployeeTaskDao,
) : TaskRepository {

    // """ LOCAL OPERATIONS """

    // Generate a temporary task ID for new tasks
    suspend fun generateTempTaskId(): Int {
        val minTempId = taskDao.getMinTempTaskId() ?: 0
        return minTempId - 1
    }

    // Add a new task
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
            status = TaskStatus.PENDING,
            deadline = deadline,
            parentTaskId = parentTaskId,
            isSynced = false,
            isDeleted = false,
            updatedAt = formattedNow(),
            createdAt = formattedNow()
        )
        taskDao.upsertTask(entity)
        return tempId
    }

    // Unused insert function, commented out for now
//    override suspend fun insert(task: Task) {
//        taskDao.insertTask(task.toEntity())
//    }

    // Update task locally
    override suspend fun update(task: Task) {
        val updated = task.copy(updatedAt = formattedNow())
        taskDao.updateTask(updated.toEntity(isSynced = false))
    }

    override fun getVisibleLinks(): Flow<List<EmployeeTaskCrossRef>> {
        return employeeTaskDao.getVisibleLinks()
            .map { list -> list.map { it.toDomain() } }
    }

    // Clear parent ID from subtasks of a given task
    @Transaction
    suspend fun clearParentIdFromSubtask(taskId: Int) {
        val subTasks = taskDao.getTasksByParentId(taskId)
        subTasks.forEach { subTask ->
            val updatedSubTask = subTask.copy(parentTaskId = null, isSynced = false, updatedAt = formattedNow())
            taskDao.updateTask(updatedSubTask)
        }
    }

    // Soft delete a task and its subtasks, along with employee-task links
    @Transaction
    override suspend fun softDeleteTaskWithLinks(taskId: Int) {
        taskDao.softDeleteTaskById(taskId, formattedNow())
        // Only soft delete to match web behavior
        taskDao.softDeleteSubTasksByParentId(taskId, formattedNow())
//        if (softDeleteConnected) {
//            taskDao.softDeleteSubTasksByParentId(taskId, formattedNow())
//        } else {
//            clearParentIdFromSubtask(taskId)
//        }
        employeeTaskDao.softDeleteAllLinksForTask(taskId, formattedNow())
    }

    override fun getVisibleTasks(): Flow<List<Task>> {
        return taskDao.getVisibleTasksFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getSubtaskForTask(taskId: Int): Flow<List<Task>> {
        return taskDao.getSubTasksByParentId(taskId).map { list -> list.map { it.toDomain() } }
    }

    override fun getEmployeesForTask(taskId: Int): Flow<List<Employee>> {
        return employeeTaskDao.getActiveEmployeesForTask(taskId)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun upsertEmployeeTaskLink(employeeId: Int, taskId: Int) {
        employeeTaskDao.upsertLink(employeeId, taskId)
    }

    override suspend fun softDeleteEmployeeTaskLink(employeeId: Int, taskId: Int) {
        employeeTaskDao.softDeleteLink(employeeId, taskId, formattedNow())
    }

    override suspend fun insertTasks(tasks: List<Task>) {
        taskDao.insertTasks(tasks.map { it.toEntity() })
    }

    override suspend fun insertEmployeeTasks(crossRefs: List<EmployeeTaskCrossRef>) {
        employeeTaskDao.insertLinks(crossRefs.map { it.toEntity() })
    }

    override suspend fun getPendingSyncTasks(): List<Task> {
        return taskDao.getPendingSyncTasks().map { it.toDomain() }
    }

    override suspend fun getPendingSyncEmployeeTasks(): List<EmployeeTaskCrossRef> {
        return employeeTaskDao.getPendingSyncLinks().map { it.toDomain() }
    }

    override suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    override suspend fun deleteAllEmployeeTasks() {
        employeeTaskDao.deleteAllLinks()
    }


    override fun getTasksForEmployee(employeeId: Int): Flow<List<Task>> {
        return employeeTaskDao.getActiveTasksForEmployee(employeeId)
            .map { list -> list.map { it.toDomain() } }
    }

    // """ API OPERATIONS """

//    private suspend fun fetchDataFromRemote(): TaskApiResponseDto {
//        val response = api.getTaskData()
//        if (!response.isSuccessful) {
//            throw Exception("Failed to fetch tasks from remote: ${response.code}")
//        }
//
//        val bodyString = response.body.string()
//        return Json.decodeFromString<TaskApiResponseDto>(bodyString)
//    }

//    @Transaction
//    private suspend fun upsertDataFromRemote(response: TaskApiResponseDto) {
//        val remoteTasks = response.toDomainTasks()
//        val remoteEmployeeTasks = response.toDomainEmployeeTasks()
//
//        remoteTasks.forEach { task ->
//            val entity = task.toEntity()
//            taskDao.upsertRemoteTaskIfClean(entity)
//        }
//
//        remoteEmployeeTasks.forEach { crossRef ->
//            val entity = crossRef.toEntity()
//            employeeTaskDao.upsertRemoteLinkIfClean(entity)
//        }
//    }
//
//    private suspend fun pushChangesToRemote() {
//        val requestDto = TaskApiRequestDto(
//            tasks = getPendingSyncTasks().map { it.toDto() },
//            employeeTasks = getPendingSyncEmployeeTasks().map { it.toDto() }
//        )
//        api.pushTaskChanges(requestDto)
//    }
//
//    override suspend fun syncEmployeeTasks() {
//        pushChangesToRemote()
//        val response = fetchDataFromRemote()
//        upsertDataFromRemote(response)
//    }
}