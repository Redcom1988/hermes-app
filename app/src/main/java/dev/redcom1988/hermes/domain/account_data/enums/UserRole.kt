package dev.redcom1988.hermes.domain.account_data.enums

enum class UserRole(val label: String) {
    ADMIN("admin"),
    USER("user");

    companion object {
        fun fromLabel(label: String): UserRole? {
            return entries.firstOrNull() { it.label.equals(label, ignoreCase = true) }
        }
    }
}