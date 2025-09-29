package dev.redcom1988.hermes.domain.auth

import dev.redcom1988.hermes.data.remote.model.LoginDataDto
import dev.redcom1988.hermes.data.remote.model.responses.LoginResponseDto
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.account_data.enums.UserRole

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String
    ): Result<Boolean>

    suspend fun logout(): Result<Boolean>
    suspend fun refreshToken(): Result<String>
    fun isLoggedIn(): Boolean
    fun getCurrentUserId(): Int
    fun getAuthToken(): String
    fun getCurrentUserEmail(): String
    fun getCurrentEmployeeId(): Int
    fun getCurrentUserRole(): UserRole?
    fun getCurrentDivision(): DivisionType?
    fun getLastSyncTime(): String
}