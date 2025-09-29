package dev.redcom1988.hermes.data.remote.model.responses

import dev.redcom1988.hermes.data.remote.model.AccessDto
import dev.redcom1988.hermes.data.remote.model.DivisionAccessDto
import dev.redcom1988.hermes.data.remote.model.DivisionDto
import dev.redcom1988.hermes.data.remote.model.EmployeeDto
import dev.redcom1988.hermes.data.remote.model.UserDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.account_data.model.Access
import dev.redcom1988.hermes.domain.account_data.model.Division
import dev.redcom1988.hermes.domain.account_data.model.DivisionAccessCrossRef
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.account_data.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountApiResponseDto(
    val users: List<UserDto>?,
    val employees: List<EmployeeDto>?,
    val divisions: List<DivisionDto>?,
    val accesses: List<AccessDto>?,
    @SerialName("division_accesses")
    val divisionAccesses: List<DivisionAccessDto>?
)

fun AccountApiResponseDto.toDomainUsers(): List<User> {
    return users?.map { it.toDomain() } ?: emptyList()
}

fun AccountApiResponseDto.toDomainEmployees(): List<Employee> {
    return employees?.map { it.toDomain() } ?: emptyList()
}

fun AccountApiResponseDto.toDomainDivisions(): List<Division> {
    return divisions?.map { it.toDomain() } ?: emptyList()
}

fun AccountApiResponseDto.toDomainAccesses(): List<Access> {
    return accesses?.map { it.toDomain() } ?: emptyList()
}

fun AccountApiResponseDto.toDomainDivisionAccesses(): List<DivisionAccessCrossRef> {
    return divisionAccesses?.map { it.toDomain() } ?: emptyList()
}