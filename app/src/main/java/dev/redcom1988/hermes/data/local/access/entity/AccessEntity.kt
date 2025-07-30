package dev.redcom1988.hermes.data.local.access.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.data.local.task.entity.taskEntityTableName

const val accessEntityTableName = "accesses"

@Entity(tableName = accessEntityTableName)
data class AccessEntity(
    @PrimaryKey val accessId: Int,
    val name: String,
    val description: String,
    val isDeleted: Boolean,
    val updatedAt: String,
    val createdAt: String
)