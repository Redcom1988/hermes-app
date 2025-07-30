package dev.redcom1988.hermes.domain.employee

import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    fun getEmployeesFlow(): Flow<List<Employee>>
    suspend fun syncEmployeesFromServer()
    suspend fun clearLocalEmployees()
}