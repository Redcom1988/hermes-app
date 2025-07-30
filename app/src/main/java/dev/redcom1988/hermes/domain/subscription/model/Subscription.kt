package dev.redcom1988.hermes.domain.subscription.model

import dev.redcom1988.hermes.domain.category.model.Category
import java.util.Currency
import java.util.Date

data class Subscription(
    var id: Int,
    var name: String,
    var active: Boolean,
    var logo: String?,
    var price: Double,
    var currency: Currency,
    var nextPaymentDate: Long,
    var billingValue: Int,
    var billingUnit: TimeUnit,
    var url: String?,
    var notes: String?,
    var categories: List<Category>,
    var notifications: List<Notification>,
    var deleted: Boolean,
    var createdAt: Long,
    var updatedAt: Long,
) {
    data class Notification(
        val id: Int,
        val subscriptionId: Int,
        val notifyPush: Boolean,
        val notifyEmail: Boolean,
        val offsetValue: Int,
        val offsetUnit: TimeUnit
    )
    enum class TimeUnit(val label: String) {
        DAYS("Days"),
        WEEKS("Weeks"),
        MONTHS("Months"),
        YEARS("Years")
    }

    companion object {
        fun create(): Subscription {
            return Subscription(
                id = 0,
                name = "",
                active = true,
                logo = null,
                price = 0.0,
                currency = Currency.getInstance("IDR"),
                nextPaymentDate = Date().time,
                billingValue = 0,
                billingUnit = TimeUnit.DAYS,
                url = null,
                notes = null,
                categories = emptyList(),
                notifications = emptyList(),
                deleted = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}