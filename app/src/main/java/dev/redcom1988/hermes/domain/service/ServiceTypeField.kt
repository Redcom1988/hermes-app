package dev.redcom1988.hermes.domain.service

class ServiceTypeField(
    val id: Int,
    val serviceTypeId: Int,
    val fieldName: String,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String
)