package dev.redcom1988.hermes.ui.screen.choose_currency

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.ui.components.ScrollbarLazyColumn
import dev.redcom1988.hermes.ui.components.SearchToolbar
import dev.redcom1988.hermes.ui.util.popWithResult
import java.util.Currency

const val CHOOSE_CURRENCY_KEY = "CHOOSE_CURRENCY_KEY"

data class ChooseCurrencyScreen(
    val currencyCode: String,
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var searchQuery by remember { mutableStateOf<String?>(null) }
        val lazyListState = rememberLazyListState()

        val searchEntries by remember {
            derivedStateOf {
                val currencyList = Currency.getAvailableCurrencies().map { it.currencyCode }
                val (current, others) = currencyList.partition { it == currencyCode }
                val sortedOthers = others.sorted()
                val sortedCurrencyList = current + sortedOthers
                val entries = sortedCurrencyList.associateWith {
                    "($it) ${Currency.getInstance(it).displayName}"
                }
                entries.filter {
                    if (searchQuery != null && searchQuery!!.isNotEmpty()) {
                        it.value.lowercase().contains(searchQuery!!.lowercase())
                    } else true
                }
            }
        }

        fun handleBack(key: String? = null) {
            navigator.popWithResult(
                CHOOSE_CURRENCY_KEY to when {
                    key != null -> key
                    else -> currencyCode
                }
            )
        }

        BackHandler { handleBack() }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Surface(
                    shadowElevation = 8.dp,
                ) {
                    SearchToolbar(
                        searchQuery = searchQuery,
                        onChangeSearchQuery = { searchQuery = it },
                        titleContent = {
                            Text("Choose Currency")
                        },
                        navigateUp = { handleBack() },
                    )
                }
            }
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ) {
                if (searchEntries.isEmpty()) {
                    Text(
                        text = "No entries found",
                        style = MaterialTheme.typography.bodyLarge.merge(),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(vertical = 48.dp),
                    )
                } else {
                    ScrollbarLazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            bottom = 8.dp,
                            top = 8.dp,
                        )
                    ) {
                        searchEntries.forEach { current ->
                            val isSelected = currencyCode == current.key
                            item {
                                ChooseCurrencyRow(
                                    label = current.value,
                                    isSelected = isSelected,
                                    onSelected = {
                                        handleBack(current.key)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun ChooseCurrencyRow(
    label: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected = isSelected,
                onClick = { if (!isSelected) onSelected() },
            )
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .padding(end = 16.dp),
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.merge(),
            modifier = Modifier.padding(start = 24.dp),
        )
    }
}