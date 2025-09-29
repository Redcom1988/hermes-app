package dev.redcom1988.hermes.ui.main

import android.app.Application
import android.util.Log
import dev.redcom1988.hermes.core.di.coreModule
import dev.redcom1988.hermes.core.notification.NotificationHelper
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.di.dataModule
import dev.redcom1988.hermes.ui.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            logger(
                object : Logger() {
                    override fun display(level: Level, msg: MESSAGE) {
                        when (level) {
                            Level.DEBUG -> Log.d(null, msg)
                            Level.INFO -> Log.i(null, msg)
                            Level.WARNING -> Log.w(null, msg)
                            Level.ERROR -> Log.e(null, msg)
                            Level.NONE -> Log.v(null, msg)
                        }
                    }
                }
            )
            androidContext(this@MainApplication)
            modules(
                listOf(
                    uiModule,
                    coreModule,
                    dataModule,
                )
            )
        }

        val notificationHelper by injectLazy<NotificationHelper>()
        notificationHelper.createNotificationChannels(
            listOf(
            )
        )
    }

}