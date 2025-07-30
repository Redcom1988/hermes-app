package dev.redcom1988.hermes.data.local.auth

import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.remote.api.AuthApi
import dev.redcom1988.hermes.data.remote.model.LoginResponseDto
import dev.redcom1988.hermes.data.remote.model.LogoutResponseDto
import dev.redcom1988.hermes.data.remote.model.RefreshTokenResponseDto
import dev.redcom1988.hermes.core.auth.AuthPreference
import dev.redcom1988.hermes.data.local.user.UserDao
import dev.redcom1988.hermes.domain.auth.AuthRepository
import java.text.SimpleDateFormat
import java.util.*

class AuthRepositoryImpl (
    private val authApi: AuthApi,
    private val authPreference: AuthPreference
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            val response = authApi.login(email, password)

            if (response.isSuccessful) {
                val loginResponse = response.parseAs<LoginResponseDto>()

                if (loginResponse.success && loginResponse.data != null) {
                    val userData = loginResponse.data

                    // Parse expiry date
                    val expiresAt = parseExpiryDate(userData.expires_at)

                    // Save to preferences
                    authPreference.saveLoginData(
                        userId = userData.user.id,
                        userEmail = userData.user.email,
                        role = userData.user.role,
                        authToken = userData.token,
                        tokenExpiresAt = expiresAt,
                        lastLoginAt = System.currentTimeMillis().toString()
                    )

                    Result.success(true)
                } else {
                    Result.failure(Exception(loginResponse.message))
                }
            } else {
                val errorResponse = response.parseAs<LoginResponseDto>()
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Boolean> {
        return try {
            val response = authApi.logout()

            if (response.isSuccessful) {
                val logoutResponse = response.parseAs<LogoutResponseDto>()
                authPreference.clearLoginData()
                Result.success(logoutResponse.success)
            } else {
                // Clear local data even if server logout fails
                authPreference.clearLoginData()
                Result.success(true)
            }
        } catch (e: Exception) {
            // Clear local data even on network error
            authPreference.clearLoginData()
            Result.success(true)
        }
    }

    override suspend fun refreshToken(): Result<String> {
        return try {
            val currentToken = authPreference.authToken().get()

            if (currentToken.isEmpty()) {
                return Result.failure(Exception("No token to refresh"))
            }

            val response = authApi.refreshToken()

            if (response.isSuccessful) {
                val refreshResponse = response.parseAs<RefreshTokenResponseDto>()

                if (refreshResponse.success && refreshResponse.data != null) {
                    val newToken = refreshResponse.data.token
                    val expiresAt = parseExpiryDate(refreshResponse.data.expires_at)

                    // Update token in preferences
                    authPreference.authToken().set(newToken)
                    authPreference.tokenExpiresAt().set(expiresAt)

                    Result.success(newToken)
                } else {
                    Result.failure(Exception(refreshResponse.message))
                }
            } else {
                val errorResponse = response.parseAs<RefreshTokenResponseDto>()
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Boolean = authPreference.isTokenValid()

    override fun getCurrentUserId(): Int = authPreference.userId().get()

    override fun getAuthToken(): String = authPreference.authToken().get()

    override fun getCurrentUserEmail(): String = authPreference.userEmail().get()

    override fun getCurrentUserRole(): String = authPreference.role().get()

    private fun parseExpiryDate(dateString: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            // Default to 30 days from now if parsing fails
            System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L)
        }
    }
}