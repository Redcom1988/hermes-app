package dev.redcom1988.hermes.domain.account_data.model

class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val isDeleted: Boolean,
    val updatedAt: String,
    val createdAt: String
)