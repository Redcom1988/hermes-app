package dev.redcom1988.hermes.ui.screen.subscription

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.preference.getAndSet
import dev.redcom1988.hermes.core.util.extension.inject
import dev.redcom1988.hermes.domain.category.model.Category
import dev.redcom1988.hermes.domain.category.repository.CategoryRepository
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionCategoryFilter
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionCategoryFilterSerializer
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionPaymentDate
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionSortType
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionStatus
import dev.redcom1988.hermes.domain.subscription.repository.SubscriptionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubscriptionScreenModel(
    categoryRepository: CategoryRepository = inject(),
    private val subscriptionRepository: SubscriptionRepository = inject(),
    private val subscriptionPreference: SubscriptionPreference = inject(),
): ScreenModel {

    val statusFilter = subscriptionPreference.statusFilter().stateIn(screenModelScope)
    val paymentDateFilter = subscriptionPreference.paymentDateFilter().stateIn(screenModelScope)
    val includeUncategorized = subscriptionPreference.includeUncategorizedFilter().stateIn(screenModelScope)
    private val _categoryFilters = subscriptionPreference.categoryFilters().stateIn(screenModelScope)
    val categoryFilters = _categoryFilters
        .map { it.map { SubscriptionCategoryFilterSerializer.deserialize(it) } }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )
    val selectedCategoryFilterCount = combine(
        categoryFilters,
        includeUncategorized
    ) { filterCategories, includeUncategorized ->
        val includeCategoriesCount = if (includeUncategorized) 1 else 0
        val total = filterCategories.count().plus(1)
        val count = filterCategories.map { it.isChecked }.count { it }.plus(includeCategoriesCount)
        if (total == 1) "" else "($count of $total selected)"
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ""
    )
    val sortAscending = subscriptionPreference.sortAscending().stateIn(screenModelScope)
    val sortType = subscriptionPreference.sortType().stateIn(screenModelScope)

    val categories = categoryRepository.getCategoriesFlow()
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow<String?>(null)
    val searchQuery = _searchQuery.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("UNCHECKED_CAST")
    private val _filteredSubscriptions = combine(
        statusFilter,
        paymentDateFilter,
        categoryFilters,
        sortAscending,
        sortType,
        _searchQuery,
        includeUncategorized,
    ) { data ->
        val status = data[0] as SubscriptionStatus
        val paymentDate = data[1] as SubscriptionPaymentDate
        val categoryFilters = data[2] as List<SubscriptionCategoryFilter>
        val ascending = data[3] as Boolean
        val sortType = data[4] as SubscriptionSortType
        val query = data[5] as String?
        val includeUncategorized = data[6] as Boolean
        subscriptionRepository.getSubscriptionsFlow(
            includeDeleted = false,
            includeUncategorized = includeUncategorized,
            statusFilter = status,
            paymentDateFilter = paymentDate,
            categoryFilters = categoryFilters,
            searchQuery = query,
            sortType = sortType,
            sortAscending = ascending
        )
    }.flatMapLatest { it }

    val filteredSubscriptions = _filteredSubscriptions
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val hasFilters = combine(
        statusFilter,
        paymentDateFilter,
        _categoryFilters,
        includeUncategorized
    ) { status, paymentDate, categories, includeUncategorized ->
        val isStatusFiltered = status != SubscriptionStatus.All
        val isPaymentDateFiltered = paymentDate != SubscriptionPaymentDate.All
        val categoryFilters = categories.map { SubscriptionCategoryFilterSerializer.deserialize(it) }
        val isCategoriesFiltered = !categoryFilters.all { it.isChecked }
        isStatusFiltered || isPaymentDateFiltered || isCategoriesFiltered || !includeUncategorized
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    init {
        screenModelScope.launch {
            filteredSubscriptions.filterNotNull().first()
            _isReady.value = true
        }
        removeSubscriptionCategoriesFilterNotInCategories()
    }

    @Suppress("UNCHECKED_CAST")
    fun applyFilter(
        filter: Any
    ) {
        when(filter) {
            is SubscriptionStatus -> subscriptionPreference.statusFilter().set(filter)
            is SubscriptionPaymentDate -> subscriptionPreference.paymentDateFilter().set(filter)
            is Pair<*, *> -> {
                val isChecked = filter.first as Boolean
                val categoryId = (filter.second as Category).id
                subscriptionPreference.categoryFilters().getAndSet {
                    it
                        .map { SubscriptionCategoryFilterSerializer.deserialize(it) }
                        .map {
                            if (it.categoryId == categoryId) it.copy(isChecked = isChecked)
                            else it
                        }.map { SubscriptionCategoryFilterSerializer.serialize(it) }
                        .toSet()
                }
            }
            is Boolean -> subscriptionPreference.sortAscending().set(filter)
            is SubscriptionSortType -> subscriptionPreference.sortType().set(filter)
        }
    }

    fun clearCategoriesFilter() {
        subscriptionPreference.categoryFilters().getAndSet {
            it
                .map { SubscriptionCategoryFilterSerializer.deserialize(it) }
                .map { it.copy(isChecked = false) }
                .map { SubscriptionCategoryFilterSerializer.serialize(it) }
                .toSet()
        }
    }

    fun setIncludeUncategorized(value: Boolean) {
        subscriptionPreference.includeUncategorizedFilter().set(value)
    }

    fun search(query: String?) {
        _searchQuery.update { query }
    }

    fun markAsPaid(subscriptionId: Int) = screenModelScope.launch {
        subscriptionRepository.markSubscriptionAsPaidById(subscriptionId)
    }

    fun removeSubscriptionCategoriesFilterNotInCategories() {
        screenModelScope.launch {
            val (categories, filterCategories) = combine(
                categories,
                categoryFilters,
            ) { categories, filterCategories ->
                val categories = categories
                val filterCategories = filterCategories
                Pair(categories, filterCategories)
            }.drop(1).first()
            val newFilterCategories = filterCategories.toMutableList()
            newFilterCategories.removeIf { it.categoryId !in categories.map { it.id } }
            subscriptionPreference.categoryFilters().set(
                newFilterCategories.map { SubscriptionCategoryFilterSerializer.serialize(it) }.toSet()
            )
        }
    }

}
