package dev.redcom1988.hermes.ui.screen.settings

import dev.redcom1988.hermes.core.preference.PreferenceStore
import dev.redcom1988.hermes.core.preference.getEnum

class SettingsPreference(
    private val preferenceStore: PreferenceStore
) {
    fun appTheme() = preferenceStore.getEnum(
        key = "app_theme",
        defaultValue = AppTheme.SYSTEM
    )

    enum class AppTheme(val label: String) {
        SYSTEM("System"),
        LIGHT("Light"),
        DARK("Dark");

        companion object {
            val asMap = entries.associateWith { it.label }
        }

        override fun toString(): String = label
    }
}