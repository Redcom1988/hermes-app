package dev.redcom1988.hermes.ui.di

import dev.redcom1988.hermes.ui.screen.settings.SettingsPreference
import dev.redcom1988.hermes.ui.screen.subscription.SubscriptionPreference
import dev.redcom1988.hermes.ui.util.ToastHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val uiModule = module {
    single { SettingsPreference(get()) }
    single { SubscriptionPreference(get()) }
    single { ToastHelper(androidContext()) }
}