package dev.redcom1988.hermes.data.local.account_data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.data.local.account_data.entity.DivisionAccessCrossRefEntity
import dev.redcom1988.hermes.data.local.account_data.entity.DivisionEntity
import dev.redcom1988.hermes.data.local.account_data.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlin.collections.forEach

@Dao
interface DivisionDao {

    @Query("SELECT * FROM divisions")
    suspend fun getAllDivisions(): List<DivisionEntity>

    @Query("SELECT * FROM division_accesses")
    suspend fun getAllDivisionAccesses(): List<DivisionAccessCrossRefEntity>

    @Query("SELECT * FROM divisions WHERE isDeleted = 0")
    fun getVisibleDivisions(): Flow<List<DivisionEntity>>

    @Query("SELECT * FROM division_accesses WHERE isDeleted = 0")
    fun getVisibleDivisionAccesses(): Flow<List<DivisionAccessCrossRefEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDivision(division: DivisionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDivisions(division: List<DivisionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDivisionAccess(divisionAccess: DivisionAccessCrossRefEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDivisionAccesses(divisionAccesses: List<DivisionAccessCrossRefEntity>)

    @Query("SELECT * FROM divisions WHERE divisionId = :divisionId")
    suspend fun getDivisionById(divisionId: Int): DivisionEntity?

    @Query("SELECT * FROM divisions WHERE divisionName = :divisionName")
    fun getDivisionByName(divisionName: String): DivisionEntity?

    @Query("SELECT * FROM division_accesses WHERE divisionId = :divisionId AND accessId = :accessId")
    suspend fun getDivisionAccessById(divisionId: Int, accessId: Int): DivisionAccessCrossRefEntity?

    @Transaction
    suspend fun upsertRemoteDivision(remote: DivisionEntity) {
        val existing = getDivisionById(remote.divisionId)
        if (existing != null) {
            updateDivision(remote)
        } else {
            insertDivision(remote)
        }
    }

    @Transaction
    suspend fun upsertDivisions(divisions: List<DivisionEntity>) {
        divisions.forEach { division ->
            upsertRemoteDivision(division)
        }
    }

    @Transaction
    suspend fun upsertRemoteDivisionAccess(remote: DivisionAccessCrossRefEntity) {
        val existing = getDivisionAccessById(remote.divisionId, remote.accessId)
        if (existing != null) {
            updateDivisionAccess(remote)
        } else {
            insertDivisionAccess(remote)
        }
    }

    @Transaction
    suspend fun upsertDivisionAccesses(divisionAccesses: List<DivisionAccessCrossRefEntity>) {
        divisionAccesses.forEach { divisionAccess ->
            upsertRemoteDivisionAccess(divisionAccess)
        }
    }

    @Update
    suspend fun updateDivision(division: DivisionEntity)

    @Update
    suspend fun updateDivisionAccess(divisionAccess: DivisionAccessCrossRefEntity)

    @Query("DELETE FROM divisions")
    suspend fun deleteAllDivisions()

    @Query("DELETE FROM division_accesses")
    suspend fun deleteAllDivisionAccesses()

}