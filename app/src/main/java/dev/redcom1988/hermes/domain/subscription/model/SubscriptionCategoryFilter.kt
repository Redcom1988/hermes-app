package dev.redcom1988.hermes.domain.subscription.model

data class SubscriptionCategoryFilter(
    val isChecked: Boolean,
    val categoryId: Int,
)

object SubscriptionCategoryFilterSerializer {

    fun serialize(filter: SubscriptionCategoryFilter): String {
        return "${if (filter.isChecked) "1" else "0"}|${filter.categoryId}"
    }

    fun deserialize(value: String): SubscriptionCategoryFilter {
        val parts = value.split("|")
        require(parts.size == 2) { "Invalid format for SubscriptionCategoryFilter: $value" }

        val isChecked = when (parts[0]) {
            "1" -> true
            "0" -> false
            else -> throw IllegalArgumentException("Invalid boolean value: ${parts[0]}")
        }

        val categoryId = parts[1].toIntOrNull()
            ?: throw IllegalArgumentException("Invalid long value: ${parts[1]}")

        return SubscriptionCategoryFilter(isChecked, categoryId)
    }
}
