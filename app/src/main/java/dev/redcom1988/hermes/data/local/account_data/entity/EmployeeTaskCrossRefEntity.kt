package dev.redcom1988.hermes.data.local.account_data.entity

import androidx.room.Entity
import androidx.room.Index
import dev.redcom1988.hermes.core.util.extension.formattedNow

@Entity(
    tableName = "employee_tasks",
    primaryKeys = ["employeeId", "taskId"],
    indices = [
        Index(value = ["employeeId"]),
        Index(value = ["taskId"])
    ]
)
data class EmployeeTaskCrossRefEntity(
    val employeeId: Int,
    val taskId: Int,
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow()
)