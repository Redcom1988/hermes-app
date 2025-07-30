package dev.redcom1988.hermes.domain.attendance

import dev.redcom1988.hermes.domain.task.Task

data class AttendanceWithTask(
    val attendance: Attendance,
    val tasks: List<Task>
)