package dev.redcom1988.hermes.domain.service

class ServiceType(
    val id: Int,
    val name: String,
    val description: String?,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String
)