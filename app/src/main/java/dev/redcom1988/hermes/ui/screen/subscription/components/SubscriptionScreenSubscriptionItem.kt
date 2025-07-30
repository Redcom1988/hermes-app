package dev.redcom1988.hermes.ui.screen.subscription.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.AllIcons
import compose.icons.fontawesomeicons.Brands
import dev.redcom1988.hermes.domain.subscription.model.Subscription
import dev.redcom1988.hermes.ui.util.getLocale
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SubscriptionScreenSubscriptionItem(
    subscription: Subscription,
    onClick: () -> Unit,
    onMarkAsPaid: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val numberFormat = NumberFormat.getCurrencyInstance(subscription.currency.getLocale())
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeInterval = when {
        subscription.billingValue == 1 -> when(subscription.billingUnit) {
            Subscription.TimeUnit.DAYS -> "Daily"
            Subscription.TimeUnit.WEEKS -> "Weekly"
            Subscription.TimeUnit.MONTHS -> "Monthly"
            Subscription.TimeUnit.YEARS -> "Annually"
        }
        subscription.billingUnit == Subscription.TimeUnit.DAYS -> {
            when (subscription.billingValue) {
                7 -> "Weekly"
                30 -> "Monthly"
                365 -> "Annually"
                else -> "Every ${subscription.billingValue} ${subscription.billingUnit.label}"
            }
        }
        else -> "Every ${subscription.billingValue} ${subscription.billingUnit.label}"
    }

    OutlinedCard(
        modifier = modifier,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 4.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = numberFormat.format(subscription.price),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = " / $timeInterval",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                        )
                    }
                }
                val image = FontAwesomeIcons.Brands.AllIcons.firstOrNull { it.name == subscription.logo }
                image?.let {
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = null,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Next payment date at ${dateFormat.format(subscription.nextPaymentDate)}", // TODO
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMarkAsPaid() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Mark as paid",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

        }
    }
}