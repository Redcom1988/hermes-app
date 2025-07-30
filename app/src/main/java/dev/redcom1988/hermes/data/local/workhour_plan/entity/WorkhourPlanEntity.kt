package dev.redcom1988.hermes.data.local.workhour_plan.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.data.local.user.entity.UserEntity
import java.time.LocalDate
import java.time.LocalDateTime

const val workhourPlanEntityTableName = "workhour_plan"

@Entity(
    tableName = workhourPlanEntityTableName,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkhourPlanEntity(
    @PrimaryKey val planId: Int,
    val userId: Int?,
    val planDate: LocalDate,
    val plannedStartTime: LocalDateTime,
    val plannedEndTime: LocalDateTime,
    val workLocation: String,
    val submittedAt: LocalDateTime
)

