package dev.redcom1988.hermes.ui.screen.subscription

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.ui.components.AppBar
import dev.redcom1988.hermes.ui.components.AppBarActions
import dev.redcom1988.hermes.ui.components.ScrollbarLazyColumn
import dev.redcom1988.hermes.ui.components.SearchToolbar
import dev.redcom1988.hermes.ui.screen.category.CategoryScreen
import dev.redcom1988.hermes.ui.screen.form.SubscriptionFormScreen
import dev.redcom1988.hermes.ui.screen.form.SubscriptionFormScreenType
import dev.redcom1988.hermes.ui.screen.notifications.NotificationsScreen
import dev.redcom1988.hermes.ui.screen.settings.SettingsScreen
import dev.redcom1988.hermes.ui.screen.subscription.components.SubscriptionScreenBottomSheet
import dev.redcom1988.hermes.ui.screen.subscription.components.SubscriptionScreenSubscriptionItem
import dev.redcom1988.hermes.ui.screen.subscription.notifier.SubscriptionNotifier
import dev.redcom1988.hermes.ui.theme.active

data class SubscriptionScreen(
    var subscriptionIdToMarkAsPaid: Int? = null
): Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val layoutDirection = LocalLayoutDirection.current
        val density = LocalDensity.current
        val lazyListState = rememberLazyListState()
        val sheetState = rememberModalBottomSheetState(true)
        val showBottomSheet = remember { mutableStateOf(false) }
        var fabHeight by remember { mutableStateOf(0.dp) }

        val screenModel = rememberScreenModel { SubscriptionScreenModel() }
        val categories by screenModel.categories.collectAsState()
        val filteredSubscriptions = screenModel.filteredSubscriptions.collectAsState().value
        val searchQuery by screenModel.searchQuery.collectAsState()
        val statusFilter by screenModel.statusFilter.collectAsState()
        val paymentDateFilter by screenModel.paymentDateFilter.collectAsState()
        val categoryFilters by screenModel.categoryFilters.collectAsState()
        val sortAscending by screenModel.sortAscending.collectAsState()
        val sortType by screenModel.sortType.collectAsState()
        val includeUncategorized by screenModel.includeUncategorized.collectAsState()
        val isReady by screenModel.isReady.collectAsState()
        val hasFilters by screenModel.hasFilters.collectAsState()
        val selectedCategoryFilterCount by screenModel.selectedCategoryFilterCount.collectAsState()

        LaunchedEffect(subscriptionIdToMarkAsPaid) {
            val subscriptionId = subscriptionIdToMarkAsPaid
            if (subscriptionId != null) {
                screenModel.markAsPaid(subscriptionId)
                subscriptionIdToMarkAsPaid = null
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                Surface(
                    shadowElevation = 8.dp,
                ) {
                    SearchToolbar(
                        titleContent = {
                            Text(
                                text = "Subscriptions",
                            )
                        },
                        searchQuery = searchQuery,
                        onChangeSearchQuery = {
                            screenModel.search(it)
                        },
                        placeholderText = "Search...",
                        actions = {
                            AppBarActions(
                                actions = listOf(
                                    AppBar.Action(
                                        title = "Filter",
                                        icon = Icons.Default.FilterList,
                                        iconTint = when {
                                            hasFilters -> MaterialTheme.colorScheme.active
                                            else -> null
                                        },
                                        onClick = {
                                            showBottomSheet.value = true
                                        }
                                    ),
                                    AppBar.OverflowAction(
                                        title = "Notifications",
                                        icon = Icons.Default.Notifications,
                                        onClick = {
                                            navigator.push(NotificationsScreen)
                                        }
                                    ),
                                    AppBar.OverflowAction(
                                        title = "Reports",
                                        icon = Icons.Default.BarChart,
                                        onClick = {
                                            // TODO
                                        }
                                    ),
                                    AppBar.OverflowAction(
                                        title = "Categories",
                                        icon = Icons.Default.Category,
                                        onClick = {
                                            navigator.push(CategoryScreen)
                                        }
                                    ),
                                    AppBar.OverflowAction(
                                        title = "Settings",
                                        icon = Icons.Default.Settings,
                                        onClick = {
                                            navigator.push(SettingsScreen)
                                        }
                                    )
                                )
                            )
                        },
                    )
                }
            },
            floatingActionButton = {
                val expanded = !lazyListState.canScrollBackward
                FloatingActionButton(
                    modifier = Modifier.onSizeChanged {
                        fabHeight = with(density) { it.height.toDp().plus(16.dp) }
                    },
                    onClick = {
                        navigator.push(SubscriptionFormScreen(SubscriptionFormScreenType.Add))
                    },
                ) {
                    val startPadding = if (expanded) 16.dp else 12.dp
                    val endPadding = if (expanded) 20.dp else 0.dp
                    Row(
                        modifier =
                            Modifier
                                .animateContentSize()
                                .sizeIn(
                                    minWidth =
                                        if (expanded) 80.dp
                                        else 56.dp
                                )
                                .padding(start = startPadding, end = endPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (expanded) Arrangement.Start else Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                        Row(Modifier.clearAndSetSemantics {}) {
                            Spacer(Modifier.width(12.dp))
                            if (!lazyListState.canScrollBackward) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        ) { contentPadding ->
            SubscriptionScreenBottomSheet(
                filterCategoriesSelectedText = selectedCategoryFilterCount,
                showBottomSheet = showBottomSheet,
                sheetState = sheetState,
                includeUncategorized = includeUncategorized,
                categories = categories,
                selectedSubscriptionStatus = statusFilter,
                selectedSubscriptionPaymentDate = paymentDateFilter,
                categoryFilters = categoryFilters,
                sortAscending = sortAscending,
                sortType = sortType,
                onApplySubscriptionStatus = { status ->
                    screenModel.applyFilter(status)
                },
                onApplySubscriptionPaymentDate = { paymentDate ->
                    screenModel.applyFilter(paymentDate)
                },
                onApplyCategory = { isChecked, category ->
                    screenModel.applyFilter(Pair(isChecked, category))
                },
                onToggleSortAscending = {
                    screenModel.applyFilter(!sortAscending)
                },
                onChangeSortType = { type ->
                    screenModel.applyFilter(type)
                },
                onClearCategoriesFilter = {
                    screenModel.clearCategoriesFilter()
                },
                onIncludeUncategorizedChanged = {
                    screenModel.setIncludeUncategorized(it)
                }
            )

            if (!isReady) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (filteredSubscriptions?.isNotEmpty() == true) {
                    ScrollbarLazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = lazyListState,
                        contentPadding = PaddingValues(
                            top = contentPadding.calculateTopPadding().plus(16.dp),
                            bottom = contentPadding.calculateBottomPadding().plus(16.dp).plus(fabHeight),
                            start = contentPadding.calculateStartPadding(layoutDirection).plus(16.dp),
                            end = contentPadding.calculateEndPadding(layoutDirection).plus(16.dp)
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        positionOffset = 0f
                    ) {
                        items(
                            items = filteredSubscriptions,
                            key = { it.id }
                        ) { subscription ->
                            SubscriptionScreenSubscriptionItem(
                                modifier = Modifier.animateItem(),
                                subscription = subscription,
                                onClick = {
                                    navigator.push(SubscriptionFormScreen(SubscriptionFormScreenType.Edit(subscription)))
                                },
                                onMarkAsPaid = {
                                    // TODO remove testing
                                    SubscriptionNotifier.notify(context, subscription)
                                }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("No Subscriptions found")
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    navigator.push(SubscriptionFormScreen(SubscriptionFormScreenType.Add))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Subscription")
                            }
                        }
                    }
                }
            }
        }

    }
}