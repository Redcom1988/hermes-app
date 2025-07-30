package dev.redcom1988.hermes.ui.screen.subscription.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.domain.category.model.Category
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionCategoryFilter
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionPaymentDate
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionSortType
import dev.redcom1988.hermes.domain.subscription.model.SubscriptionStatus
import dev.redcom1988.hermes.ui.components.LabeledCheckbox
import dev.redcom1988.hermes.ui.components.LabeledCheckboxData
import dev.redcom1988.hermes.ui.screen.category.CategoryScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class SubscriptionBottomSheetTab(val label: String) {
    Filter("Filter"),
    Sort("Sort")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreenBottomSheet(
    filterCategoriesSelectedText: String,
    showBottomSheet: MutableState<Boolean>,
    sheetState: SheetState,
    includeUncategorized: Boolean,
    selectedSubscriptionStatus: SubscriptionStatus,
    selectedSubscriptionPaymentDate: SubscriptionPaymentDate,
    categoryFilters: List<SubscriptionCategoryFilter>,
    sortAscending: Boolean,
    sortType: SubscriptionSortType,
    categories: List<Category>,
    onApplySubscriptionStatus: (SubscriptionStatus) -> Unit,
    onApplySubscriptionPaymentDate: (SubscriptionPaymentDate) -> Unit,
    onApplyCategory: (Boolean, Category) -> Unit,
    onToggleSortAscending: () -> Unit,
    onChangeSortType: (SubscriptionSortType) -> Unit,
    onClearCategoriesFilter: () -> Unit,
    onIncludeUncategorizedChanged: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow
    val pagerState = rememberPagerState { SubscriptionBottomSheetTab.entries.count() }
    var selectedTab by rememberSaveable { mutableIntStateOf(SubscriptionBottomSheetTab.Filter.ordinal) }
    var categoriesFilterExpanded by rememberSaveable { mutableStateOf(true) }

    if (showBottomSheet.value) {
        ModalBottomSheet(
            modifier = Modifier.statusBarsPadding(),
            sheetState = sheetState,
            dragHandle = null,
            onDismissRequest = { showBottomSheet.value = false }
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
            ) {
                SubscriptionBottomSheetTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            scope.launch { pagerState.animateScrollToPage(selectedTab) }
                        },
                        text = {
                            Text(
                                text = tab.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
            HorizontalPager(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                state = pagerState,
            ) {
                when(it) {
                    0 -> {
                        FilterBottomSheet(
                            filterCategoriesSelectedText = filterCategoriesSelectedText,
                            categoriesFilterExpanded = categoriesFilterExpanded,
                            includeUncategorized = includeUncategorized,
                            selectedSubscriptionStatus = selectedSubscriptionStatus,
                            selectedSubscriptionPaymentDate = selectedSubscriptionPaymentDate,
                            categoryFilters = categoryFilters,
                            categoryOptions = categories,
                            onApplySubscriptionStatus = onApplySubscriptionStatus,
                            onApplySubscriptionPaymentDate = onApplySubscriptionPaymentDate,
                            onApplyCategory = onApplyCategory,
                            onHideBottomSheet = {
                                hideBottomSheet(
                                    scope = scope,
                                    showBottomSheet = showBottomSheet,
                                    sheetState = sheetState
                                ) {
                                    navigator.push(CategoryScreen)
                                }
                            },
                            onClearCategoriesFilter = onClearCategoriesFilter,
                            onExpand = {
                                categoriesFilterExpanded = !categoriesFilterExpanded
                            },
                            onIncludeUncategorizedChanged = onIncludeUncategorizedChanged
                        )
                    }
                    1 -> {
                        SortBottomSheet(
                            sortAscending = sortAscending,
                            sortType = sortType,
                            onToggleSortAscending = onToggleSortAscending,
                            onChangeSortType = onChangeSortType
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SortBottomSheet(
    sortAscending: Boolean,
    sortType: SubscriptionSortType,
    onToggleSortAscending: () -> Unit,
    onChangeSortType: (SubscriptionSortType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        SubscriptionSortType.entries.forEach { type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (sortType == type) onToggleSortAscending()
                        else onChangeSortType(type)
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(24.dp)
                ) {
                    if (sortType == type) {
                        Icon(
                            imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun FilterBottomSheet(
    filterCategoriesSelectedText: String,
    categoriesFilterExpanded: Boolean,
    includeUncategorized: Boolean,
    selectedSubscriptionStatus: SubscriptionStatus,
    selectedSubscriptionPaymentDate: SubscriptionPaymentDate,
    categoryFilters: List<SubscriptionCategoryFilter>,
    categoryOptions: List<Category>,
    onApplySubscriptionStatus: (SubscriptionStatus) -> Unit,
    onApplySubscriptionPaymentDate: (SubscriptionPaymentDate) -> Unit,
    onApplyCategory: (Boolean, Category) -> Unit,
    onHideBottomSheet: () -> Unit,
    onClearCategoriesFilter: () -> Unit,
    onExpand: () -> Unit,
    onIncludeUncategorizedChanged: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Subscription Status Section
        Text(
            text = "Subscription Status",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SubscriptionStatus.entries.forEach { status ->
                FilterChip(
                    label = { Text(status.label) },
                    selected = selectedSubscriptionStatus == status,
                    onClick = {
                        onApplySubscriptionStatus(status)
                    },
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Payment Date",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SubscriptionPaymentDate.entries.forEach { paymentDate ->
                FilterChip(
                    label = { Text(paymentDate.label) },
                    selected = selectedSubscriptionPaymentDate == paymentDate,
                    onClick = {
                        onApplySubscriptionPaymentDate(paymentDate)
                    },
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpand() }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = filterCategoriesSelectedText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Icon(
                imageVector = if (categoriesFilterExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
            )
        }

        AnimatedVisibility(
            visible = categoriesFilterExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            if (categoryOptions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    categoryOptions.forEach { category ->
                        LabeledCheckbox(
                            data = LabeledCheckboxData(
                                label = category.name,
                                checked = categoryFilters.first { it.categoryId == category.id }.isChecked,
                                onCheckedChange = { isChecked ->
                                    onApplyCategory(isChecked, category)
                                }
                            )
                        )
                    }
                    LabeledCheckbox(
                        data = LabeledCheckboxData(
                            label = "Uncategorized",
                            checked = includeUncategorized,
                            onCheckedChange = onIncludeUncategorizedChanged
                        )
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No Categories found")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onHideBottomSheet,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Categories")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun hideBottomSheet(
    scope: CoroutineScope,
    showBottomSheet: MutableState<Boolean>,
    sheetState: SheetState,
    onComplete: () -> Unit = {},
) {
    scope.launch { sheetState.hide() }.invokeOnCompletion {
        showBottomSheet.value = false
        onComplete()
    }
}