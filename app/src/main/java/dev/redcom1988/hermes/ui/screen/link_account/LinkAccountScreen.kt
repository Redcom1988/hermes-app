package dev.redcom1988.hermes.ui.screen.link_account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.ui.components.preference.Preference
import dev.redcom1988.hermes.ui.components.preference.PreferenceScreen
import dev.redcom1988.hermes.ui.theme.icons.Google

object LinkAccountScreen: Screen {

    @Suppress("unused")
    private fun readResolve(): Any = LinkAccountScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { LinkAccountScreenModel() }
        val state = screenModel.state.collectAsState().value

        PreferenceScreen(
            title = "Link Account",
            onBackPressed = { navigator.pop() },
            itemsProvider = {
                listOf(
                    getLinkAccountGroup(
                        state = state,
                        onLinkWithBasic = { email, password ->
                            screenModel.linkAccount(
                                LinkAccountMethod.Basic(email, password)
                            )
                        },
                        onUnlinkWithBasic = {
                            screenModel.unlinkAccount(
                                LinkAccountMethod.Basic("","")
                            )
                        },
                        onLinkWithGoogle = {
                            screenModel.linkAccount(LinkAccountMethod.Google)
                        },
                        onUnlinkWithGoogle = {
                            screenModel.unlinkAccount(LinkAccountMethod.Google)
                        }
                    )
                )
            }
        )
    }

    @Composable
    private fun getLinkAccountGroup(
        state: LinkAccountScreenState,
        onLinkWithBasic: (String, String) -> Unit,
        onUnlinkWithBasic: () -> Unit,
        onLinkWithGoogle: () -> Unit,
        onUnlinkWithGoogle: () -> Unit,
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = "Choose link method",
            preferenceItems = listOf(
                Preference.PreferenceItem.CustomPreference {
                    EmailAndPasswordLinkAccountRow(
                        state = state.basicLinkState,
                        errorMessage = state.basicLinkState.errorMessage,
                        icon = Icons.Default.Email,
                        onLink = onLinkWithBasic,
                        onUnlink = onUnlinkWithBasic
                    )
                },
                Preference.PreferenceItem.CustomPreference {
                    LinkAccountRow(
                        state = state.googleLinkState,
                        text = "Google",
                        icon = Google,
                        onLink = onLinkWithGoogle,
                        onUnlink = onUnlinkWithGoogle,
                    )
                },
            )
        )
    }

    @Composable
    private fun LinkAccountRow(
        state: LinkAccountScreenState.LinkState,
        text: String,
        icon: ImageVector,
        onLink: () -> Unit,
        onUnlink: () -> Unit,
    ) {
        val (actionText, action) = when {
            state.linked -> "Unlink" to onUnlink
            !state.linked -> "Link" to onLink
            else -> "" to null
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(!state.loading) {
                    action?.invoke()
                }
                .padding(
                    horizontal = 24.dp,
                    vertical = 8.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(start = 1.dp).size(20.dp),
                imageVector = icon,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = text,
            )
            Spacer(modifier = Modifier.width(16.dp))
            action?.let {
                if (state.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    TextButton(
                        onClick = { it.invoke() }
                    ) {
                        Text(
                            text = actionText
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun EmailAndPasswordLinkAccountRow(
        state: LinkAccountScreenState.LinkState,
        errorMessage: String?,
        icon: ImageVector,
        onLink: (String, String) -> Unit,
        onUnlink: () -> Unit,
    ) {
        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = {
                    if (!state.loading) {
                        showDialog = false
                    }
                },
                title = {
                    Text("Link Account")
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = email,
                            maxLines = 1,
                            readOnly = state.loading,
                            label = {
                                Text(
                                    text = "Email"
                                )
                            },
                            onValueChange = {
                                email = it
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            maxLines = 1,
                            readOnly = state.loading,
                            label = {
                                Text(
                                    text = "Password"
                                )
                            },
                            onValueChange = {
                                password = it
                            }
                        )
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = !state.loading && (email.isNotBlank() && password.isNotBlank()),
                        onClick = { onLink(email, password) },
                    ) {
                        when {
                            state.loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            else -> {
                                Text("OK")
                            }
                        }
                    }
                },
            )
        }

        LinkAccountRow(
            state = state,
            text = "Email & Password",
            icon = icon,
            onLink = { showDialog = true },
            onUnlink = onUnlink
        )
    }

}