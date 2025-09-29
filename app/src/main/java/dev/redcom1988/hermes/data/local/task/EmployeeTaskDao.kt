package dev.redcom1988.hermes.data.local.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeEntity
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeTaskCrossRefEntity
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeTaskDao {

    @Query("SELECT * FROM employee_tasks WHERE isSynced = 0")
    suspend fun getPendingSyncLinks(): List<EmployeeTaskCrossRefEntity>

    @Query("SELECT * FROM employee_tasks WHERE isDeleted = 0")
    fun getVisibleLinks(): Flow<List<EmployeeTaskCrossRefEntity>>

    @Query("""
        SELECT * FROM employee_tasks 
        WHERE employeeId = :employeeId AND taskId = :taskId 
        LIMIT 1
    """)
    suspend fun findLink(employeeId: Int, taskId: Int): EmployeeTaskCrossRefEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLink(crossRef: EmployeeTaskCrossRefEntity)

    @Query("""
        UPDATE employee_tasks 
        SET isDeleted = 0, isSynced = 0, updatedAt = :updatedAt 
        WHERE employeeId = :employeeId AND taskId = :taskId
    """)
    suspend fun reactivateLink(employeeId: Int, taskId: Int, updatedAt: String)

    @Query("""
        UPDATE employee_tasks 
        SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt 
        WHERE employeeId = :employeeId AND taskId = :taskId
    """)
    suspend fun softDeleteLink(employeeId: Int, taskId: Int, updatedAt: String)

    @Query("""
        UPDATE employee_tasks 
        SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt
        WHERE taskId = :taskId
    """)
    suspend fun softDeleteAllLinksForTask(taskId: Int, updatedAt: String)

    @Update
    suspend fun updateLink(crossRef: EmployeeTaskCrossRefEntity)

    @Transaction
    suspend fun upsertLink(employeeId: Int, taskId: Int) {
        val existing = findLink(employeeId, taskId)

        if (existing != null) {
            reactivateLink(employeeId, taskId, formattedNow())
        } else {
            insertLink(
                EmployeeTaskCrossRefEntity(
                    employeeId = employeeId,
                    taskId = taskId,
                    isSynced = false,
                    isDeleted = false,
                    updatedAt = formattedNow(),
                    createdAt = formattedNow()
                )
            )
        }
    }

    @Transaction
    suspend fun upsertRemoteLink(remote: EmployeeTaskCrossRefEntity) {
        val existing = findLink(remote.employeeId, remote.taskId)
        if (existing != null) {
            updateLink(remote)
        } else {
            insertLink(remote)
        }
    }

    @Transaction
    suspend fun upsertLinks(remotes: List<EmployeeTaskCrossRefEntity>) {
        remotes.forEach { remote ->
            upsertRemoteLink(remote)
        }
    }

    @Query("""
        SELECT e.* FROM employees e
        INNER JOIN employee_tasks etr ON e.employeeId = etr.employeeId
        WHERE etr.taskId = :taskId AND etr.isDeleted = 0
    """)
    fun getActiveEmployeesForTask(taskId: Int): Flow<List<EmployeeEntity>>

    @Query("""
        SELECT t.* FROM tasks t
        INNER JOIN employee_tasks etr ON t.taskId = etr.taskId
        WHERE etr.employeeId = :employeeId AND etr.isDeleted = 0
    """)
    fun getActiveTasksForEmployee(employeeId: Int): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<EmployeeTaskCrossRefEntity>)

    @Query("DELETE FROM employee_tasks")
    suspend fun deleteAllLinks()
}
