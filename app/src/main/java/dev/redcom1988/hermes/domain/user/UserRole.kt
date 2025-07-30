package dev.redcom1988.hermes.domain.user

enum class UserRole (val label: String) {
    ADMIN("admin"),
    USER("user");

    companion object {
        fun fromLabel(label: String): UserRole? {
            return entries.firstOrNull() { it.label.equals(label, ignoreCase = true) }
        }
    }
}