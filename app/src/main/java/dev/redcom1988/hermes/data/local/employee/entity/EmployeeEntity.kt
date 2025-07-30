package dev.redcom1988.hermes.data.local.employee.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.data.local.user.entity.UserEntity
import dev.redcom1988.hermes.data.local.division.entity.DivisionEntity
import java.time.LocalDate
import java.time.LocalDateTime

const val employeeEntityTableName = "employees"

@Entity(
    tableName = employeeEntityTableName,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DivisionEntity::class,
            parentColumns = ["divisionId"],
            childColumns = ["divisionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class EmployeeEntity(
    @PrimaryKey val employeeId: Int,
    val userId: Int,
    val divisionId: Int,
    val fullName: String,
    val phoneNumber: String,
    val gender: String,
    val birthDate: String,
    val address: String,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
)