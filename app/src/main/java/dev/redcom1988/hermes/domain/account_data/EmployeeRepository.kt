package dev.redcom1988.hermes.domain.account_data

import dev.redcom1988.hermes.domain.account_data.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {

    fun getVisibleEmployees(): Flow<List<Employee>>

    suspend fun getEmployeeById(id: Int): Employee?

}