package dev.redcom1988.hermes.ui.screen.choose_logo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.AllIcons
import compose.icons.fontawesomeicons.Brands
import dev.redcom1988.hermes.ui.components.ScrollbarLazyVerticalGrid
import dev.redcom1988.hermes.ui.components.SearchToolbar
import dev.redcom1988.hermes.ui.util.popWithResult
import dev.redcom1988.hermes.ui.util.splitCamelCase

const val CHOOSE_LOGO_KEY = "CHOOSE_LOGO_KEY"

data class ChooseLogoScreen(
    val logo: String? = null,
): Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val columns = 3
        val navigator = LocalNavigator.currentOrThrow
        var searchQuery by remember { mutableStateOf<String?>(null) }

        val searchEntries by remember {
            derivedStateOf {
                val logoList = FontAwesomeIcons.Brands.AllIcons.sortedBy { it.name }
                val entries = logoList.associateWith { it.name }
                entries.filter {
                    if (searchQuery != null && searchQuery!!.isNotEmpty()) {
                        it.value.lowercase().contains(searchQuery!!.lowercase())
                    } else true
                }
            }
        }

        fun handleBack(image: ImageVector? = null) {
            navigator.popWithResult(
                CHOOSE_LOGO_KEY to when {
                    image != null -> image.name
                    else -> logo
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
                            Text("Choose Logo")
                        },
                        navigateUp = { handleBack() }
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
                    ScrollbarLazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(16.dp),
                        positionOffset = 0f
                    ) {
                        searchEntries.forEach { (icon, name) ->
                            item {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            color = when {
                                                logo != name -> MaterialTheme.colorScheme.surfaceContainer
                                                else -> MaterialTheme.colorScheme.primaryContainer
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { handleBack(icon) }
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Icon(
                                        modifier = Modifier.size(36.dp),
                                        imageVector = icon,
                                        contentDescription = null,
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = name.splitCamelCase(),
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
