package dev.redcom1988.hermes.data.local.division

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.redcom1988.hermes.data.local.division.entity.DivisionEntity
import dev.redcom1988.hermes.data.local.division.entity.DivisionWithAccesses
import kotlinx.coroutines.flow.Flow

@Dao
interface DivisionDao {

    @Query("SELECT * FROM divisions")
    suspend fun getAllDivisions(): List<DivisionEntity>

    @Query("SELECT * FROM divisions WHERE isDeleted = 0")
    fun getVisibleDivisionsFlow(): Flow<List<DivisionEntity>>

    @Transaction
    @Query("SELECT * FROM divisions WHERE isDeleted = 0")
    fun getVisibleDivisionsWithAccessesFlow(): Flow<List<DivisionWithAccesses>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDivisions(divisions: List<DivisionEntity>)

    @Query("DELETE FROM divisions")
    suspend fun deleteAllDivisions()

}