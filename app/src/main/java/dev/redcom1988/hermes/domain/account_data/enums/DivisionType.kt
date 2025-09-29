package dev.redcom1988.hermes.domain.account_data.enums


enum class DivisionType(val label: String) {
    PM("Project Manager"),
    DEV("Developer");

    companion object {
        fun fromLabel(label: String): DivisionType? {
            return entries.firstOrNull { it.label.equals(label, ignoreCase = true) }
        }
    }
}