package dev.redcom1988.hermes.domain.service

class Service (
    val id: Int,
    val clientId: Int,
    val serviceTypeId: Int,
    val status: String,
    val servicePrice: Int,
    val startTime: String,
    val expireTime: String,
    val isDeleted: Boolean,
    val updatedAt: String,
    val createdAt: String
)