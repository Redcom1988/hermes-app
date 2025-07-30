package dev.redcom1988.hermes.ui.screen.subscription.notifier

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import dev.redcom1988.hermes.R
import dev.redcom1988.hermes.core.notification.NotificationHelper
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.subscription.model.Subscription
import dev.redcom1988.hermes.ui.main.MainActivity
import dev.redcom1988.hermes.ui.util.formatDate

object SubscriptionNotifier {
    const val SUBSCRIPTION_NOTIFICATION_ID = 1000
    const val SUBSCRIPTION_NOTIFICATION_CHANNEL_ID = "SUBSCRIPTION_CHANNEL"
    const val SUBSCRIPTION_NOTIFICATION_BUNDLE = "SUBSCRIPTION_NOTIFICATION_BUNDLE"
    const val SUBSCRIPTION_ID = "SUBSCRIPTION_ID"
    private val notificationHelper by injectLazy<NotificationHelper>()

    fun createNotificationChannel(): NotificationHelper.Channel {
        return NotificationHelper.Channel(
            id = SUBSCRIPTION_NOTIFICATION_CHANNEL_ID,
            name = "Subscription Reminder",
            description = "Get a reminder for upcoming due subscription payments",
        )
    }
    
    fun notify(
        context: Context, 
        subscription: Subscription
    ) {
        val markAsPaidPendingIntent = notificationHelper.createActivityPendingIntent(
            requestCode = 0,
            activityClass = MainActivity::class.java,
            context = context,
            data = SUBSCRIPTION_NOTIFICATION_BUNDLE to bundleOf(
                SUBSCRIPTION_ID to subscription.id
            )
        )
        val notification = notificationHelper.createNotification(
            NotificationHelper.Data(
                channelId = SUBSCRIPTION_NOTIFICATION_CHANNEL_ID,
                title = "${subscription.name} is due at ${subscription.nextPaymentDate.formatDate()}",
                text = "",
                smallIconResId = R.drawable.ic_launcher_foreground, // TODO
                context = context,
                pendingIntent = markAsPaidPendingIntent,
                actions = listOf(
                    NotificationCompat.Action.Builder(
                        0,
                        "Mark as Paid",
                        markAsPaidPendingIntent
                    ).build()
                )
            )
        )
        notificationHelper.notify(
            notificationId = SUBSCRIPTION_NOTIFICATION_ID,
            notification = notification
        )
    }
    
}