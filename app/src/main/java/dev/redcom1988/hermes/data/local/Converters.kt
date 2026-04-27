package dev.redcom1988.hermes.data.local

import androidx.room.TypeConverter
import dev.redcom1988.hermes.domain.task.TaskStatus

class Converters {
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.label
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus {
        return TaskStatus.fromLabel(value) ?: TaskStatus.PENDING
    }
}