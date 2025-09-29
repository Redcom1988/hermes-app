package dev.redcom1988.hermes.data.local.workhour_plan.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeEntity
import dev.redcom1988.hermes.domain.common.WorkLocation
import java.time.LocalDateTime

const val workhourPlanEntityTableName = "workhour_plans"

@Entity(
    tableName = workhourPlanEntityTableName,
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["employeeId"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkhourPlanEntity(
    @PrimaryKey val planId: Int,
    val employeeId: Int,
    val planDate: String,
    val plannedStartTime: String,
    val plannedEndTime: String,
    val workLocation: WorkLocation,
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow()
)
