package dev.redcom1988.hermes.domain.employee

class Employee (
    val id: Int,
    val userId: Int,
    val divisionId: Int,
    val fullName: String,
    val phoneNumber: String,
    val gender: String,
    val birthDate: String,
    val address: String,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String,
)