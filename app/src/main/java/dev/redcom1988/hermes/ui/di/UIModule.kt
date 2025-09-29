package dev.redcom1988.hermes.ui.di

import dev.redcom1988.hermes.data.local.auth.AuthPreference
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.ui.screen.settings.SettingsPreference
import dev.redcom1988.hermes.ui.util.ToastHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val uiModule = module {
    single { AuthPreference(get()) }
    single { UserPreference(get()) }
    single { SettingsPreference(get()) }
    single { ToastHelper(androidContext()) }
}