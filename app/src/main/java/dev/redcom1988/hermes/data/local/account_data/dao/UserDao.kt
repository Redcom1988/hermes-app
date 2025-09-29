package dev.redcom1988.hermes.data.local.account_data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.data.local.account_data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE isDeleted = 0")
    fun getVisibleUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(user: List<UserEntity>)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: Int): UserEntity?

    @Transaction
    suspend fun upsertUser(user: UserEntity) {
        val existingUser = getUserById(user.userId)
        if (existingUser != null) {
            updateUser(user)
        } else {
            insertUser(user)
        }
    }

    @Transaction
    suspend fun upsertUsers(users: List<UserEntity>) {
        users.forEach { user ->
            upsertUser(user)
        }
    }

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

}