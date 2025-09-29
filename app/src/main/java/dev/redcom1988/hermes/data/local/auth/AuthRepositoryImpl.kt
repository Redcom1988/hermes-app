package dev.redcom1988.hermes.data.local.auth

import android.util.Log
import androidx.room.withTransaction
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.local.HermesDatabase
import dev.redcom1988.hermes.data.local.account_data.dao.DivisionDao
import dev.redcom1988.hermes.data.local.account_data.dao.EmployeeDao
import dev.redcom1988.hermes.data.local.account_data.dao.UserDao
import dev.redcom1988.hermes.data.local.account_data.entity.DivisionEntity
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeEntity
import dev.redcom1988.hermes.data.local.account_data.entity.UserEntity
import dev.redcom1988.hermes.data.local.account_data.mapper.toEntity
import dev.redcom1988.hermes.data.remote.api.AuthApi
import dev.redcom1988.hermes.data.remote.model.requests.LoginRequestDto
import dev.redcom1988.hermes.data.remote.model.responses.LoginResponseDto
import dev.redcom1988.hermes.data.remote.model.responses.LogoutResponseDto
import dev.redcom1988.hermes.data.remote.model.responses.RefreshTokenResponseDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.account_data.enums.UserRole
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.domain.auth.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val syncRepository: SyncRepository,
    private val authPreference: AuthPreference,
    private val userPreference: UserPreference,
    private val db: HermesDatabase
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))

            Log.d("ASD", "Login response: $response")
            if (response.isSuccessful) {
                val loginResponse = response.parseAs<LoginResponseDto>()
                Log.d("ASD", "Parsed login response: $loginResponse")

                if (loginResponse.success && loginResponse.data != null) {
                    val userData = loginResponse.data
                    val expiresAt = parseExpiryDate(userData.expiresAt)

                    try {
                        syncRepository.performSync(
                            lastSyncTime = "",
                            forceClearDataOverride = true
                        )

                        authPreference.saveLoginData(
                            userId = userData.user.id,
                            userEmail = userData.user.email,
                            authToken = userData.token,
                            tokenExpiresAt = expiresAt,
                            lastLoginAt = System.currentTimeMillis().toString()
                        )

                        userPreference.saveUserData(
                            name = userData.employee?.name ?: "",
                            role = userData.user.role,
                            employeeId = userData.employee?.id ?: 0,
                            divisionType = userData.division?.name ?: "",
                        )

                        Log.d("ASD", "Saved division type: ${userData.division?.name ?: ""}")

                        Log.d("ASD", "Login + sync successful")
                        Result.success(true)
                    } catch (syncEx: Exception) {
                        Log.e("ASD", "Initial sync failed after login", syncEx)
                        Result.failure(syncEx)
                    }
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
            authPreference.clearLoginData()
            userPreference.clearUserData()

            withContext(Dispatchers.IO) {
                db.clearAllTables()
            }

            try {
                val response = authApi.logout()
                if (response.isSuccessful) {
                    Log.d("Auth", "Server logout successful")
                } else {
                    Log.w("Auth", "Server logout failed but local data cleared")
                }
            } catch (networkError: Exception) {
                Log.w("Auth", "Offline logout - server not notified: ${networkError.message}")
            }

            Log.d("Auth", "Logout completed - all local data cleared")
            Result.success(true)

        } catch (localError: Exception) {
            Log.e("Auth", "Failed to clear local data during logout", localError)
            Result.failure(localError)
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
                    val expiresAt = parseExpiryDate(refreshResponse.data.expiresAt)

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

    override fun getCurrentEmployeeId(): Int = userPreference.employeeId().get()

    override fun getCurrentDivision(): DivisionType? = userPreference.getDivisionType()

    override fun getCurrentUserRole(): UserRole? = userPreference.getUserRole()

    override fun getLastSyncTime(): String = userPreference.lastSyncTime().get()

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