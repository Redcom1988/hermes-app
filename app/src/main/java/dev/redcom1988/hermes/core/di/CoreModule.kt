package dev.redcom1988.hermes.core.di

import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.interceptor.AuthInterceptor
import dev.redcom1988.hermes.core.notification.NotificationHelper
import dev.redcom1988.hermes.core.preference.AndroidPreferenceStore
import dev.redcom1988.hermes.core.preference.PreferenceStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreModule = module {
    single<PreferenceStore> { AndroidPreferenceStore(androidContext()) }
    single<AuthInterceptor> { AuthInterceptor(preferenceStore = get()) }
    single<NetworkHelper> {
        NetworkHelper(
            context = androidContext(),
            isDebugBuild = true,
            authInterceptor = get()
        )
    }
    singleOf(::NotificationHelper)
}