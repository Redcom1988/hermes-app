package dev.redcom1988.hermes.domain.user

class User (
    val id: Int,
    val email: String,
    val role: UserRole,
    val isDeleted: Boolean,
    val updatedAt: String,
    val createdAt: String
)