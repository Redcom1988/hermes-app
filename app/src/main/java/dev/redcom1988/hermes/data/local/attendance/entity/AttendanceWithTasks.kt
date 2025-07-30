package dev.redcom1988.hermes.data.local.attendance.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.redcom1988.hermes.data.local.attendance.toDomain
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.data.local.task.toDomain
import dev.redcom1988.hermes.domain.attendance.AttendanceWithTask

data class AttendanceWithTasks(
    @Embedded val attendance: AttendanceEntity,
    @Relation(
        parentColumn = "attendanceId",
        entityColumn = "taskId",
        associateBy = Junction(AttendanceTaskCrossRef::class)
    )
    val tasks: List<TaskEntity>
)

fun AttendanceWithTasks.toDomain(): AttendanceWithTask {
    return AttendanceWithTask(
        attendance = this.attendance.toDomain(),
        tasks = this.tasks.map { it.toDomain() }
    )
}