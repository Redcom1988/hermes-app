package dev.redcom1988.hermes.ui.screen.category

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.ui.components.AppBar
import dev.redcom1988.hermes.ui.util.PlaceholderTransformation
import kotlinx.coroutines.launch

sealed interface CategoryScreenAction {
    data object Add: CategoryScreenAction
    data class Edit(
        val id: Int,
        val name: String,
    ): CategoryScreenAction
    data object None: CategoryScreenAction
    data class Delete(
        val id: Int,
        val name: String,
    ): CategoryScreenAction
}

object CategoryScreen: Screen {
    private fun readResolve(): Any = CategoryScreen

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val lazyListState = rememberLazyListState()
        val screenModel = rememberScreenModel { CategoryScreenModel() }
        val focusRequester = remember { FocusRequester() }
        var isDialogShown by remember { mutableStateOf(false) }
        var isDeleteDialogShown by remember { mutableStateOf(false) }
        var fabHeight by remember { mutableStateOf(0.dp) }
        var categoryAction = rememberSaveable(stateSaver = CategoryScreenActionSaver) {
            mutableStateOf(CategoryScreenAction.None)
        }.value
        val categories by screenModel.categories.collectAsState()

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                Surface(
                    shadowElevation = 8.dp
                ) {
                    AppBar(
                        title = "Edit Categories",
                        navigateUp = { navigator.pop() }
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
                        categoryAction = CategoryScreenAction.Add
                        isDialogShown = true
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

            if (categories.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = lazyListState,
                    contentPadding = PaddingValues(
                        top = contentPadding.calculateTopPadding().plus(16.dp),
                        bottom = contentPadding.calculateBottomPadding().plus(fabHeight).plus(16.dp),
                        start = contentPadding.calculateStartPadding(layoutDirection).plus(16.dp),
                        end = contentPadding.calculateEndPadding(layoutDirection).plus(16.dp)
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(
                        items = categories,
                        key = { it.id }
                    ) { category ->
                        OutlinedCard {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = category.name,
                                )
                                IconButton(
                                    onClick = {
                                        categoryAction = CategoryScreenAction.Edit(
                                            id = category.id,
                                            name = category.name
                                        )
                                        isDialogShown = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        categoryAction = CategoryScreenAction.Delete(
                                            id = category.id,
                                            name = category.name
                                        )
                                        isDeleteDialogShown = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                    )
                                }
                            }
                        }
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
                        Text("No Category found")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = {
                                categoryAction = CategoryScreenAction.Add
                                isDialogShown = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Category")
                        }
                    }
                }
            }

            if (isDeleteDialogShown) {
                val action = categoryAction
                val onDismissRequest = {
                    categoryAction = CategoryScreenAction.None
                    isDeleteDialogShown = false
                }
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    title = {
                        Text("Delete Category")
                    },
                    text = {
                        Text(
                            text = when(action) {
                                is CategoryScreenAction.Delete -> "Do you wish to delete category \"${action.name}\"?"
                                else -> ""
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                when(action) {
                                    is CategoryScreenAction.Delete -> screenModel.deleteCategory(action.id)
                                    else -> Unit
                                }
                                onDismissRequest()
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                onDismissRequest()
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (isDialogShown) {
                val scope = rememberCoroutineScope()
                val onDismissRequest = {
                    categoryAction = CategoryScreenAction.None
                    isDialogShown = false
                }
                val action = categoryAction
                var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                    mutableStateOf(
                        TextFieldValue(
                            when(action) {
                                is CategoryScreenAction.Edit -> action.name
                                else -> ""
                            }
                        )
                    )
                }
                val title = when(action) {
                    is CategoryScreenAction.Add -> "Add Category"
                    is CategoryScreenAction.Edit -> "Edit Category"
                    else -> ""
                }

                fun handleConfirm() {
                    scope.launch {
                        when(action) {
                            is CategoryScreenAction.Add -> screenModel.addCategory(textFieldValue.text)
                            is CategoryScreenAction.Edit -> screenModel.editCategory(action.id, textFieldValue.text)
                            else -> Unit
                        }
                    }
                    onDismissRequest()
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    title = { Text(text = title) },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = textFieldValue,
                                onValueChange = { textFieldValue = it },
                                trailingIcon = {
                                    if (textFieldValue.text.isNotBlank()) {
                                        IconButton(onClick = { textFieldValue = TextFieldValue("") }) {
                                            Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                                        }
                                    }
                                },
                                label = {
                                    Text("Name")
                                },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    color = when {
                                        textFieldValue.text.isEmpty() -> OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor
                                        else -> LocalTextStyle.current.color
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                visualTransformation = if (textFieldValue.text.isEmpty())
                                    PlaceholderTransformation("Enter Category Name")
                                else VisualTransformation.None
                            )
                        }
                    },
                    properties = DialogProperties(
                        usePlatformDefaultWidth = true,
                    ),
                    confirmButton = {
                        TextButton(
                            enabled = textFieldValue.text.isNotBlank(),
                            onClick = { handleConfirm() },
                        ) {
                            Text(text = "OK") // TODO copy
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onDismissRequest) {
                            Text(text = "Cancel") // TODO copy
                        }
                    },
                )
            }

        }
    }
}

val CategoryScreenActionSaver: Saver<CategoryScreenAction, Any> = Saver(
    save = { action ->
        when (action) {
            is CategoryScreenAction.None -> listOf("None")
            is CategoryScreenAction.Add -> listOf("Add")
            is CategoryScreenAction.Edit -> listOf("Edit", action.id, action.name)
            is CategoryScreenAction.Delete -> listOf("Delete", action.id, action.name)
        }
    },
    restore = { saved ->
        val list = saved as? List<*>
        when (list?.getOrNull(0) as? String) {
            "None" -> CategoryScreenAction.None
            "Add" -> CategoryScreenAction.Add
            "Edit" -> {
                val id = (list.getOrNull(1) as? Int) ?: return@Saver CategoryScreenAction.None
                val name = list.getOrNull(2) as? String ?: return@Saver CategoryScreenAction.None
                CategoryScreenAction.Edit(id, name)
            }
            "Delete" -> {
                val id = (list.getOrNull(1) as? Int) ?: return@Saver CategoryScreenAction.None
                val name = list.getOrNull(2) as? String ?: return@Saver CategoryScreenAction.None
                CategoryScreenAction.Delete(id, name)
            }
            else -> CategoryScreenAction.None
        }
    }
)
