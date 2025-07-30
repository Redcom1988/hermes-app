package dev.redcom1988.hermes.domain.subscription.model

enum class SubscriptionPaymentDate(val label: String) {
    All("All"),
    ThisMonth("This Month"),
    NextMonth("Next Month"),
}