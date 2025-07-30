package dev.redcom1988.hermes.ui.screen.notifications

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.ui.components.preference.PreferenceScreen

object NotificationsScreen: Screen {

    @Suppress("unused")
    private fun readResolve(): Any = NotificationsScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        PreferenceScreen(
            title = "Notifications",
            onBackPressed = {
                navigator.pop()
            },
            itemsProvider = {
                listOf(
                    // TODO
                )
            }
        )
    }
}