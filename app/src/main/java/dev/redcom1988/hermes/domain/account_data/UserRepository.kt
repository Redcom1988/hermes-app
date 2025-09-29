package dev.redcom1988.hermes.domain.account_data

import dev.redcom1988.hermes.domain.account_data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getVisibleUsers(): Flow<List<User>>

    suspend fun getUserById(id: Int): User?

}