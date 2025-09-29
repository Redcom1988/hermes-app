package dev.redcom1988.hermes.domain.task

import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun addTask(
        name: String,
        description: String?,
        deadline: String,
        parentTaskId: Int? = null,
    ): Int

    suspend fun update(task: Task)
    suspend fun softDeleteTaskWithLinks(taskId: Int)
    suspend fun softDeleteEmployeeTaskLink(employeeId: Int, taskId: Int)

    fun getVisibleTasks(): Flow<List<Task>>
    fun getVisibleLinks(): Flow<List<EmployeeTaskCrossRef>>
    fun getSubtaskForTask(taskId: Int): Flow<List<Task>>
    fun getEmployeesForTask(taskId: Int): Flow<List<Employee>>
    fun getTasksForEmployee(employeeId: Int): Flow<List<Task>>

    suspend fun upsertEmployeeTaskLink(employeeId: Int, taskId: Int)
    suspend fun insertTasks(tasks: List<Task>)
    suspend fun insertEmployeeTasks(crossRefs: List<EmployeeTaskCrossRef>)

    suspend fun getPendingSyncTasks(): List<Task>
    suspend fun getPendingSyncEmployeeTasks(): List<EmployeeTaskCrossRef>

    suspend fun deleteAllTasks()
    suspend fun deleteAllEmployeeTasks()
//    suspend fun syncEmployeeTasks()
}