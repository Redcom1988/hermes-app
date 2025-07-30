package dev.redcom1988.hermes.core.di

import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.notification.NotificationHelper
import dev.redcom1988.hermes.core.preference.AndroidPreferenceStore
import dev.redcom1988.hermes.core.preference.PreferenceStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreModule = module {
    single<PreferenceStore> { AndroidPreferenceStore(androidContext()) }
    single<NetworkHelper> {
        NetworkHelper(
            context = androidContext(),
            isDebugBuild = true
        )
    }
    singleOf(::NotificationHelper)
}