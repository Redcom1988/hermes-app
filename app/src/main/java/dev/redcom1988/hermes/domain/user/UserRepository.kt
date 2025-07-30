package dev.redcom1988.hermes.domain.user

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsersFlow(): Flow<List<User>>
    suspend fun syncUsersFromServer()
    suspend fun clearLocalUsers()
}