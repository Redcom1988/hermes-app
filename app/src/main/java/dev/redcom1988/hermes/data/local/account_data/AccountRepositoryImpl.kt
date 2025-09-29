package dev.redcom1988.hermes.data.local.account_data

import android.util.Log
import dev.redcom1988.hermes.data.local.account_data.dao.AccessDao
import dev.redcom1988.hermes.data.local.account_data.dao.DivisionDao
import dev.redcom1988.hermes.data.local.account_data.dao.EmployeeDao
import dev.redcom1988.hermes.data.local.account_data.dao.UserDao
import dev.redcom1988.hermes.data.local.account_data.entity.AccessEntity
import dev.redcom1988.hermes.data.local.account_data.entity.DivisionAccessCrossRefEntity
import dev.redcom1988.hermes.data.local.account_data.entity.DivisionEntity
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeEntity
import dev.redcom1988.hermes.data.local.account_data.entity.UserEntity
import dev.redcom1988.hermes.data.local.account_data.mapper.toDomain
import dev.redcom1988.hermes.data.local.account_data.mapper.toEntity
import dev.redcom1988.hermes.data.remote.api.AccountApi
import dev.redcom1988.hermes.data.remote.model.responses.AccountApiResponseDto
import dev.redcom1988.hermes.data.remote.model.responses.toDomainAccesses
import dev.redcom1988.hermes.data.remote.model.responses.toDomainDivisionAccesses
import dev.redcom1988.hermes.data.remote.model.responses.toDomainDivisions
import dev.redcom1988.hermes.data.remote.model.responses.toDomainEmployees
import dev.redcom1988.hermes.data.remote.model.responses.toDomainUsers
import dev.redcom1988.hermes.domain.account_data.AccountRepository
import dev.redcom1988.hermes.domain.account_data.model.Access
import dev.redcom1988.hermes.domain.account_data.model.Division
import dev.redcom1988.hermes.domain.account_data.model.DivisionAccessCrossRef
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.account_data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

enum class AccountDataType {
    USER, EMPLOYEE, DIVISION, ACCESS, DIVISION_ACCESS
}

data class AccountData(
    val users: List<User>,
    val employees: List<Employee>,
    val divisions: List<Division>,
    val accesses: List<Access>,
    val divisionAccesses: List<DivisionAccessCrossRef>
)

class AccountRepositoryImpl(
    private val userDao: UserDao,
    private val employeeDao: EmployeeDao,
    private val divisionDao: DivisionDao,
    private val accessDao: AccessDao,
    private val api: AccountApi
) : AccountRepository {

    override fun getAccountDataFlow(): Flow<AccountData> {
        return combine(
            userDao.getVisibleUsers().map { it -> it.map { it.toDomain() } },
            employeeDao.getVisibleEmployees().map { it -> it.map { it.toDomain() } },
            divisionDao.getVisibleDivisions().map { it -> it.map { it.toDomain() } },
            accessDao.getVisibleAccesses().map { it -> it.map { it.toDomain() } },
            divisionDao.getVisibleDivisionAccesses().map { it -> it.map { it.toDomain() } }
        ) { users, employees, divisions, accesses, divisionAccesses ->
            AccountData(users, employees, divisions, accesses, divisionAccesses)
        }
    }

//    override suspend fun fetchAccountDataFromRemote(): AccountApiResponseDto {
//        val response = api.getAccountData()
//        if (!response.isSuccessful) {
//            throw Exception("Failed to fetch account data from remote: ${response.code}")
//        }
//
//        val bodyString = response.body.string()
//        Log.e("ASD", "Account data response: $bodyString")
//        return Json.decodeFromString<AccountApiResponseDto>(bodyString)
//    }

    override suspend fun clearLocalAccountData() {
        userDao.deleteAllUsers()
        employeeDao.deleteAllEmployees()
        divisionDao.deleteAllDivisions()
        divisionDao.deleteAllDivisionAccesses()
        accessDao.deleteAllAccesses()
    }

//    override suspend fun upsertAccountData(data: Any, dataType: AccountDataType) {
//        when (dataType) {
//            AccountDataType.USER ->
//                userDao.upsertUser(data as UserEntity)
//            AccountDataType.EMPLOYEE ->
//                employeeDao.upsertEmployee(data as EmployeeEntity)
//            AccountDataType.DIVISION ->
//                divisionDao.upsertDivision(data as DivisionEntity)
//            AccountDataType.DIVISION_ACCESS ->
//                divisionDao.upsertDivisionAccess(data as DivisionAccessCrossRefEntity)
//            AccountDataType.ACCESS ->
//                accessDao.upsertAccess(data as AccessEntity)
//        }
//    }

//    override suspend fun syncAccountData() {
//        try {
//            Log.d("ASD", "Starting to fetch account data from remote")
//            val remoteData = fetchAccountDataFromRemote()
//            Log.d("ASD", "Fetched account data: $remoteData")
//
//            remoteData.toDomainUsers().forEach {
//                upsertAccountData(it.toEntity(), AccountDataType.USER)
//            }
//            remoteData.toDomainDivisions().forEach {
//                upsertAccountData(it.toEntity(), AccountDataType.DIVISION)
//            }
//            remoteData.toDomainEmployees().forEach {
//                upsertAccountData(it.toEntity(), AccountDataType.EMPLOYEE)
//            }
//            remoteData.toDomainAccesses().forEach {
//                upsertAccountData(it.toEntity(), AccountDataType.ACCESS)
//            }
//            remoteData.toDomainDivisionAccesses().forEach {
//                upsertAccountData(it.toEntity(), AccountDataType.DIVISION_ACCESS)
//            }
//
//            Log.d("ASD", "Successfully synced account data")
//        } catch (e: Exception) {
//            Log.e("ASD", "Error syncing account data: ${e.message}", e)
//            throw e
//        }
//    }
}