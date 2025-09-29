package dev.redcom1988.hermes.data.local.account_data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formattedNow

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
            onDelete = ForeignKey.CASCADE
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
    val imagePath: String? = null,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow()
)