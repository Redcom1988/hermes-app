package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.employee.Employee
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDto (
    val id: Int,
    val userId: Int,
    val divisionId: Int,
    val fullName: String,
    val phoneNumber: String,
    val gender: String,
    val birthDate: String,
    val address: String,
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun EmployeeDto.toDomain() = Employee(
    id = this.id,
    userId = this.userId,
    divisionId = this.divisionId,
    fullName = this.fullName,
    phoneNumber = this.phoneNumber,
    gender = this.gender,
    birthDate = this.birthDate,
    address = this.address,
    isDeleted = this.isDeleted,
    updatedAt = this.updatedAt,
    createdAt = this.createdAt,
)