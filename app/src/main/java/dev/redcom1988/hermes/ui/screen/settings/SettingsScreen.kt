package dev.redcom1988.hermes.ui.screen.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.ui.components.PreferenceScreenVariant
import dev.redcom1988.hermes.ui.components.preference.Preference
import dev.redcom1988.hermes.ui.main.ScreenLayout
import dev.redcom1988.hermes.ui.screen.login.LoginScreen
import dev.redcom1988.hermes.ui.util.collectAsState

object SettingsScreen: Screen {
    @Suppress("unused")
    private fun readResolve(): Any = SettingsScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val settingsPreference by remember { injectLazy<SettingsPreference>() }
        val screenModel = rememberScreenModel { SettingsScreenModel() }
        val state by screenModel.state.collectAsState()
        var showLogoutDialog by remember { mutableStateOf(false) }
        var showWipeRepopulateDialog by remember { mutableStateOf(false) }

        ScreenLayout(
            screen = SettingsScreen,
            title = "Settings",
            isLoading = state.isLoading,
            loadingText = "Loading..."
        ) {
            PreferenceScreenVariant(
                itemsProvider = {
                    listOf(
                        getThemePreferenceGroup(settingsPreference),
                        getAccountPreferenceGroup(
                            onLogoutClick = { showLogoutDialog = true }
                        ),
                        getLocalPreferenceGroup(
                            onWipeClick = { showWipeRepopulateDialog = true }
                        )
                    )
                }
            )
        }

        if (showWipeRepopulateDialog) {
            AlertDialog(
                onDismissRequest = { showWipeRepopulateDialog = false },
                title = { Text("Force Wipe & Repopulate") },
                text = { Text("Are you sure you want to clear all local data and re-download from server? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            screenModel.forcedClearSync()
                            showWipeRepopulateDialog = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWipeRepopulateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            screenModel.logout(
                                onSuccess = {
                                    showLogoutDialog = false
                                    navigator.replaceAll(LoginScreen)
                                },
                                onError = { error ->
                                    showLogoutDialog = false
                                    // Error is already handled in the state
                                }
                            )
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Error handling
        state.errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { screenModel.clearError() },
                title = { Text("Error") },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = { screenModel.clearError() }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    @Composable
    private fun getThemePreferenceGroup(
        settingsPreference: SettingsPreference
    ): Preference.PreferenceGroup {
        val themePref = settingsPreference.appTheme()
        val themeValue = themePref.collectAsState().value

        return Preference.PreferenceGroup(
            title = "Appearance",
            preferenceItems = listOf(
                Preference.PreferenceItem.ListPreference(
                    preference = themePref,
                    title = "App Theme",
                    entries = SettingsPreference.AppTheme.asMap,
                    subtitle = themeValue.label
                )
            )
        )
    }

    @Composable
    private fun getAccountPreferenceGroup(
        onLogoutClick: () -> Unit
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = "Account",
            preferenceItems = listOf(
                Preference.PreferenceItem.TextPreference(
                    title = "Logout",
                    subtitle = "Sign out of your account",
                    onClick = onLogoutClick
                )
            )
        )
    }

    @Composable
    private fun getLocalPreferenceGroup(
        onWipeClick: () -> Unit
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = "Local",
            preferenceItems = listOf(
                Preference.PreferenceItem.TextPreference(
                    title = "Force Wipe & Repopulate",
                    subtitle = "Clear all local data and re-download from server",
                    onClick = onWipeClick
                )
            )
        )
    }
}