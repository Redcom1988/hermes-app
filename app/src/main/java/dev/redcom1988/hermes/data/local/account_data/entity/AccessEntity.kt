package dev.redcom1988.hermes.data.local.account_data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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