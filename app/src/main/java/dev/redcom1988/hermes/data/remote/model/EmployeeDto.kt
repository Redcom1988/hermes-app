package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.account_data.model.Employee
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDto(
    val id: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("division_id")
    val divisionId: Int,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    val gender: String,
    @SerialName("birth_date")
    val birthDate: String,
    val address: String,
    @SerialName("image_path")
    val imagePath: String?,
    @SerialName("is_deleted")
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
    imagePath = this.imagePath,
    isDeleted = this.isDeleted,
    updatedAt = this.updatedAt,
    createdAt = this.createdAt,
)