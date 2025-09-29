package dev.redcom1988.hermes.data.local.account_data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employees")
    suspend fun getAllEmployees(): List<EmployeeEntity>

    @Query("SELECT * FROM employees WHERE isDeleted = 0")
    fun getVisibleEmployees(): Flow<List<EmployeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<EmployeeEntity>)

    @Query("SELECT * FROM employees WHERE employeeId = :employeeId")
    suspend fun getEmployeeById(employeeId: Int): EmployeeEntity?

    @Transaction
    suspend fun upsertRemoteEmployee(remote: EmployeeEntity) {
        val existing = getEmployeeById(remote.employeeId)
        if (existing != null) {
            updateEmployee(remote)
        } else {
            insertEmployee(remote)
        }
    }

    @Transaction
    suspend fun upsertEmployees(employees: List<EmployeeEntity>) {
        employees.forEach { employee ->
            upsertRemoteEmployee(employee)
        }
    }

    @Update
    suspend fun updateEmployee(employee: EmployeeEntity)

    @Query("DELETE FROM employees")
    suspend fun deleteAllEmployees()

}