package dev.redcom1988.hermes.core.util

import androidx.room.TypeConverter
import dev.redcom1988.hermes.domain.common.SyncStatus

class SyncStatusConverter {
    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}