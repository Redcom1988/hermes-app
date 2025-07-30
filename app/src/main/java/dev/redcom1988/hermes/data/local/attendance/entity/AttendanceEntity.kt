package dev.redcom1988.hermes.data.local.attendance.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.data.local.user.entity.UserEntity
import dev.redcom1988.hermes.domain.attendance.AttendanceWorkLocation
import dev.redcom1988.hermes.domain.common.SyncStatus
import java.time.LocalDateTime

const val attendanceEntityTableName = "attendances"

@Entity(
    tableName = attendanceEntityTableName,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AttendanceEntity(
    @PrimaryKey val attendanceId: Int,
    val userId: Int,
    val startTime: String = LocalDateTime.now().formatToString(),
    val endTime: String? = null,
    val workLocation: AttendanceWorkLocation,
    val longitude: Double,
    val latitude: Double,
    val imagePath: String?,
    val taskLink: String? = null,
    val syncStatus: SyncStatus = SyncStatus.CREATED,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
)