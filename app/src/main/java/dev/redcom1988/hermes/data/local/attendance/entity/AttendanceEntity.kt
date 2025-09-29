package dev.redcom1988.hermes.data.local.attendance.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeEntity
import dev.redcom1988.hermes.domain.common.WorkLocation
import java.time.LocalDateTime

const val attendanceEntityTableName = "attendances"

@Entity(
    tableName = attendanceEntityTableName,
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["employeeId"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AttendanceEntity(
    @PrimaryKey val attendanceId: Int,
    val employeeId: Int,
    val startTime: String = LocalDateTime.now().formatToString(),
    val endTime: String? = null,
    val workLocation: WorkLocation,
    val longitude: Double,
    val latitude: Double,
    val imagePath: String?,
    val taskLink: String? = null,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
)