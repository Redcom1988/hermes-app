package dev.redcom1988.hermes.domain.workhour_plan

import dev.redcom1988.hermes.data.local.workhour_plan.entity.WorkhourPlanEntity
import dev.redcom1988.hermes.domain.common.WorkLocation
import kotlinx.coroutines.flow.Flow

interface WorkhourPlanRepository {
    suspend fun addPlan(
        employeeId: Int,
        planDate: String,
        plannedStartTime: String,
        plannedEndTime: String,
        workLocation: WorkLocation
    ): Int
    fun getVisiblePlans(): Flow<List<WorkhourPlan>>
    suspend fun update(plan: WorkhourPlan)
    suspend fun softDeletePlan(planId: Int)
    suspend fun insertPlans(plans: List<WorkhourPlan>)
    suspend fun getPendingSyncPlans(): List<WorkhourPlan>
    suspend fun deleteAllPlans()
//    suspend fun syncPlans()
}