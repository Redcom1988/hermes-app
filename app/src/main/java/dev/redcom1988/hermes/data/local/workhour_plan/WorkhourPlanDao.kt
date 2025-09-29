package dev.redcom1988.hermes.data.local.workhour_plan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.workhour_plan.entity.WorkhourPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkhourPlanDao {

    @Query("SELECT * FROM workhour_plans")
    suspend fun getAllWorkhourPlans(): List<WorkhourPlanEntity>

    @Query("SELECT * FROM workhour_plans WHERE isDeleted = 0")
    fun getVisibleWorkhourPlans(): Flow<List<WorkhourPlanEntity>>

    @Query("SELECT MIN(planId) FROM workhour_plans WHERE planId < 0")
    suspend fun getMinTempPlanId(): Int?

    @Query("SELECT * FROM workhour_plans WHERE planId = :id")
    suspend fun getWorkhourPlanById(id: Int): WorkhourPlanEntity?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertWorkhourPlan(plan: WorkhourPlanEntity)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertWorkhourPlans(plans: List<WorkhourPlanEntity>)

    @Update
    suspend fun updateWorkhourPlan(plan: WorkhourPlanEntity)

    @Transaction
    suspend fun upsertPlan(plan: WorkhourPlanEntity) {
        val existing = getWorkhourPlanById(plan.planId)
        if (existing != null) {
            updateWorkhourPlan(plan)
        } else {
            insertWorkhourPlan(plan)
        }
    }

    @Transaction
    suspend fun upsertRemotePlan(remote: WorkhourPlanEntity) {
        val existing = getWorkhourPlanById(remote.planId)
        if (existing != null) {
            updateWorkhourPlan(remote)
        } else {
            insertWorkhourPlan(remote)
        }
    }

    @Transaction
    suspend fun upsertPlans(plans: List<WorkhourPlanEntity>) {
        plans.forEach { plan ->
            upsertRemotePlan(plan)
        }
    }

    @Query("UPDATE workhour_plans SET isSynced = 0, isDeleted = 1, updatedAt = :updatedAt WHERE planId = :id")
    suspend fun softDeleteWorkhourPlanById(id: Int, updatedAt: String)

    @Query("SELECT * FROM workhour_plans WHERE isSynced = 0")
    suspend fun getPendingSyncWorkhourPlans(): List<WorkhourPlanEntity>

    @Query("DELETE FROM workhour_plans")
    suspend fun deleteAllWorkhourPlans()

}