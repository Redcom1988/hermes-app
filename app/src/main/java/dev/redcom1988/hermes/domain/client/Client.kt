package dev.redcom1988.hermes.domain.client

class Client(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String
)