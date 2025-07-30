package dev.redcom1988.hermes.ui.screen.category

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.preference.getAndSet
import dev.redcom1988.hermes.core.util.extension.inject
import dev.redcom1988.hermes.domain.category.model.Category
import dev.redcom1988.hermes.domain.category.repository.CategoryRepository
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionCategoryFilter
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionCategoryFilterSerializer
import dev.redcom1988.hermes.ui.screen.subscription.SubscriptionPreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryScreenModel(
    private val categoryRepository: CategoryRepository = inject(),
    private val subscriptionPreference: SubscriptionPreference = inject()
): ScreenModel {

    val categories = categoryRepository.getCategoriesFlow()
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun addCategory(
        name: String,
    ) {
        screenModelScope.launch {
            val newId = categoryRepository.addCategory(
                name = name,
                colorHex = Color.Transparent.toArgb() // TODO implement color
            )
            subscriptionPreference.categoryFilters().getAndSet {
                it.plus(
                    SubscriptionCategoryFilterSerializer.serialize(
                        SubscriptionCategoryFilter(
                            isChecked = true,
                            categoryId = newId
                        )
                    )
                )
            }
        }
    }

    fun editCategory(
        id: Int,
        name: String,
    ) {
        screenModelScope.launch {
            categoryRepository.updateCategory(
                Category(id, name, Color.Transparent.toArgb()) // TODO color
            )
        }
    }

    fun deleteCategory(
        id: Int,
    ) {
        screenModelScope.launch {
            categoryRepository.deleteCategory(id.toString())
            val filterCategoryIdsPreference = subscriptionPreference.categoryFilters()
            val categoryFilters = filterCategoryIdsPreference.get().map {
                SubscriptionCategoryFilterSerializer.deserialize(it)
            }.toMutableList()
            categoryFilters.removeIf { it.categoryId == id }
            filterCategoryIdsPreference.set(
                categoryFilters
                    .map { SubscriptionCategoryFilterSerializer.serialize(it) }
                    .toSet()
            )
            if (categoryFilters.isEmpty()) {
                subscriptionPreference.includeUncategorizedFilter().set(true)
            }
        }
    }

}