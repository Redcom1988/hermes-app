package dev.redcom1988.hermes.data.local.account_data

import dev.redcom1988.hermes.data.local.account_data.dao.UserDao
import dev.redcom1988.hermes.data.local.account_data.mapper.toDomain
import dev.redcom1988.hermes.domain.account_data.UserRepository
import dev.redcom1988.hermes.domain.account_data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override fun getVisibleUsers(): Flow<List<User>> {
        return userDao.getVisibleUsers().map { it -> it.map { it.toDomain() } }
    }

    override suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)?.toDomain()
    }

}