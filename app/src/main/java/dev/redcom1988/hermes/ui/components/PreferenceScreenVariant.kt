package dev.redcom1988.hermes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import dev.redcom1988.hermes.ui.components.preference.Preference
import dev.redcom1988.hermes.ui.components.preference.PreferenceItem
import dev.redcom1988.hermes.ui.components.preference.widget.PreferenceGroupHeader
import dev.redcom1988.hermes.ui.util.onClickInput

@Composable
fun PreferenceScreenVariant(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    itemsProvider: @Composable () -> List<Preference>,
    bottomBar: @Composable () -> Unit = {},
) {
    val items = itemsProvider()
    val lazyListState = rememberLazyListState()

    Box {
        Column(modifier = modifier.fillMaxSize()) {
            ScrollbarLazyColumn(
                modifier = Modifier.weight(1f),
                state = lazyListState,
            ) {
                items.fastForEachIndexed { i, preference ->
                    when (preference) {
                        is Preference.PreferenceGroup -> {
                            item {
                                Column {
                                    PreferenceGroupHeader(
                                        title = preference.title,
                                        visible = preference.visible
                                    )
                                }
                            }
                            if (!preference.visible) return@fastForEachIndexed
                            items(preference.preferenceItems) { item ->
                                PreferenceItem(
                                    item = item,
                                    highlightKey = null,
                                )
                            }
                            item {
                                if (i < items.lastIndex) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }

                        is Preference.PreferenceItem<*> -> item {
                            PreferenceItem(
                                item = preference,
                                highlightKey = null,
                            )
                        }
                    }
                }
            }

            bottomBar()
        }

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .onClickInput(
                        pass = PointerEventPass.Initial,
                        ripple = false,
                        onUp = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


private fun List<Preference>.findHighlightedIndex(highlightKey: String): Int {
    return flatMap {
        if (it is Preference.PreferenceGroup) {
            buildList<String?> {
                add(null) // Header
                addAll(it.preferenceItems.map { groupItem -> groupItem.title })
                add(null) // Spacer
            }
        } else {
            listOf(it.title)
        }
    }.indexOfFirst { it == highlightKey }
}
