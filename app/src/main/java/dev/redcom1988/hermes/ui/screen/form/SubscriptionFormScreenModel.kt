package dev.redcom1988.hermes.ui.screen.form

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.inject
import dev.redcom1988.hermes.domain.category.model.Category
import dev.redcom1988.hermes.domain.category.repository.CategoryRepository
import dev.redcom1988.hermes.domain.subscription.model.Subscription
import dev.redcom1988.hermes.domain.subscription.repository.SubscriptionRepository
import dev.redcom1988.hermes.ui.screen.settings.SettingsPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Currency

data class SubscriptionFormData(
    val type: SubscriptionFormScreenType,
    val name: String = "",
    val active: Boolean = true,
    val logo: String? = null,
    val price: String = "",
    val currency: Currency = Currency.getInstance(
        inject<SettingsPreference>().defaultCurrencyCode().get()
    ),
    val paymentDate: Long,
    val billingCycle: BillingCycle = BillingCycle.Monthly(),
    val url: String = "",
    val notes: String = "",
    val categories: List<Category> = emptyList(),
    val notifications: List<Subscription.Notification> = emptyList(),
)
sealed interface BillingCycle {
    val text: String
    data class Custom(
        val value: String,
        val unit: Subscription.TimeUnit,
        override val text: String = "Custom"
    ): BillingCycle
    data class Daily(override val text: String = "Daily"): BillingCycle
    data class Weekly(override val text: String = "Weekly"): BillingCycle
    data class Monthly(override val text: String = "Monthly"): BillingCycle
    data class Annually(override val text: String = "Annually"): BillingCycle
}

class SubscriptionFormScreenModel(
    private val initialFormData: SubscriptionFormData,
    private val subscriptionRepository: SubscriptionRepository = inject(),
    private val categoryRepository: CategoryRepository = inject(),
): ScreenModel {

    private val mutableState = MutableStateFlow(initialFormData)
    val state = mutableState.asStateFlow()

    val categories = categoryRepository.getCategoriesFlow()
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun updateState(
        block: (SubscriptionFormData) -> SubscriptionFormData
    ) = mutableState.update { block(state.value) }

    fun isChanged(subscriptionFormData: SubscriptionFormData): Boolean {
        return state.value != subscriptionFormData
    }

    fun handleDelete(
        onSuccess: () -> Unit
    ) = screenModelScope.launch {
        val type = state.value.type
        if (type is SubscriptionFormScreenType.Edit) {
            subscriptionRepository.deleteSubscription(type.subscription.id)
            onSuccess()
        }
    }

    fun handleSave(
        onSuccess: () -> Unit,
    ) = screenModelScope.launch {
        val state = state.value
        val type = state.type
        val price = state.price.toDoubleOrNull() ?: 0.0
        val url = state.url.ifBlank { null }
        val notes = state.notes.ifBlank { null }
        val (billingValue, billingUnit) = when(state.billingCycle) {
            is BillingCycle.Custom -> state.billingCycle.value.toInt() to state.billingCycle.unit
            is BillingCycle.Annually -> 1 to Subscription.TimeUnit.YEARS
            is BillingCycle.Daily -> 1 to Subscription.TimeUnit.DAYS
            is BillingCycle.Monthly -> 1 to Subscription.TimeUnit.MONTHS
            is BillingCycle.Weekly -> 1 to Subscription.TimeUnit.WEEKS
        }
        val subscription = when(type) {
            is SubscriptionFormScreenType.Add -> {
                Subscription.create().apply {
                    this.name = state.name
                    this.active = state.active
                    this.logo = state.logo
                    this.price = price
                    this.currency = Currency.getInstance(state.currency.currencyCode)
                    this.nextPaymentDate = state.paymentDate
                    this.billingValue = billingValue
                    this.billingUnit = billingUnit
                    this.url = url
                    this.notes = notes
                    this.categories = state.categories
                    this.notifications = state.notifications
                }
            }
            is SubscriptionFormScreenType.Edit -> type.subscription.copy(
                name = state.name,
                active = state.active,
                logo = state.logo,
                price = price,
                currency = Currency.getInstance(state.currency.currencyCode),
                nextPaymentDate = state.paymentDate,
                billingValue = billingValue,
                billingUnit = billingUnit,
                url = url,
                notes = notes,
                categories = state.categories,
                notifications = state.notifications,
                updatedAt = System.currentTimeMillis(),
            )
        }
        when(type) {
            is SubscriptionFormScreenType.Add -> {
                subscriptionRepository.addSubscription(subscription)
            }
            is SubscriptionFormScreenType.Edit -> {
                subscriptionRepository.updateSubscription(subscription)
            }
        }
        onSuccess()
    }

}

fun SubscriptionFormScreenType.toFormData(): SubscriptionFormData {
    return when(this) {
        is SubscriptionFormScreenType.Add -> {
            SubscriptionFormData(
                type = this,
                paymentDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            )
        }
        is SubscriptionFormScreenType.Edit -> {
            SubscriptionFormData(
                type = this,
                name = subscription.name,
                active = subscription.active,
                logo = subscription.logo,
                price = if (subscription.price == 0.0) "" else subscription.price.toString(),
                currency = subscription.currency,
                paymentDate = subscription.nextPaymentDate,
                billingCycle = when (subscription.billingUnit) {
                    Subscription.TimeUnit.DAYS -> {
                        when (subscription.billingValue) {
                            1 -> BillingCycle.Daily()
                            7 -> BillingCycle.Weekly()
                            30 -> BillingCycle.Monthly()
                            365 -> BillingCycle.Annually()
                            else -> BillingCycle.Custom(
                                subscription.billingValue.toString(),
                                Subscription.TimeUnit.DAYS
                            )
                        }
                    }
                    Subscription.TimeUnit.WEEKS -> {
                        when(subscription.billingValue) {
                            1 -> BillingCycle.Weekly()
                            else -> BillingCycle.Custom(
                                subscription.billingValue.toString(),
                                Subscription.TimeUnit.WEEKS
                            )
                        }
                    }
                    Subscription.TimeUnit.MONTHS -> {
                        when(subscription.billingValue) {
                            1 -> BillingCycle.Monthly()
                            else -> BillingCycle.Custom(
                                subscription.billingValue.toString(),
                                Subscription.TimeUnit.MONTHS
                            )
                        }
                    }
                    Subscription.TimeUnit.YEARS -> {
                        when(subscription.billingValue) {
                            1 -> BillingCycle.Annually()
                            else -> BillingCycle.Custom(
                                subscription.billingValue.toString(),
                                Subscription.TimeUnit.YEARS
                            )
                        }
                    }
                },
                url = subscription.url.orEmpty(),
                notes = subscription.notes.orEmpty(),
                categories = subscription.categories,
                notifications = subscription.notifications,
            )
        }
    }
}
