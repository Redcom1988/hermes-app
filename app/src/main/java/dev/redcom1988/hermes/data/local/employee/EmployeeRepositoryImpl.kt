package dev.redcom1988.hermes.data.local.employee

import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.remote.api.EmployeeApi
import dev.redcom1988.hermes.data.remote.model.EmployeeDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.employee.Employee
import dev.redcom1988.hermes.domain.employee.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EmployeeRepositoryImpl (
    private val employeeDao: EmployeeDao,
    private val api: EmployeeApi
) : EmployeeRepository {

    override fun getEmployeesFlow(): Flow<List<Employee>> {
        return employeeDao.getVisibleEmployeesFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncEmployeesFromServer() {
        val response = api.getEmployees()
        if (response.isSuccessful) {
            val employees = response.parseAs<List<EmployeeDto>>().map { it.toDomain().toEntity() }
            employeeDao.insertEmployees(employees)
        }
    }

    override suspend fun clearLocalEmployees() {
        employeeDao.deleteAllEmployees()
    }

}