package dev.redcom1988.hermes.domain.auth

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
    fun getCurrentUserRole(): String
}