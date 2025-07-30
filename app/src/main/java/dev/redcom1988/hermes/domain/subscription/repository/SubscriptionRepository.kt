package dev.redcom1988.hermes.domain.subscription.repository

import dev.redcom1988.hermes.domain.subscription.model.Subscription
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionCategoryFilter
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionPaymentDate
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionSortType
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionStatus
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getSubscriptionsFlow(
        includeDeleted: Boolean = false,
        includeUncategorized: Boolean,
        statusFilter: SubscriptionStatus = SubscriptionStatus.All,
        paymentDateFilter: SubscriptionPaymentDate = SubscriptionPaymentDate.All,
        categoryFilters: List<SubscriptionCategoryFilter> = emptyList(),
        searchQuery: String? = null,
        sortType: SubscriptionSortType = SubscriptionSortType.Alphabetically,
        sortAscending: Boolean = true,
    ): Flow<List<Subscription>>
    suspend fun getSubscriptionById(id: Int): Subscription
    suspend fun addSubscription(subscription: Subscription)
    suspend fun updateSubscription(
        subscription: Subscription,
        updateCategory: Boolean = true,
        updateNotification: Boolean = true,
    )
    suspend fun markSubscriptionAsPaidById(subscriptionId: Int)
    suspend fun deleteSubscription(subscriptionId: Int, softDelete: Boolean = true)
}