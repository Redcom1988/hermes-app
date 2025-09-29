package dev.redcom1988.hermes.domain.service

class ServiceTypeDataCrossRef(
    val fieldId: Int,
    val serviceId: Int,
    val value: String,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String
)