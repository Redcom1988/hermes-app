package dev.redcom1988.hermes.data.local.account_data

import dev.redcom1988.hermes.data.local.account_data.dao.DivisionDao
import dev.redcom1988.hermes.data.local.account_data.dao.EmployeeDao
import dev.redcom1988.hermes.data.local.account_data.mapper.toDomain
import dev.redcom1988.hermes.domain.account_data.DivisionRepository
import dev.redcom1988.hermes.domain.account_data.EmployeeRepository
import dev.redcom1988.hermes.domain.account_data.model.Employee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EmployeeRepositoryImpl(
    private val employeeDao: EmployeeDao,
) : EmployeeRepository {

    override fun getVisibleEmployees(): Flow<List<Employee>> {
        return employeeDao.getVisibleEmployees().map { it -> it.map { it.toDomain() } }
    }

    override suspend fun getEmployeeById(id: Int): Employee? {
        return employeeDao.getEmployeeById(id)?.toDomain()
    }

}