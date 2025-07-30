package dev.redcom1988.hermes.data.local.user

import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.remote.api.UserApi
import dev.redcom1988.hermes.data.remote.model.UserDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.user.User
import dev.redcom1988.hermes.domain.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val api: UserApi
) : UserRepository {

    override fun getUsersFlow(): Flow<List<User>> {
        return userDao.getVisibleUsersFlow()
            .map { list -> list.map { it.toDomain() }
        }
    }

    override suspend fun syncUsersFromServer() {
        val response = api.getUsers()
        if (response.isSuccessful) {
            val users = response.parseAs<List<UserDto>>().map { it.toDomain().toEntity() }
            userDao.insertUsers(users)
        }
    }

    override suspend fun clearLocalUsers() {
        userDao.deleteAllUsers()
    }

}