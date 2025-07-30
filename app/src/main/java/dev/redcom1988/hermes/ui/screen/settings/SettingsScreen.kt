package dev.redcom1988.hermes.ui.screen.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.core.util.extension.inject
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.category.repository.CategoryRepository
import dev.redcom1988.hermes.ui.components.ResultScreen
import dev.redcom1988.hermes.ui.components.preference.Preference
import dev.redcom1988.hermes.ui.components.preference.PreferenceScreen
import dev.redcom1988.hermes.ui.screen.category.CategoryScreen
import dev.redcom1988.hermes.ui.screen.choose_currency.CHOOSE_CURRENCY_KEY
import dev.redcom1988.hermes.ui.screen.choose_currency.ChooseCurrencyScreen
import dev.redcom1988.hermes.ui.util.collectAsState
import java.util.Currency

object SettingsScreen: ResultScreen() {

    @Suppress("unused")
    private fun readResolve(): Any = SettingsScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(arguments: Map<String, Any?>) {
        val navigator = LocalNavigator.currentOrThrow
        val settingsPreference by remember { injectLazy<SettingsPreference>() }
        val currencyArg = arguments[CHOOSE_CURRENCY_KEY] as? String
        val selectedCurrency = when {
            currencyArg != null -> Currency.getInstance(currencyArg)
            else -> null
        }

        PreferenceScreen(
            title = "Settings",
            onBackPressed = { navigator.pop() },
            itemsProvider = {
                listOf(
                    getCategoriesGroup(
                        onNavigateToCategoryScreen = { navigator.push(CategoryScreen) }
                    ),
                    getLocaleGroup(
                        settingsPreference = settingsPreference,
                        selectedCurrency = selectedCurrency,
                        onNavigateToChooseCurrencyScreen = {
                            navigator.push(ChooseCurrencyScreen(it))
                        }
                    ),
                    getNotificationGroup(settingsPreference),
                )
            }
        )
    }

    @Composable
    private fun getCategoriesGroup(
        onNavigateToCategoryScreen: () -> Unit,
    ): Preference.PreferenceGroup {
        val categoryCount = inject<CategoryRepository>()
            .getCategoriesFlow()
            .collectAsState(emptyList())
            .value.count()
        return Preference.PreferenceGroup(
            title = "Categories",
            preferenceItems = listOf(
                Preference.PreferenceItem.TextPreference(
                    title = "Edit Categories",
                    subtitle = "$categoryCount categories",
                    onClick = onNavigateToCategoryScreen,
                )
            )
        )
    }

    @Composable
    private fun getLocaleGroup(
        settingsPreference: SettingsPreference,
        selectedCurrency: Currency?,
        onNavigateToChooseCurrencyScreen: (String) -> Unit,
    ): Preference.PreferenceGroup {
        val defaultCurrencyPreference = settingsPreference.defaultCurrencyCode()
        selectedCurrency?.let { defaultCurrencyPreference.set(selectedCurrency.currencyCode) }
        val defaultCurrency by defaultCurrencyPreference.collectAsState()
        val currency = Currency.getInstance(defaultCurrency)
        return Preference.PreferenceGroup(
            title = "Locale",
            preferenceItems = listOf(
                Preference.PreferenceItem.TextPreference(
                    title = "Default Currency",
                    subtitle = "(${currency.currencyCode}) ${currency.displayName}",
                    onClick = { onNavigateToChooseCurrencyScreen(defaultCurrency) }
                ),
            )
        )
    }

    @Composable
    private fun getNotificationGroup(
        settingsPreference: SettingsPreference
    ): Preference.PreferenceGroup {
        val notifyPushPreference = settingsPreference.defaultNotificationNotifyPush()
        val notificationOffsetPreference = settingsPreference.defaultNotificationOffset()
        val notificationOffset by notificationOffsetPreference.collectAsState()
        return Preference.PreferenceGroup(
            title = "Notifications",
            preferenceItems = listOf(
                Preference.PreferenceItem.SwitchPreference(
                    preference = notifyPushPreference,
                    title = "Notify via Push Notifications",
//                    subtitle = "Applies to new subscriptions by default"
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = notificationOffsetPreference,
                    entries = NotificationOffset.entries.associateWith { it.label },
                    title = "Send Reminder",
                    subtitle = notificationOffset.label,
                )
            )
        )
    }

}