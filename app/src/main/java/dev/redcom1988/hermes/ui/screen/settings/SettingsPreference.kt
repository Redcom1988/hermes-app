package dev.redcom1988.hermes.ui.screen.settings

import android.icu.util.Currency
import dev.redcom1988.hermes.core.preference.PreferenceStore
import dev.redcom1988.hermes.core.preference.getEnum
import java.util.Locale

class SettingsPreference(
    private val preferenceStore: PreferenceStore,
) {

    fun defaultCurrencyCode() = preferenceStore.getString(
        key = "profile_preference_default_currency",
        defaultValue = Currency.getInstance(Locale.US).currencyCode
    )

    fun defaultNotificationNotifyPush() = preferenceStore.getBoolean(
        key = "profile_preference_default_notification_notify_push",
        defaultValue = true
    )

    fun defaultNotificationNotifyEmail() = preferenceStore.getBoolean(
        key = "profile_preference_default_notification_notify_email",
        defaultValue = false
    )

    fun defaultNotificationOffset() = preferenceStore.getEnum(
        key = "profile_preference_default_notification_offset",
        defaultValue = NotificationOffset.DAY_1_BEFORE
    )

}

enum class NotificationOffset(val label: String, val millisBefore: Long) {
    AT_PAYMENT_DATE("At payment date", 0L),
    MINUTES_15_BEFORE("15 minutes before", 15 * 60 * 1000L),
    HOUR_1_BEFORE("1 hour before", 1 * 60 * 60 * 1000L),
    HOURS_2_BEFORE("2 hours before", 2 * 60 * 60 * 1000L),
    DAY_1_BEFORE("1 day before", 1 * 24 * 60 * 60 * 1000L),
    DAYS_2_BEFORE("2 days before", 2 * 24 * 60 * 60 * 1000L),
    WEEK_1_BEFORE("1 week before", 7 * 24 * 60 * 60 * 1000L);

    override fun toString(): String = label
}
