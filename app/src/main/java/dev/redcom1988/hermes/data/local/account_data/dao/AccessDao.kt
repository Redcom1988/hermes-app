package dev.redcom1988.hermes.data.local.account_data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.data.local.account_data.entity.AccessEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessDao {

    @Query("SELECT * FROM accesses")
    fun getAllAccesses(): List<AccessEntity>

    @Query("SELECT * FROM accesses WHERE isDeleted = 0")
    fun getVisibleAccesses(): Flow<List<AccessEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccess(access: AccessEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccesses(accesses: List<AccessEntity>)

    @Query("SELECT * FROM accesses WHERE accessId = :accessId")
    suspend fun getAccessById(accessId: Int): AccessEntity?

    @Transaction
    suspend fun upsertAccess(access: AccessEntity) {
        val existing = getAccessById(access.accessId)
        if (existing != null) {
            updateAccess(access)
        } else {
            insertAccess(access)
        }
    }

    @Transaction
    suspend fun upsertAccesses(accesses: List<AccessEntity>) {
        accesses.forEach { access ->
            upsertAccess(access)
        }
    }

    @Update
    suspend fun updateAccess(access: AccessEntity)


    @Query("DELETE FROM accesses")
    suspend fun deleteAllAccesses()
}
