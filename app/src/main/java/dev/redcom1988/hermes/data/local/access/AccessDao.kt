package dev.redcom1988.hermes.data.local.access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Update
import dev.redcom1988.hermes.data.local.access.entity.AccessEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessDao {

    @Query("SELECT * FROM accesses")
    fun getAllAccesses(): List<AccessEntity>

    @Query("SELECT * FROM accesses WHERE isDeleted = 0")
    fun getVisibleAccessesFlow(): Flow<List<AccessEntity>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAccesses(accesses: List<AccessEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccesses(accesses: List<AccessEntity>)

    @Query("DELETE FROM accesses")
    suspend fun deleteAllAccesses()
}
