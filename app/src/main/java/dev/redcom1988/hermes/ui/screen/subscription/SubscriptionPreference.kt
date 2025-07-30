package dev.redcom1988.hermes.ui.screen.subscription

import dev.redcom1988.hermes.core.preference.PreferenceStore
import dev.redcom1988.hermes.core.preference.getEnum
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionPaymentDate
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionSortType
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionStatus

class SubscriptionPreference(
    private val preferenceStore: PreferenceStore,
) {

    fun statusFilter() = preferenceStore.getEnum(
        key = "${this::class.simpleName}_subscription_status",
        defaultValue = SubscriptionStatus.All,
    )

    fun paymentDateFilter() = preferenceStore.getEnum(
        key = "${this::class.simpleName}_subscription_payment_date",
        defaultValue = SubscriptionPaymentDate.All,
    )

    fun categoryFilters() = preferenceStore.getStringSet(
        key = "${this::class.simpleName}_subscription_categories",
        defaultValue = emptySet()
    )

    fun includeUncategorizedFilter() = preferenceStore.getBoolean(
        key = "${this::class.simpleName}_include_uncategorized",
        defaultValue = true
    )

    fun sortAscending() = preferenceStore.getBoolean(
        key = "${this::class.simpleName}_sort_ascending",
        defaultValue = true
    )

    fun sortType() = preferenceStore.getEnum(
        key = "${this::class.simpleName}_sort_type",
        defaultValue = SubscriptionSortType.Alphabetically
    )

}