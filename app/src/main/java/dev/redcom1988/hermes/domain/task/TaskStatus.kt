package dev.redcom1988.hermes.domain.task

enum class TaskStatus(val label: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    ISSUE("Issue"),
    CANCELLED("Cancelled");

    companion object {
        fun fromLabel(label: String): TaskStatus? {
            return entries.firstOrNull() { it.label.equals(label, ignoreCase = true) }
        }
    }
}