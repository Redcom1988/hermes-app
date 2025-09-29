package dev.redcom1988.hermes.domain.client

import dev.redcom1988.hermes.core.util.extension.formattedNow

data class Client(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow()
)